package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.newEntityQueryBuilder;
import static java.time.Instant.now;
import static resources.SendEmailResource.sendActivationEmail;

@Path("/activation")
public class ActivationResource {

    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final Logger LOG = Logger.getLogger(ActivationResource.class.getName());
    private final KeyFactory activateTokenKeyFactory = datastore.newKeyFactory().setKind("ActivationToken");

    private static StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    public static void sendActivationRequest(Transaction txn, String email,String username) {
        LOG.warning("sendActivationRequest");
         try {
             ActivationToken at = new ActivationToken(email);
             Key authKey = datastore.newKeyFactory().setKind("ActivationToken").newKey(at.tokenID);
             Entity auth = txn.get(authKey);
             while(auth != null) {
                at = new ActivationToken(email);
                authKey = datastore.newKeyFactory().setKind("ActivationToken").newKey(at.tokenID);
                auth = txn.get(authKey);
             }
             Date date = Date.from(now().plusMillis(at.getExpirationTime()));
             auth = Entity.newBuilder(authKey).set("jwtToken", at.jwtToken).set("expireAt", nonIndexedTimeStamp(Timestamp.of(date))).build();
             txn.put(auth);
             sendActivationEmail(email,at.jwtToken,username);
             txn.commit();
             LOG.info("Email sent successfully");
         } catch (Exception e) {
             txn.rollback();
             LOG.severe(e.getMessage());
         } finally {
             if (txn.isActive()) {
                 txn.rollback();
             }
         }
    }

    private static TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCode(@Context HttpServletRequest request,@QueryParam("tk") String tk) {
        LOG.warning("ACTIVATING ACCOUNT");
        LOG.warning(tk);
        Jws<Claims> jwt = JWTActivationValidation.parseJwt(tk);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String email = (String) values.get("email");
        Key tokenKey = activateTokenKeyFactory.newKey(values.getId());
        Transaction txn = datastore.newTransaction();
        try{
            Query<Entity> query = newEntityQueryBuilder().setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("email", email)).build();
            QueryResults<Entity> results = txn.run(query);
            if(results.hasNext()) {
                Entity user = results.next();
                if(user.getString("activity").equals("Active")){
                    txn.rollback();
                    return Response.ok("Account was already activated").build();
                }
                user = Entity.newBuilder(user).set("activity", "Active").build();
                txn.update(user);
                txn.delete(tokenKey);
                txn.commit();
                return Response.ok("User is now active").build();
            }
            txn.rollback();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Email is not associated with any account").build();
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
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response activateAccount(ActivationData data) {
        Transaction txn = datastore.newTransaction();
        try{
            Query<Entity> query = newEntityQueryBuilder().setKind("User")
                    .setFilter(StructuredQuery.PropertyFilter.eq("email", data.email)).build();
            QueryResults<Entity> results = txn.run(query);
            if(results.hasNext()) {
                Entity user = results.next();
                if(user.getString("activity").equals("Active")){
                    txn.rollback();
                    return Response.ok("Account was already activated").build();
                }
                if(data.clipExists()) {
                    user = Entity.newBuilder(user).set("activity", "Active").build();
                    txn.update(user);
                    txn.commit();
                    return Response.ok("User is now active").build();
                }
            }
            txn.rollback();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Email is not associated with any account").build();
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
