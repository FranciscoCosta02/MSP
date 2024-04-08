package resources;

import com.google.cloud.datastore.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/*
    Resource related with all posting and main feed
 */
@Path("/soc")
public class SocialMediaResource {


    private static final Logger LOG = Logger.getLogger(SocialMediaResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory feedPostKeyFactory = datastore.newKeyFactory().setKind("FeedPost");
    private final Gson g = new Gson();
    private static final Storage storage = StorageOptions.getDefaultInstance().getService();

    private final String bucketName = "feedphotos";

    // debug
    //String sender = "REPLACEHERE";




    public SocialMediaResource() {
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postFeedPost(@Context HttpServletRequest request, FeedPostData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String sender = (String) values.get("username");



        data = new FeedPostData(data.title, data.text, data.photo);

        Transaction txn = datastore.newTransaction();

        Entity processedPostData = null;

        try {

            Key postKey = feedPostKeyFactory.newKey(data.generateId(sender));
            Entity feedPost = txn.get(postKey);

            // if already exists
            if(feedPost != null){
                txn.rollback();
                return Response.ok().entity("No spam").build();
            }

            BlobId blobId = BlobId.of(bucketName, data.id);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            // post's image being saved in google storage, with photoid == this.data.id
            storage.createFrom(blobInfo, new ByteArrayInputStream(Base64.getDecoder().decode(data.photo)));

            // post metadata
            feedPost = Entity.newBuilder(postKey)
                    .set("sender", sender)
                    .set("title", sender + " :" + data.title)
                    .set("creation_date", data.postingDate)
                    .set("text", data.text)
                    .set("postId", data.id) // id will match photo id @ storage
                    .set("comments", g.toJson(new ArrayList<String>())).build();

            txn.put(feedPost);
            txn.commit();
            LOG.info("FeedPost was sent successfully.");
            processedPostData = feedPost;

        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return Response.ok(g.toJson(processedPostData)).build();
    }

    // Ver se auth token tem match com o owner da actividade, se sim, delete
    // Deletes feedPost and corresponding image from Storage
    @DELETE
    @Path("/{feedPostId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteFeedPost(@PathParam("feedPostId") String feedPostId,
                                   @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String sender = (String) values.get("username");

        Key feedPostKey = feedPostKeyFactory.newKey(feedPostId);

        Transaction txn = datastore.newTransaction();
        try {

            Entity feedPost = txn.get(feedPostKey);
            if(feedPost == null || !feedPost.getString("sender").equals(sender)) { // missing verification for role here todo, should allow superior roles to delete
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: FeedPost does not exist").build();
            }

            String objectName = feedPost.getString("postId");
            Blob blob = storage.get(bucketName, objectName);
            if (blob == null) {
                String msg = "The object " + objectName + " wasn't found in " + bucketName;
                LOG.warning(msg);
                throw new Exception(msg);
            }

            // Optional: set a generation-match precondition to avoid potential race
            // conditions and data corruptions. The request to upload returns a 412 error if
            // the object's generation number does not match your precondition.
            Storage.BlobSourceOption precondition =
                    Storage.BlobSourceOption.generationMatch(blob.getGeneration());

            storage.delete(bucketName, objectName, precondition);

            LOG.info("Object " + objectName + " was deleted from " + bucketName);

            txn.delete(feedPostKey);
            txn.commit();
            LOG.fine("FeedPost with id: " + feedPostId + " was deleted.");
            return Response.ok("FeedPost with id: " + feedPostId + " was deleted.").build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    // maybe we could store where in the array this user has commented? keep track of all user's comments
    // that way we could look them up and delete them if needed? @gallis
    @PUT
    @Path("/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postComment(@Context HttpServletRequest request, FeedCommentPostData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String sender = (String) values.get("username");

        // debug
        //String sender = "Francisco Tester";

        data.id = data.generateId(sender);

        Transaction txn = datastore.newTransaction();

        try {

            Key postKey = feedPostKeyFactory.newKey(data.origPostId);
            Entity feedPost = txn.get(postKey);

            // if already exists
            if(feedPost == null){
                txn.rollback();
                return Response.serverError().entity("Non existent post.").build();
            }

            // add comment to post
            ArrayList<String> comments = g.fromJson(feedPost.getString("comments"), ArrayList.class);

            if(data.text.length() > 250){
                throw new Exception("Comment may not be longer than 250 characters");
            }

            comments.add(sender + ": " + data.text); // should save posting date of this comment todo order will be preserved by addition to list for now

            // post metadata, assuming post exists
            feedPost = Entity.newBuilder(postKey)
                    .set("title", feedPost.getString("title"))
                    .set("sender", feedPost.getString("sender"))
                    .set("text", feedPost.getString("text"))
                    .set("creation_date", feedPost.getLong("creation_date"))
                    .set("comments", g.toJson(comments))
                    .set("postId", data.origPostId).build(); // id will match photo id @ storage

            txn.put(feedPost);
            txn.commit();
            LOG.info("Comment on Post was successfully posted.");

        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return Response.ok(g.toJson(data)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listFeedPosts(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                                  @QueryParam("page") int page, @QueryParam("pattern") String pattern){

        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
        }
        catch (NoSuchElementException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }

        assert jwt != null;
        Transaction txn = datastore.newTransaction();
        try {

            EntityQuery.Builder queryBuilder =
                    Query.newEntityQueryBuilder().setKind("FeedPost")
                            .setOffset(elements*page)
                            .setLimit(elements);

            if(pattern != null) {
                queryBuilder.setFilter(
                        StructuredQuery.PropertyFilter.ge("title", pattern)
                );
            }
            QueryResults<Entity> tasks = txn.run(queryBuilder.build());

            List<String> finalList = new ArrayList<>(elements);
            int maxNumber = tasks.getSkippedResults();
            while (tasks.hasNext()) {
                Entity aux = tasks.next();

                // get photo from Google Storage, encode to base 64
                String photoBase64 = Base64.getEncoder().encodeToString(storage.readAllBytes(bucketName, aux.getString("postId")));

                System.out.println( aux.getString("comments") );
                maxNumber++;
                FeedPostDataList data = new FeedPostDataList(
                        aux.getString("postId"),
                        aux.getString("title"),
                        aux.getString("text"),
                        photoBase64,
                        aux.getLong("creation_date"),
                        g.fromJson(aux.getString("comments") , String[].class));
                finalList.add(g.toJson(data));
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(finalList));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            LOG.fine("FeedPosts listed.");
            return Response.ok(g.toJson(jsonObject)).build();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        }
    }
}