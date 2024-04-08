package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.ActivityCalendarData;
import utils.ActivityCalendarDataList;
import utils.JWTValidation;
import utils.MyActivities;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.newEntityQueryBuilder;
import static resources.ListObjectsWithPrefix.listObjectsWithPrefix;

@Path("/activity")
public class ActivityCalendarResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory activityKeyFactory = datastore.newKeyFactory().setKind("Activity");
    private final KeyFactory myActivitiesKeyFactory = datastore.newKeyFactory().setKind("MyActivities");
    private final Gson g = new Gson();

    public ActivityCalendarResource() {

    }


    /*
    This registers an activity, associated with one user
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response registerActivity(ActivityCalendarData data,
                                     @Context HttpServletRequest request) {

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
        Claims values = jwt.getBody();

        if(data.confirmInputs())
            return Response.status(Status.BAD_REQUEST)
                    .entity("At least one input is empty").build();

        // set Activity id
        data.id = data.generateActivityId();
        data.username = (String) values.get("username");
        Key activityKey = activityKeyFactory.newKey(data.id);

        LOG.fine("Attempt to register activity: " + data.title);
        Transaction txn = datastore.newTransaction();

        try{

            Entity activity = txn.get(activityKey);
            if(activity != null) {
                txn.rollback();
                // activity created by this user at this time already exists!
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Activity already exists").build();
            }
            ListValue list = ListValue.newBuilder().addValue(data.username).build();

            //auth = Entity.newBuilder(authKey).set("jwtToken", at.jwtToken).set("expireAt", Timestamp.of(date)).build();

            //line below is what is being stored in datastore
            activity = Entity.newBuilder(activityKey)
                    .set("participants", list)
                    .set("startDate", nonIndexedString(data.startDate))
                    .set("endDate", nonIndexedString(data.endDate))
                    .set("expireAt", Timestamp.parseTimestamp(data.endDate))
                    .set("title", data.title)
                    .set("description", nonIndexedString(data.description))
                    .set("owner", data.username)
                    .set("maxParticipants", nonIndexedString(data.maxParticipants))
                    .build();
            txn.put(activity);
            txn.commit();
            LOG.fine("Activity registered: Id: " + data.id);
            return Response.ok("Activity registered: Id: " + data.id).build();
        } catch (Exception e) {
            txn.rollback();
            LOG.warning("ERROR MSG: "+ e.getMessage());
            e.printStackTrace();
            return Response.status(Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    private TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    // Atributos do LoginData de addUserToActivity é o name do grupo e a sua password TODO
    // must do: check if requester is owner of activity, if yes, allow add user
    // gets activity, requester data and user to be added. if all match adds user to activity
    @PUT
    @Path("/addUser/{userToAdd}/{actId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUserToActivity(@Context HttpServletRequest request,
                                      @PathParam("actId") String actId, // gets id of activity to be added
                                      @PathParam("userToAdd") String userToAdd) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
        }
        catch (NoSuchElementException e){
            return Response.status(Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }

        assert jwt != null;
        Key activityKey = activityKeyFactory.newKey(actId);
        Key myActivityKey = myActivitiesKeyFactory.newKey(userToAdd+"_"+actId);
        Transaction txn = datastore.newTransaction();
        try{
            Entity activity = txn.get(activityKey);
            if(activity == null) {
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("Error: Activity does not exist").build();
            }
            String startDate = activity.getString("startDate");
            if(Timestamp.now().compareTo(Timestamp.parseTimestamp(startDate))>=0){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("Error: Activity already started no one can join activity anymore").build();
            }
            // verification if user is in list
            List<Value<?>> l = activity.getList("participants");
            int numParticipants = activity.getList("participants").size()-1;
            if(numParticipants>=Integer.parseInt(activity.getString("maxParticipants"))){
                txn.rollback();
                return Response.status(Status.NOT_ACCEPTABLE).entity("the maximum number of participants has been reached").build();
            }
            for(Value<?> v: l) {
                String aux = (String) v.get();
                if(aux.equals(userToAdd))
                    return Response.status(Status.NOT_ACCEPTABLE).entity("User is already in the activity")
                            .build();
            }
            Entity myActivity;

            // verification if user exists
            Entity userEnt = txn.get(datastore.newKeyFactory().setKind("User").newKey(userToAdd));
            if(userEnt == null){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("Error: User does not exist.").build();
            }

            ListValue list = ListValue.newBuilder().set(l)
                    .addValue(userToAdd).build();
            activity = Entity.newBuilder(activity).set("participants", list).build();

            String endDate = activity.getString("endDate");
            myActivity = Entity.newBuilder(myActivityKey)
                    .set("username", userToAdd)
                    .set("title",activity.getString("title"))
                    .set("startDate",nonIndexedString(activity.getString("startDate")))
                    .set("endDate",nonIndexedString(endDate))
                    .set("description",nonIndexedString(activity.getString("description")))
                    .set("expireAt", nonIndexedTimeStamp(Timestamp.parseTimestamp(endDate))).build();

            txn.put(myActivity);
            txn.update(activity);
            txn.commit();
            LOG.fine("User " + userToAdd + " added to Activity with Id: " + actId);
            return Response.ok("User " + userToAdd + " added to Activity with Id: " + actId).build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/delUser/{delUser}/{actId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUserFromActivity(@Context HttpServletRequest request, @PathParam("actId") String actId,
                                        @PathParam("delUser") String delUser) {
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

        Key activityKey = activityKeyFactory.newKey(actId);
        Key personalActivityKey = myActivitiesKeyFactory.newKey(delUser+"_"+actId);
        Transaction txn = datastore.newTransaction();
        try{


            Entity activity = txn.get(activityKey);
            if(activity == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Activity does not exist").build();
            }

            String startDate = activity.getString("startDate");
            if(Timestamp.now().compareTo(Timestamp.parseTimestamp(startDate))>=0){
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("Error: Activity already started no one can left activity anymore").build();
            }

            String username = delUser;//(String) values.get("username");

            // check if trying to delete owner, dont let
            if(username.equals(activity.getString("owner")) ) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Don't have permissions").build();
            }

            List<Value<String>> listPart = activity.getList("participants");

            // verification if user exists
            Entity userEnt = txn.get(datastore.newKeyFactory().setKind("User").newKey(delUser));
            if(userEnt == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: User does not exist.").build();
            }

            ListValue.Builder newListPart = ListValue.newBuilder();

            // very inefficient! should be a binary search tree so we dont have to traverse linearly all users!
            // array will do for now :( @francisco gallis

            for (Value<String> v : listPart) {
                String aux = v.get();
                if (!aux.equals(delUser)) {
                    newListPart.addValue(aux); // new list with user removed
                }
            }

            System.out.println(newListPart);
            activity = Entity.newBuilder(activity).set("participants", newListPart.build()).build();


            txn.update(activity);
            txn.delete(personalActivityKey);
            txn.commit();
            LOG.fine("User " + username + " removed from Activity " + actId);
            return Response.ok("User " + username + " removed from Activity " + actId).build();

        } catch (Exception e) {
            txn.rollback();
            e.printStackTrace();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(delUser + " is not in the activity!").build();
            //return Response.status(Response.Status.FORBIDDEN).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    // Ver se auth token's username tem match com o username do owner da actividade, se sim, delete
    @DELETE
    @Path("/{actId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteActivity(@PathParam("actId") String activityId,
                                   @Context HttpServletRequest request) {
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
        Claims values = jwt.getBody();
        Key activityKey = activityKeyFactory.newKey(activityId);
        Transaction txn = datastore.newTransaction();
        try {

            Entity activity = txn.get(activityKey);
            if(activity == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Activity does not exist").build();
            }

            String username = (String) values.get("username");
            String token = (String) values.get("token");
            if((activity.getString("owner").equals(username)) || token.equals("SU")){
                txn.delete(activityKey);
                txn.commit();
                LOG.fine("Activity with id: " + id + " was deleted.");
            }
            else{
                throw new Exception("User does not own activity, cannot delete.");
            }

            return Response.ok("Activity with id: " + activityId + " was deleted.").build();
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

    @GET
    @Path("/photos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listActivityPhotos(@Context HttpServletRequest request, @QueryParam("id") String id){
        String Headerid = request.getHeader("Authorization");
        Headerid = Headerid.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(Headerid);
        }
        catch (NoSuchElementException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }
        assert jwt != null;
        //Claims values = jwt.getBody();

        try{

            List photosList = listObjectsWithPrefix(id);
            return Response.ok(photosList).build();
        }
        catch (Exception e){
            return Response.status(Status.EXPECTATION_FAILED).entity("Error getting activity photos").build();
        }
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUserActivities(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                                      @QueryParam("cursor") String cursor, @QueryParam("pattern") String pattern){
        String Headerid = request.getHeader("Authorization");
        Headerid = Headerid.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(Headerid);
        }
        catch (NoSuchElementException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }
        assert jwt != null;
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try{
            String username = (String) values.get("username");
            EntityQuery query = createActivityQuery(elements, cursor, username);
            QueryResults<Entity> tasks = txn.run(query);
            List<MyActivities> activitiesList = new ArrayList<>();
            Cursor newCursor = tasks.getCursorAfter();
            while (tasks.hasNext()) {
                Entity aux = tasks.next();
                String title = aux.getString("title");
                if(title.contains(pattern)) {
                    MyActivities data = new MyActivities(aux.getKey().getName(),
                            aux.getString("username"), aux.getString("startDate"),
                            aux.getString("endDate"), title, aux.getString("description"));
                    activitiesList.add(data);
                }
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(activitiesList));
            jsonObject.addProperty("cursor", newCursor.toUrlSafe());
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
        }
        catch (Exception e){
            LOG.warning(e.getMessage());
            return Response.status(Status.EXPECTATION_FAILED).entity("Error user activities").build();
        }

    }

    private EntityQuery createActivityQuery(int elements, String startCursor, String username) {
        EntityQuery.Builder q = newEntityQueryBuilder().setKind("MyActivities")
                .setFilter(StructuredQuery.PropertyFilter.eq("username", username))
                .setLimit(elements);
        if(!startCursor.isEmpty())
            q.setStartCursor(Cursor.fromUrlSafe(startCursor));
        return q.build();
    }

    /*@GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listActivities(@Context HttpServletRequest request, @QueryParam("elements") int elements,
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
        try {

            EntityQuery.Builder queryBuilder =
                    Query.newEntityQueryBuilder().setKind("Activity")
                            .setOffset(elements*page)
                            .setFilter(StructuredQuery.PropertyFilter.ge("expireAt", Timestamp.now()))
                            .setLimit(elements);

            if(pattern != null) {
                queryBuilder.setFilter(StructuredQuery.CompositeFilter.and(
                        StructuredQuery.PropertyFilter.ge("title", pattern),StructuredQuery.PropertyFilter.ge("expireAt", Timestamp.now())
                        )
                );
            }
            QueryResults<Entity> tasks = datastore.run(queryBuilder.build());

            List<ActivityCalendarDataList> list = new ArrayList<>(elements);
            int maxNumber = tasks.getSkippedResults();
            while (tasks.hasNext()) {
                Entity aux = tasks.next();
                maxNumber++;
                ActivityCalendarDataList data = new ActivityCalendarDataList(aux.getKey().getName(),
                        aux.getString("owner"), aux.getString("startDate"), aux.getString("endDate"),
                        aux.getString("title"), aux.getString("description"),aux.getString("maxParticipants"), ""+(aux.getList("participants").size()-1));
                list.add(data);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            LOG.fine("Activities listed.");
            return Response.ok(g.toJson(jsonObject)).build();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        }
    }*/

    @GET
    @Path("/backOffice")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listActivitiesBackOffice(@Context HttpServletRequest request, @QueryParam("elements") int elements,
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
        try {

            EntityQuery.Builder queryBuilder =
                    Query.newEntityQueryBuilder().setKind("Activity")
                            .setOffset(elements*page)
                            .setLimit(elements);

            if(pattern != null) {
                queryBuilder.setFilter(
                        StructuredQuery.PropertyFilter.ge("title", pattern)
                );
            }
            QueryResults<Entity> tasks = datastore.run(queryBuilder.build());

            List<ActivityCalendarDataList> list = new ArrayList<>(elements);
            int maxNumber = tasks.getSkippedResults();
            while (tasks.hasNext()) {
                Entity aux = tasks.next();
                maxNumber++;
                ActivityCalendarDataList data = new ActivityCalendarDataList(aux.getKey().getName(),
                        aux.getString("owner"), aux.getString("startDate"), aux.getString("endDate"),
                        aux.getString("title"), aux.getString("description"),aux.getString("maxParticipants"), ""+(aux.getList("participants").size()-1));
                list.add(data);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            LOG.fine("Activities listed.");
            return Response.ok(g.toJson(jsonObject)).build();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        }
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateActivity(@Context HttpServletRequest request,ActivityCalendarData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
        }
        catch (NoSuchElementException e){
            return Response.status(Status.FORBIDDEN).entity("Error: Invalid token").build();
        }

        assert jwt != null;
        Key activityKey = activityKeyFactory.newKey(data.id);
        Transaction txn = datastore.newTransaction();

        try{
            Entity activity = txn.get(activityKey);
            if(activity == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Activity does not exist").build();
            }
            activity = Entity.newBuilder(activity)
                    .set("description", data.description)
                    .set("title", data.title)
                    .set("startDate", nonIndexedString(data.startDate))
                    .set("endDate", nonIndexedString(data.endDate))
                    .set("expireAt", Timestamp.parseTimestamp(data.endDate))
                    .set("maxParticipants", nonIndexedString(data.maxParticipants))
                    .build();
            txn.update(activity);
            txn.commit();
            LOG.info("User was deactivated successfully");
            return Response.ok("User was updated successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Error updating activity").build();
        }
        finally {
            if (txn.isActive())
                txn.rollback();
        }

    }
}

