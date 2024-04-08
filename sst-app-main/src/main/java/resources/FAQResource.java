package resources;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.FAQData;
import utils.JWTValidation;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/faq")
public class FAQResource {
    private static final Logger LOG = Logger.getLogger(FAQResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory tokenKeyFactory = datastore.newKeyFactory().setKind("Token");
    private static final KeyFactory faqKeyFactory = datastore.newKeyFactory().setKind("FAQ");
    private final Gson g = new Gson();

    public FAQResource(){}

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createFAQ(@Context HttpServletRequest request, FAQData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Key faqKey = faqKeyFactory.newKey(data.question);
            Entity faq = Entity.newBuilder(faqKey).set("answer", nonIndexedString(data.answer)).set("tag",nonIndexedString(data.tag)).build();
            txn.put(faq);
            LOG.info("FAQ was created successfully.");
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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateFAQ(@Context HttpServletRequest request, FAQData data){
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Key faqKey = faqKeyFactory.newKey(data.question);
            Entity faq = txn.get(faqKey);
            if(faq == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: FAQ does not exist").build();
            }
            faq = Entity.newBuilder(faq).set("answer", nonIndexedString(data.answer)).set("tag",nonIndexedString(data.tag)).build();
            txn.update(faq);
            txn.commit();
            LOG.info("FAQ was updated successfully.");
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

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteFAQ(@Context HttpServletRequest request, @QueryParam("id") String faqID){
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if (!role.equals("SU") && !role.equals("SERVICE") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.FORBIDDEN).entity("Error: No Permissions!").build();
            }
            Key faqKey = faqKeyFactory.newKey(faqID);
            Entity faq = txn.get(faqKey);
            if(faq == null){
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Error: FAQ does not exist").build();
            }
            txn.delete(faqKey);
            txn.commit();
            LOG.info("FAQ was deletes successfully.");
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
