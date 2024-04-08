package resources;

import com.google.cloud.datastore.*;
import org.apache.commons.codec.digest.DigestUtils;
import utils.UserData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Logger;

import static resources.ActivationResource.sendActivationRequest;

@Path("/register")
public class RegisterResource {

    private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    public RegisterResource() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response registerGuest(UserData user) {
            if(user.confirmInputs())
                return Response.status(Status.BAD_REQUEST)
                    .entity("At least one input is empty").build();
            if(!user.emailValid())
                return Response.status(Status.NOT_ACCEPTABLE)
                        .entity("Email format is invalid").build();
            Response pwdValidation = user.pwdValid();
            if(pwdValidation.getStatus() != Status.OK.getStatusCode())
                return pwdValidation;
            LOG.fine("Attempt to register user: " + user.username);
            Key userKey = userKeyFactory.newKey(user.username);
            Transaction txn = datastore.newTransaction();
            try{
                Entity userEnt = txn.get(userKey);
                if(userEnt!=null) {
                    txn.rollback();
                    return Response.status(Status.NOT_ACCEPTABLE).entity("User already exists").build();
                }
                Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
                        .setFilter(StructuredQuery.PropertyFilter.eq("email", user.email)).build();
                QueryResults<Entity> results = datastore.run(query);
                LOG.info("Checking email: "+user.email);
                if(results.hasNext()){
                    txn.rollback();
                    return Response.status(Status.NOT_ACCEPTABLE).entity("Email was already used").build();
                }
                String privacy = "phone;department";
                String groups = "SU;ADMIN;STAFF;SERVICE";
                userEnt = Entity.newBuilder(userKey).set("password", nonIndexedString(DigestUtils.sha512Hex(user.password)))
                        .set("email", user.email).set("name", user.name)
                        .set("role", "GUEST").set("activity", "Inactive")
                        .set("department", nonIndexedString(user.department))
                        .set("phone", nonIndexedString(user.phone))
                        .set("photo", nonIndexedString(""))
                        .set("privacy", nonIndexedString(privacy))
                        .set("groups", nonIndexedString(groups))
                        .set("myGroups", nonIndexedString("")).build();
                txn.put(userEnt);
                LOG.warning("sending email... ");
                sendActivationRequest(txn, user.email,user.username);
                LOG.info("email sent to: "+user.email);
                if (txn.isActive()) {
                    txn.commit();
                }
                LOG.fine("User registered: " + user.username);
                return Response.ok("User " + user.name + " sucessfully registered.").build();
            } catch (Exception e) {
                txn.rollback();
                e.printStackTrace();
                LOG.warning("ERROR MSG: "+e.getMessage());
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

}
