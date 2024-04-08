package resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.JWTValidation;
import utils.StatsData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/statistics")
public class StatisticsResourse {
    private static final Logger LOG = Logger.getLogger(StatisticsResourse.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson g = new Gson();

    public StatisticsResourse() {}
    
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getTotalAccesses(@Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String role = (String) values.get("role");
        switch (role) {
            case "STUDENT":
            case "TEACHER":
            case "GUEST":
                return Response.status(Status.BAD_REQUEST).entity("Error: Don't have permissions.").build();
            case "STAFF":
            case "SERVICE":
            case "ADMIN":
            case "SU":
                break;
            default:
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error: Wrong Role.").build();
        }
        Transaction txn = datastore.newTransaction();
        try {
            Query<Entity> query = Query.newEntityQueryBuilder()
                        .setKind("Stats")
                        .build();
            QueryResults<Entity> results = txn.run(query);
            List<StatsData> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity stats = results.next();
                list.add( new StatsData(stats.getKey().getName(),
                                        stats.getLong("total_number_logins"), 
                                        stats.getLong("total_number_failed"),
                                        stats.getString("total_first_login"),
                                        stats.getString("total_last_login"),
                                        stats.getString("total_last_attempt")));
            }
            return Response.ok(g.toJson(list)).build();
        }  catch (Exception e) {
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
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUserAccesses(@Context HttpServletRequest request, @PathParam("username") String username) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String role = (String) values.get("role");
        switch (role) {
            case "STUDENT":
            case "TEACHER":
            case "GUEST":
                return Response.status(Status.BAD_REQUEST).entity("Error: Don't have permissions.").build();
            case "STAFF":
            case "SU":
                break;
            default:
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error: Wrong Role.").build();
        }
        Key userStatisticsKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                                        .setKind("UserStats").newKey("userStats");
        Entity stats = datastore.get(userStatisticsKey);
        StatsData response = null;
        if(stats != null)
            response = new StatsData(username, 
                                    stats.getLong("user_number_logins"), 
                                    stats.getLong("user_number_failed"),
                                    stats.getString("user_first_login"),
                                    stats.getString("user_last_login"),
                                    stats.getString("user_last_attempt"));
        
        return Response.ok(g.toJson(response)).build();
    }
}