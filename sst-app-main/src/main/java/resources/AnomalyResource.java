package resources;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.AnomalyData;
import utils.AnomalyDataCreation;
import utils.JWTValidation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/anomaly")
public class AnomalyResource {
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory anomalyKeyFactory = datastore.newKeyFactory().setKind("Anomaly");
    private final Gson g = new Gson();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postAnomaly(@Context HttpServletRequest request, AnomalyDataCreation data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();

        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();

        Claims values = jwt.getBody();
        AnomalyData data2 = new AnomalyData((String) values.get("username"), data.reason, data.text);
        Key aKey = anomalyKeyFactory.newKey(data2.id);
        Transaction txn = datastore.newTransaction();
        try {

            Entity anomaly = txn.get(aKey);
            if(anomaly != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Anomaly already reported.").build();
            }
            Entity a = Entity.newBuilder(aKey)
                    .set("sender", data2.sender)
                    .set("reason", data2.reason)
                    .set("text", data2.text)
                    .set("creation_date", data2.formattedTime)
                    .set("solved", data2.solved).build();
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
    public Response getAnomalies( @Context HttpServletRequest request,  @QueryParam("solved") boolean solved,
                                                                        @QueryParam("elements") int elements, 
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
                    .setKind("Anomaly")
                    .setFilter(PropertyFilter.eq("solved", solved))
                    .setOffset(page*elements)
                    .setLimit(elements)
                    .build();
            QueryResults<Entity> results = txn.run(query);
            int maxNumber = results.getSkippedResults();
            List<AnomalyData> list = new ArrayList<>();
            while(results.hasNext()) {
                maxNumber++;
                Entity aux = results.next();
                AnomalyData data = new AnomalyData();
                data.setAnomalyData(aux.getKey().getName(),
                        aux.getString("sender"),
                        aux.getString("reason"),
                        aux.getString("text"),
                        aux.getString("creation_date"),
                        aux.getBoolean("solved"));
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
    public Response deleteAnomaly(@Context HttpServletRequest request, @QueryParam("aid") String aid) {
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
            Key aKey = anomalyKeyFactory.newKey(aid);
            Entity anomaly = txn.get(aKey);
            if(anomaly == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error: Anomaly does not exist.").build();
            }
            txn.delete(aKey);
            txn.commit();
            return Response.ok().entity("Anomaly removed").build();
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

    @POST
    @Path("/solve")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyAnomalyStatus( @Context HttpServletRequest request, @QueryParam("id") String anomalyId,
                                         @QueryParam("solved") boolean solved) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("ADMIN") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Key aKey = anomalyKeyFactory.newKey(anomalyId);
            Entity anomaly = txn.get(aKey);
            if(anomaly == null) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error: Anomaly does not exist.").build();
            }
            Entity a = Entity.newBuilder(anomaly).set("solved", solved).build();
            txn.put(a);
            txn.commit();
            return Response.ok().build();
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
