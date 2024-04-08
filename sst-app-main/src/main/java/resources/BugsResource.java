package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.BugData;
import utils.BugDataCreation;
import utils.BugDataList;
import utils.JWTValidation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/bug")
public class BugsResource {
    private static final Logger LOG = Logger.getLogger(BugsResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory bugKeyFactory = datastore.newKeyFactory().setKind("Bug");
    private final Gson g = new Gson();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postBug(@Context HttpServletRequest request, BugDataCreation data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();

        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();

        Claims values = jwt.getBody();

        BugData data2 = new BugData((String) values.get("username"), data.reason, data.text);
        Key aKey = bugKeyFactory.newKey(data2.id);
        Transaction txn = datastore.newTransaction();
        try {

            Entity bug = txn.get(aKey);
            if(bug != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Bug already reported.").build();
            }
            Entity a = Entity.newBuilder(aKey)
                    .set("sender", data2.sender)
                    .set("reason", data2.reason)
                    .set("text", data2.text)
                    .set("creation_date", Timestamp.now()).build();
            txn.add(a);
            txn.commit();
            return Response.ok(g.toJson(a)).build();
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
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getBugs( @Context HttpServletRequest request,@QueryParam("elements") int elements,
                                                                        @QueryParam("page") int page) {

        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if (jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("ADMIN") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind("Bug")
                    .setOffset(page*elements)
                    .setLimit(elements)
                    .build();
            QueryResults<Entity> results = txn.run(query);
            int maxNumber = results.getSkippedResults();
            List<BugDataList> list = new ArrayList<>();
            while(results.hasNext()) {
                maxNumber++;
                Entity aux = results.next();
                BugDataList data = new BugDataList();
                data.setBugData(aux.getKey().getName(),
                        aux.getString("sender"),
                        aux.getString("reason"),
                        aux.getString("text"),
                        aux.getTimestamp("creation_date").toString());
                list.add(data);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBug(@Context HttpServletRequest request, @QueryParam("aid") String aid) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if (jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("ADMIN") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Key aKey = bugKeyFactory.newKey(aid);
            Entity bug = txn.get(aKey);
            if(bug == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error: Bug does not exist.").build();
            }
            txn.delete(aKey);
            txn.commit();
            return Response.ok().entity("Bug removed").build();
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

}
