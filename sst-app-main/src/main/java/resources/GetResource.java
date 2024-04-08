package resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.GetUserData;
import utils.JWTValidation;
import utils.UserData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/get")
public class GetResource {
    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final Gson g = new Gson();

    public GetResource() {}

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUser(@Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();

        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token.").build();

        Claims values = jwt.getBody();
        String username = (String) values.get("username");

        Transaction txn = datastore.newTransaction();
        try {
            Entity user = txn.get(userKeyFactory.newKey(username));
            GetUserData data = new GetUserData(username, user.getString("email"),
                                            user.getString("name"),
                                            "", "",
                                            user.getString("role"), 
                                            user.getString("activity"),
                                            user.getString("privacy"),
                                            user.getString("phone"),
                                            user.getString("department"),
                                            user.getString("photo"),
                                            user.getString("groups"),
                                            "14.33","60");
            txn.commit();
            return Response.ok(g.toJson(data)).build();
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
    @Path("/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getRoles() {
        List<String> list = new ArrayList<>();
        list.add("STUDENT");
        list.add("TEACHER");
        list.add("GUEST");
        list.add("SERVICE");
        list.add("STAFF");
        list.add("ADMIN");
        list.add("SU");
        return Response.ok(g.toJson(list)).build();
    }

}