package resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.codec.digest.DigestUtils;
import utils.GroupData;
import utils.JWTValidation;
import utils.LoginData;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/group")
public class GroupResource {

    private static final Logger LOG = Logger.getLogger(GroupResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory groupKeyFactory = datastore.newKeyFactory().setKind("Group");
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final Gson g = new Gson();

    public GroupResource(){}

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createGroup(GroupData data, @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key groupKey = groupKeyFactory.newKey(data.name);
        if(data.checkNull(data.name)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name is required!").build();
        }
        if(data.privacy.equals("Private")) {
            Response pwdValidation = data.pwdValid();
            if(pwdValidation.getStatus() != Response.Status.OK.getStatusCode())
                return pwdValidation;
        }
        Transaction txn = datastore.newTransaction();
        try{
            String username = (String) values.get("username");
            Entity user = txn.get(userKeyFactory.newKey(username));
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User does not exist!").build();
            }
            Entity group = txn.get(groupKey);
            if(group != null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Group already exists").build();
            }
            ListValue list = ListValue.newBuilder().addValue(username).build();
            group = Entity.newBuilder(groupKey).set("privacy", nonIndexedString(data.privacy))
                    .set("password", nonIndexedString(DigestUtils.sha512Hex(data.password)))
                    .set("owner", username).set("participants", list).build();
            String groups = user.getString("myGroups");
            if(groups.isEmpty()) {
                groups = data.name;
            } else {
                groups = groups + ", " + data.name;
            }
            user = Entity.newBuilder(user).set("myGroups", groups).build();
            txn.update(user);
            txn.put(group);
            txn.commit();
            LOG.fine("Group created: " + data.name);
            return Response.ok(g.toJson(data.name)).build();
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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateGroup(@Context HttpServletRequest request, GroupData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Key groupKey = groupKeyFactory.newKey(data.name);
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try{
            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Group does not exist").build();
            }
            String username = (String) values.get("username");
            if(!username.equals(group.getString("owner"))) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User is not the owner").build();
            }
            if(group.getString("privacy").equals(data.privacy)) {
                if (data.privacy.equals("Private")){
                    String pwd = DigestUtils.sha512Hex(data.password);
                    if(data.pwdValid().getStatus() == 200 && !group.getString("password").equals(pwd)) {
                        group = Entity.newBuilder(group).set("password", nonIndexedString(pwd)).build();
                        txn.update(group);
                        txn.commit();
                        return Response.ok("Group Password updated successfully").build();
                    } else {
                        txn.rollback();
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Password is not valid!").build();
                    }
                } else {
                    txn.rollback();
                    return Response.ok("No changes made!").build();
                }
            } else {
                if(data.privacy.equals("Private")) {
                    if(data.pwdValid().getStatus() == 200) {
                        String pwd = DigestUtils.sha512Hex(data.password);
                        group = Entity.newBuilder(group).set("password", nonIndexedString(pwd))
                                .set("privacy", nonIndexedString(data.privacy)).build();
                        txn.update(group);
                        txn.commit();
                        return Response.ok("Group Privacy Status and Password updated successfully").build();
                    } else {
                        txn.rollback();
                        return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Password is not valid!").build();
                    }
                } else {
                    group = Entity.newBuilder(group).set("password", nonIndexedString(""))
                            .set("privacy", nonIndexedString(data.privacy)).build();
                    txn.update(group);
                    txn.commit();
                    return Response.ok("Group Privacy Status and Password updated successfully").build();
                }
            }
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

    // Atributos do LoginData de addUserToGroup é o name do grupo e a sua password
    @PUT
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUserToGroup(@Context HttpServletRequest request, LoginData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key groupKey = groupKeyFactory.newKey(data.username);
        Transaction txn = datastore.newTransaction();
        try{

            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Group does not exist").build();
            }
            String privacy = group.getString("privacy");
            String pwd = group.getString("password");
            if(privacy.equals("Private")) {
                if(!pwd.equals(DigestUtils.sha512Hex(data.password))) {
                    txn.rollback();
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Incorrect Password").build();
                }
            }
            String username = (String) values.get("username");
            Entity user = txn.get(userKeyFactory.newKey(username));
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User does not exist!").build();
            }
            List<Value<?>> l = group.getList("participants");
            for(Value<?> v: l) {
                String aux = (String) v.get();
                if(aux.equals(username))
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User is already in the group")
                            .build();
            }

            ListValue list = ListValue.newBuilder().set(l)
                    .addValue(username).build();

            group = Entity.newBuilder(group).set("participants", list).build();
            String groups = user.getString("myGroups");
            if(groups.isEmpty()) {
                groups = data.username;
            } else {
                groups = groups + ", " + data.username;
            }
            user = Entity.newBuilder(user).set("myGroups", groups).build();
            txn.update(group, user);
            txn.commit();
            LOG.fine("User " + username + " added to Group " + data.username);
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

    @PUT
    @Path("/remove/{delUser}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUserFromGroup(@Context HttpServletRequest request, @QueryParam("group") String name,
                                        @PathParam("delUser") String delUser) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key groupKey = groupKeyFactory.newKey(name);
        Transaction txn = datastore.newTransaction();
        try{

            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Group does not exist").build();
            }
            String username = (String) values.get("username");
            String owner = group.getString("owner");
            if(!username.equals(owner) && !username.equals(delUser) && !username.equals("SU")) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Don't have permissions").build();
            }
            Entity user = txn.get(userKeyFactory.newKey(username));
            Entity del = txn.get(userKeyFactory.newKey(delUser));
            if(user == null || del == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User does not exist!").build();
            }

            List<Value<String>> l = group.getList("participants");
            ListValue.Builder builder = ListValue.newBuilder();
            for (Value<String> stringValue : l) {
                String aux = stringValue.get();
                if (!aux.equals(delUser)) {
                    builder.addValue(aux);
                }
            }
            ListValue list = builder.build();
            if(list.get().size() == l.size()) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity(delUser + " is not in the group!").build();
            }
            if(l.isEmpty()){
                txn.delete(groupKey);
                txn.commit();
                return Response.ok().entity("Group deleted completely").build();
            }
            if(delUser.equals(owner)) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Owner must assign someone else as the owner first!").build();
            }
            group = Entity.newBuilder(group).set("participants", list).build();
            deleteGroupFromUser(del, name, txn);
            txn.update(group);
            txn.commit();
            LOG.fine("User " + username + " removed from Group " + name);
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

    private void deleteGroupFromUser(Entity user, String name, Transaction txn) {
        String[] groups = user.getString("myGroups").split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for(String aux: groups) {
            aux = aux.trim();
            if(!aux.equals(name)){
                if(stringBuilder.length() == 0)
                    stringBuilder.append(aux);
                else
                    stringBuilder.append(", ").append(aux);
            }
        }
        String myGroups = stringBuilder.toString();
        user = Entity.newBuilder(user).set("myGroups", myGroups).build();
        txn.update(user);
    }

    @PUT
    @Path("/assign/{newOwner}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response assignNewOwner(@Context HttpServletRequest request, @PathParam("newOwner") String username,
                                   @QueryParam("group") String name) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key groupKey = groupKeyFactory.newKey(name);
        Transaction txn = datastore.newTransaction();
        try{

            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Group does not exist").build();
            }
            String owner = (String) values.get("username");
            if(!owner.equals(group.getString("owner")) && !username.equals("SU")){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error: User is not the owner of this group").build();
            }

            if(owner.equals(username)) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("Error: User selected is already the owner").build();
            }
            List<Value<?>> l = group.getList("participants");
            for(Value<?> v: l) {
                String u = (String) v.get();
                if(u.equals(username)){
                    group = Entity.newBuilder(group).set("owner", u).build();
                    txn.update(group);
                    txn.commit();
                    return Response.ok(g.toJson(u)).build();
                }
            }
            txn.rollback();
            return Response.status(Response.Status.BAD_REQUEST).entity("User selected does not exist").build();
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
    public Response deleteGroup(@Context HttpServletRequest request, @QueryParam("group") String name) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key groupKey = groupKeyFactory.newKey(name);
        Transaction txn = datastore.newTransaction();
        try{
            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Group does not exist").build();
            }
            String username = (String) values.get("username");
            if(!username.equals(group.getString("owner")) && !username.equals("SU"))
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: Don't have permissions").build();

            Entity user = txn.get(userKeyFactory.newKey(username));
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Owner does not exist!").build();
            }
            List<Value<String>> participants = group.getList("participants");
            for(Value<String> p: participants) {
                String aux = p.get();
                user = txn.get(userKeyFactory.newKey(aux));
                deleteGroupFromUser(user, name, txn);
            }
            txn.delete(groupKey);
            txn.commit();
            LOG.fine("Group " + name + " was deleted.");
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
