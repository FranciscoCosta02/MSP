package resources;

import com.google.cloud.datastore.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.codec.digest.DigestUtils;
import utils.JWTValidation;
import utils.PasswordData;
import utils.RoleUpdateData;
import utils.UserData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/update")
public class UpdateResource {

    private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    public UpdateResource() {
    }

    private StringValue nonIndexedString(String data) {
        LOG.warning("StringValue: "+data);
        if(data!=null)
            return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
        return StringValue.newBuilder("").setExcludeFromIndexes(true).build();
    }

    @PUT
    @Path("/attributes")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Context HttpServletRequest request, UserData user) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Token does not exist.").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try{

            Key updateKey = userKeyFactory.newKey(user.username);
            Entity userToUpdate = txn.get(updateKey);
            if(userToUpdate == null) {
                txn.rollback();
                return Response.status(Status.BAD_REQUEST).entity("Error: User does not exist.").build();
            }
            String updRole = userToUpdate.getString("role");
            String role = (String) values.get("role");
            String username = (String) values.get("username");
            if(!username.equals(user.username)) {
                switch (role) {
                    case "ADMIN":
                        if(updRole.equals("SU"))
                            return Response.status(Status.BAD_REQUEST).entity("Error: Don't have permissions.").build();
                        break;
                    case "SU":
                        break;
                    default:
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error: Wrong Role.").build();
                }
            }

            userToUpdate = Entity.newBuilder(userToUpdate)
                    .set("email", user.email)
                    .set("name", user.name)
                    .set("activity", user.activity)
                    .set("privacy", nonIndexedString(user.privacy))
                    .set("department", user.department)
                    .set("phone", nonIndexedString(user.phone))
                    .set("photo", nonIndexedString(user.photo))
                    .set("groups", nonIndexedString(user.groups)).build();
            txn.update(userToUpdate);
            txn.commit();
            LOG.fine("User updated: " + user.username);
            return Response.ok().build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updatePwd(@Context HttpServletRequest request, PasswordData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {

            String username = (String) values.get("username");
            Entity user = txn.get(userKeyFactory.newKey(username));
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
            }
            LOG.fine("Attempt to update user: " + username);

            if (!user.getString("password").equals(DigestUtils.sha512Hex(data.oldPwd)))
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error: Wrong password").build();

            Response pwdValidation = data.validPwd();
            if(pwdValidation.getStatus() != Response.Status.OK.getStatusCode())
                return pwdValidation;

            Entity newU = Entity.newBuilder(user)
                    .set("password", nonIndexedString(DigestUtils.sha512Hex(data.newPwd))).build();
            txn.update(newU);
            txn.commit();
            LOG.fine("User updated: " + username);
            return Response.ok().build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/role")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateRole(@Context HttpServletRequest request, RoleUpdateData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String username = (String) values.get("username");
            Entity user = txn.get(userKeyFactory.newKey(username));
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: User does not exist!").build();
            }
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User does not have permissions!").build();
            }
            LOG.fine("Attempt to update user: " + username);
            Entity userChange = txn.get(userKeyFactory.newKey(data.username));
            if(userChange == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User to change does not exist!").build();
            }
            userChange = Entity.newBuilder(userChange).set("role", data.role).build();
            txn.update(userChange);
            txn.commit();
            LOG.fine("User updated: " + data.username);
            return Response.ok().build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/deactivate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deactivateAccount(@Context HttpServletRequest request, @QueryParam("id") String uid) {
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
            Key userToDeaKey = userKeyFactory.newKey(uid);
            Entity userToDea = txn.get(userToDeaKey);
            if(userToDea == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Error: User to deactivate does not exist")
                        .build();
            }
            String role = (String) values.get("role");
            if(role.equals("SU") || role.equals("ADMIN") || username.equals(uid)) {
                userToDea = Entity.newBuilder(userToDea).set("activity", "Inactive").build();
                txn.update(userToDea);
                txn.commit();
                LOG.info("User was deactivated successfully");
                return Response.ok("User was deactivated successfully").build();
            }
            txn.rollback();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: No permission to deactivate this user").build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/activate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response activateAccount(@Context HttpServletRequest request, @QueryParam("id") String uid) {
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
            Key userToActKey = userKeyFactory.newKey(uid);
            Entity userToAct = txn.get(userToActKey);
            if(userToAct == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Error: User to activate does not exist")
                        .build();
            }
            String role = (String) values.get("role");
            if(role.equals("SU") || role.equals("ADMIN") || username.equals(uid)) {
                userToAct = Entity.newBuilder(userToAct).set("activity", "Active").build();
                txn.update(userToAct);
                txn.commit();
                LOG.info("User was activated successfully");
                return Response.ok("User was activated successfully").build();
            }
            txn.rollback();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: No permission to activate this user").build();
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
}
