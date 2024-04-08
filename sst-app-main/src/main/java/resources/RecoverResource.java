package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import org.apache.commons.codec.digest.DigestUtils;
import utils.RecoverPasswordData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.newEntityQueryBuilder;
import static java.time.Instant.now;
import static resources.SendEmailResource.sendEmail;


@Path("/recover")
public class RecoverResource {

    private static final Logger LOG = Logger.getLogger(RecoverResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory recovery = datastore.newKeyFactory().setKind("Recovery");

    public RecoverResource(){}

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    private TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response sendRecoveryRequest(@QueryParam("email") String email) {
        Transaction txn = datastore.newTransaction();
        try {
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            Key rec = recovery.newKey(number);
            Entity recov = txn.get(rec);
            while (recov != null) {
                number = rnd.nextInt(999999);
                rec = recovery.newKey(number);
                recov = txn.get(rec);
            }
            Query<Entity> query = newEntityQueryBuilder().setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("email", email))
                    .build();

            QueryResults<Entity> results = txn.run(query);
            if(!results.hasNext()) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Email is not associated with any account!")
                        .build();
            }

            long code_expiration_time = 1000L * 60 * 60 * 24;//dia
            Date date = Date.from(now().plusMillis(code_expiration_time));
            recov = Entity.newBuilder(rec).set("email",nonIndexedString(email)).set("expireAt", nonIndexedTimeStamp(Timestamp.of(date))).build();
            String link = "recover, "+number;
            sendEmail(email, link);
            txn.put(recov);
            txn.commit();
            LOG.info("Email sent successfully");
            return Response.ok("Email to recover password sent successfully").build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @PUT
    @Path("/check")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response checkCode(@QueryParam("code") String code, @QueryParam("email") String email, RecoverPasswordData data) {
        Key codeKey = recovery.newKey(Integer.parseInt(code));
        Transaction txn = datastore.newTransaction();
        try{
            Entity codeE = txn.get(codeKey);
            LOG.warning("check code:");
            if(codeE == null || !codeE.getString("email").equals(email)) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Code is not valid!").build();
            }
            Response pwdValidation = data.validPwd();
            if(pwdValidation.getStatus() != Response.Status.OK.getStatusCode()){
                txn.rollback();
                return pwdValidation;
            }

            Query<Entity> query = newEntityQueryBuilder().setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("email", email))
                    .build();
            QueryResults<Entity> results = txn.run(query);
            if(!results.hasNext()) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Email is not associated with any account!")
                        .build();
            }
            Entity user = results.next();
            Entity newU = Entity.newBuilder(user)
                    .set("password", nonIndexedString(DigestUtils.sha512Hex(data.newPwd))).build();
            txn.update(newU);
            txn.delete(codeKey);
            txn.commit();
            return Response.ok("Password changed with success!").build();
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
