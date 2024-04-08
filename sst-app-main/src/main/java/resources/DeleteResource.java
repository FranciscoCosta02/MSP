package resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.JWTValidation;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/delete")
public class DeleteResource {

    private static final Logger LOG = Logger.getLogger(DeleteResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final Gson g = new Gson();

    public DeleteResource() {}

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteUser(@Context HttpServletRequest request, @QueryParam("id") String uid){
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String username = (String) values.get("username");
            Key userKey = userKeyFactory.newKey(username);
            Entity user = txn.get(userKey);
            if(user == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: User does not exist!").build();
            }
            Key userToDelKey = userKeyFactory.newKey(uid);
            Entity userToDel = txn.get(userToDelKey);
            if(userToDel == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error: User to delete does not exist")
                        .build();
            }
            String role = (String) values.get("role");
            if(role.equals("SU") || role.equals("ADMIN") || username.equals(uid)) {
                Key messagesToDel = datastore.newKeyFactory().addAncestors(PathElement.of("User", uid))
                        .setKind("Feed").newKey("messages");
                Entity feed = txn.get(messagesToDel);
                if(feed != null)
                    txn.delete(messagesToDel);
                txn.delete(userToDelKey);
                txn.commit();
                LOG.info("User was deleted successfully.");
                return Response.ok(uid).build();
            }
            txn.rollback();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: No permission to delete this user").build();
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
