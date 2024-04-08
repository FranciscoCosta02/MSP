package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.codec.digest.DigestUtils;

import utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.Instant.now;
import static resources.ActivationResource.sendActivationRequest;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");
    private final KeyFactory statsKey = datastore.newKeyFactory().setKind("Stats");
    private final Gson g = new Gson();
    public LoginResource() {
    }


    private TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @POST
    @Path("/backOffice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response loginToBackOffice(LoginData userData) {
        Key userKey = userKeyFactory.newKey(userData.username);
        Transaction txn = datastore.newTransaction();
        try{
            Entity userEnt = txn.get(userKey);
            if(userEnt == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: User does not exist").build();
            }
            String confirmation = userEnt.getString("password");
            if(confirmation.equals(DigestUtils.sha512Hex(userData.password))) {
                if(userEnt.getString("activity").equals("Inactive")){
                    sendActivationRequest(txn, userEnt.getString("email"), userData.username);
                    return Response.status(Status.UNAUTHORIZED).entity("Email sent to activate account").build();
                }
                String role = userEnt.getString("role");
                if(role.equals("SU") || role.equals("STAFF") || role.equals("ADMIN") || role.equals("SERVICE")) {
                    AuthToken at = new AuthToken(userData.username, role);
                    Key authKey = datastore.newKeyFactory().setKind("Token").newKey(at.tokenID);
                    Entity auth = txn.get(authKey);
                    while(auth != null) {
                        at = new AuthToken(userData.username, role);
                        authKey = datastore.newKeyFactory().setKind("Token").newKey(at.tokenID);
                        auth = txn.get(authKey);
                    }
                    Date date = Date.from(now().plusMillis(at.getExpirationTime()));
                    auth = Entity.newBuilder(authKey).set("jwtToken", at.jwtToken).set("expireAt", nonIndexedTimeStamp(Timestamp.of(date))).build();
                    txn.put(auth);
                    List<String> list = new ArrayList<>();
                    list.add(at.jwtToken);
                    list.add(at.username);
                    list.add(at.role);
                    addStatistics(txn, userData.username, false);
                    txn.commit();
                    return Response.ok(g.toJson(list)).build();
                }
                txn.rollback();
                return Response.status(Status.NOT_ACCEPTABLE).entity("No permission to enter!").build();
            }
            txn.rollback();
            return Response.status(Status.NOT_ACCEPTABLE).entity("Wrong password for username: " + userData.username).build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(LoginData userData) {
        LOG.fine("Attempt to login user: " + userData.username);
        Key userKey = userKeyFactory.newKey(userData.username);
        Transaction txn = datastore.newTransaction();
        try{
            Entity userEnt = txn.get(userKey);
            if(userEnt == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: User does not exist").build();
            }
            String confirmation = userEnt.getString("password");
            if(confirmation.equals(DigestUtils.sha512Hex(userData.password))) {
                if(userEnt.getString("activity").equals("Inactive")){
                    sendActivationRequest(txn, userEnt.getString("email"),userData.username);
                    return Response.status(Status.UNAUTHORIZED).entity("Email sent to activate account").build();
                }
                String role = userEnt.getString("role");
                AuthToken at = new AuthToken(userData.username, role);
                Key authKey = datastore.newKeyFactory().setKind("Token").newKey(at.tokenID);
                Entity auth = txn.get(authKey);
                while(auth != null) {
                    at = new AuthToken(userData.username, role);
                    authKey = datastore.newKeyFactory().setKind("Token").newKey(at.tokenID);
                    auth = txn.get(authKey);
                }
                //EXPIRATION_TIME
                Date date = Date.from(now().plusMillis(at.getExpirationTime()));
                auth = Entity.newBuilder(authKey).set("jwtToken", at.jwtToken).set("expireAt", nonIndexedTimeStamp(Timestamp.of(date))).build();
                txn.put(auth);
                LOG.info("User " + userData.username + " logged in sucessfully.");
                List<String> list = new ArrayList<>();
                list.add(at.jwtToken);
                list.add(at.username);
                list.add(at.role);
                addStatistics(txn, userData.username, false);
                txn.commit();
                return Response.ok(g.toJson(list)).build();
            } else {
                addStatistics(txn, userData.username, true);
                txn.commit();
                LOG.warning("Wrong password for username: " + userData.username);
                return Response.status(Status.FORBIDDEN).entity("Wrong Password.").build();
            }
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private void addStatistics(Transaction txn, String user, boolean failed) {
        Key userStatisticsKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", user))
                                        .setKind("UserStats").newKey("userStats");
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = triggerTime.format(myFormatObj);
        myFormatObj = DateTimeFormatter.ofPattern("MM-yyyy");
        String keyDate = triggerTime.format(myFormatObj);
        Entity userStats = txn.get(userStatisticsKey);
        Entity stats = txn.get(statsKey.newKey(keyDate));
        if( userStats == null ) {
            userStats = Entity.newBuilder(userStatisticsKey)
                            .set("user_number_logins", 
                            nonIndexedLong(0L))
                            .set("user_number_failed", 
                            nonIndexedLong(0L))
                            .set("user_first_login", 
                            nonIndexedString(formattedDate))
                            .set("user_last_login", 
                            nonIndexedString(formattedDate))
                            .set("user_last_attempt", 
                            nonIndexedString(formattedDate))
                            .build();
        }
        if( stats == null) {
            stats = Entity.newBuilder(statsKey.newKey(keyDate))    
                            .set("total_number_logins", 
                            nonIndexedLong(0L))
                            .set("total_number_failed", 
                            nonIndexedLong(0L))
                            .set("total_first_login",
                            nonIndexedString(formattedDate))
                            .set("total_last_login", 
                            nonIndexedString(formattedDate))
                            .set("total_last_attempt", 
                            nonIndexedString(formattedDate))
                            .build();
        }
        if(!failed) {
            userStats = Entity.newBuilder(userStats)
                    .set("user_number_logins", nonIndexedLong(1L + userStats.getLong("user_number_logins")))
                    .set("user_last_login", nonIndexedString(formattedDate))
                    .set("user_last_attempt", nonIndexedString(formattedDate))
                    .build();
            stats = Entity.newBuilder(stats)
                    .set("total_number_logins", nonIndexedLong(1L + stats.getLong("total_number_logins")))
                    .set("total_last_login", nonIndexedString(formattedDate))
                    .set("total_last_attempt", nonIndexedString(formattedDate))
                    .build();
        }
        else {
            userStats = Entity.newBuilder(userStats)
                    .set("user_number_failed", nonIndexedLong(1L + userStats.getLong("user_number_failed")))
                    .set("user_last_attempt", nonIndexedString(formattedDate))
                    .build();
            stats = Entity.newBuilder(stats)
                    .set("total_number_failed", nonIndexedLong(1L + stats.getLong("total_number_failed")))
                    .set("total_last_attempt", nonIndexedString(formattedDate))
                    .build();
        }
        txn.put(userStats, stats);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Key tokenKey = tokenKeyFactory.newKey(values.getId());
        Transaction txn = datastore.newTransaction();
        try {

            String username = (String) values.get("username");
            txn.delete(tokenKey);
            txn.commit();
            LOG.fine("User logged out: " + username);
            return Response.ok(g.toJson(username)).build();
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

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    private LongValue nonIndexedLong(Long data) {
        return LongValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

}
