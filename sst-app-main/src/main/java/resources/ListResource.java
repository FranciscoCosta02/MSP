package resources;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import com.google.cloud.datastore.StructuredQuery.*;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.*;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.*;


@Path("/list")
public class ListResource {
    private static final Logger LOG = Logger.getLogger(ListResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory msgKeyFactory = datastore.newKeyFactory().setKind("Message");
    private final KeyFactory groupKeyFactory = datastore.newKeyFactory().setKind("Group");
    private final KeyFactory faqKeyFactory = datastore.newKeyFactory().setKind("FAQ");
    private final Gson g = new Gson();
    public ListResource() {
    }

    @GET
    @Path("/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listUsers(@PathParam("type") String type, @QueryParam("elements") int elements,
                              @QueryParam("cursor") String cursor, @QueryParam("pattern") String pattern,
                              @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        LOG.fine("Attempt to list users");
        Transaction txn = datastore.newTransaction();
        try {
            id = id.substring("Bearer".length()).trim();
            Jws<Claims> jwt = JWTValidation.parseJwt(id);
            if(jwt == null)
                return Response.status(Status.BAD_REQUEST).entity("Error: Try again later").build();
            Claims values = jwt.getBody();
            String role = (String) values.get("role");
            cursor = cursor.split("\\\\")[0];
            QueryResults<Entity> results = txn.run(createUserQuery(elements, cursor, type));
            List<UserData> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                String username = aux.getKey().getName();
                if(username.contains(pattern)) {
                    list.add(getUserData(aux, role, username));
                }
            }
            Cursor newCursor = results.getCursorAfter();
            while(list.size() < elements && results.getMoreResults().getNumber() == 2) {
                results = txn.run(createUserQuery(elements, newCursor.toUrlSafe(), type));
                while(results.hasNext() && list.size() < elements) {
                    Entity aux = results.next();
                    String username = aux.getKey().getName();
                    if(username.contains(pattern)) {
                        list.add(getUserData(aux, role, username));
                    }
                }
                newCursor = results.getCursorAfter();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("cursor", newCursor.toUrlSafe());
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    @GET
    @Path("/backOffice/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listUsers(@PathParam("type") String type, @QueryParam("elements") int elements,
                              @QueryParam("page") int page, @QueryParam("pattern") String pattern,
                              @Context HttpServletRequest request) {
        String Headerid = request.getHeader("Authorization");
        Headerid = Headerid.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(Headerid);
        }
        catch (NoSuchElementException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid token").build();
        }
        assert jwt != null;
        Claims values = jwt.getBody();

        LOG.fine("Attempt to list users");
        Transaction txn = datastore.newTransaction();
        try {
            int start = page*elements;
            EntityQuery.Builder q = newEntityQueryBuilder().setKind("User")
                    .setOffset(start)
                    .setLimit(elements);
            String role = (String) values.get("role");
            if(!role.equals("SU")){
                if(role.equals("STAFF") || role.equals("ADMIN")){
                    if (!type.equals("ALL"))
                        q.setFilter(CompositeFilter.and(PropertyFilter.neq("role", "SU"),PropertyFilter.eq("role", type)));
                    else
                        q.setFilter(PropertyFilter.neq("role", "SU"));
                }
                else{
                    if (!type.equals("ALL"))
                        q.setFilter(CompositeFilter.and(PropertyFilter.eq("activity", "Active"),CompositeFilter.and(PropertyFilter.neq("role", "SU"),PropertyFilter.eq("role", type))));
                    else
                        q.setFilter(CompositeFilter.and(PropertyFilter.eq("activity", "Active"),PropertyFilter.neq("role", "SU")));
                }

            }
            else if (!type.equals("ALL"))
                q.setFilter(PropertyFilter.eq("role", type));

            QueryResults<Entity> results = txn.run(q.build());
            List<UserData> list = new ArrayList<>();
            int maxNumber = start;
            while(results.hasNext()) {
                Entity aux = results.next();
                if(list.size() < elements){
                    String username = aux.getKey().getName();
                    if(username.contains(pattern)) {
                        UserData user = getUserData(aux, role, username);
                        if(user!=null){
                            maxNumber++;
                            list.add(user);
                        }

                    }
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    private EntityQuery createUserQuery(int elements, String startCursor, String type) {
        EntityQuery.Builder q = newEntityQueryBuilder().setKind("User")
                .setLimit(elements);
        if(!startCursor.isEmpty())
            q.setStartCursor(Cursor.fromUrlSafe(startCursor));
        if (!type.equals("ALL"))
            q.setFilter(CompositeFilter.and(PropertyFilter.eq("activity", "Active"),PropertyFilter.eq("role", type)));
        else
            q.setFilter(PropertyFilter.eq("activity", "Active"));
        return q.build();
    }

    private UserData getUserData(Entity aux, String role, String username) {
        String name = aux.getString("name");
        String activity = aux.getString("activity");
        String groups = aux.getString("groups");
        String r = aux.getString("role");
        UserData data = null;
        if(role.equals("SU")) {
            data = new UserData(username, aux.getString("email"),
                    name, "", "",
                    r, activity,
                    aux.getString("privacy"),
                    aux.getString("phone"),
                    aux.getString("department"), aux.getString("photo"),
                    aux.getString("groups"));
        } else if(!r.equals("SU")) {
            if (groups.contains(role)) {
                data = new UserData(username, aux.getString("email"),
                        name, "", "",
                        r, activity,
                        aux.getString("privacy"),
                        aux.getString("phone"),
                        aux.getString("department"),
                        aux.getString("photo"), "");
            } else {
                String privacy = aux.getString("privacy");
                data = new UserData(username, getProperty(aux, "email", privacy),
                        name, "", "",
                        getProperty(aux, "role", privacy), "Active", "",
                        getProperty(aux, "phone", privacy),
                        getProperty(aux, "department", privacy),
                        aux.getString("photo"), "");
            }
        }
        return data;
    }

    private String getProperty(Entity user, String property, String privacy) {
        if(privacy.contains(property))
            return "";
        else
            return user.getString(property);
    }

    @GET
    @Path("/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listGroups(@Context HttpServletRequest request,@QueryParam("elements") int elements,
                               @QueryParam("cursor") String cursor, @QueryParam("pattern") String pattern){
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Status.BAD_REQUEST).entity("Error: Try again later").build();
        LOG.fine("Attempt to list groups");
        Transaction txn = datastore.newTransaction();
        try {
            cursor = cursor.split("\\\\")[0];
            EntityQuery.Builder q = newEntityQueryBuilder().setKind("Group")
                    .setLimit(elements);
            if(!cursor.isEmpty())
                q.setStartCursor(Cursor.fromUrlSafe(cursor));

            QueryResults<Entity> results = txn.run(q.build());
            List<GroupInfo> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                String name = aux.getKey().getName();
                if(name.contains(pattern)) {
                    GroupInfo data = new GroupInfo(name, aux.getString("privacy"),
                            aux.getString("owner"), aux.getList("participants"));
                    list.add(data);
                }
            }
            Cursor newCursor = results.getCursorAfter();
            while(list.size() < elements && results.getMoreResults().getNumber() == 2) {
                q = newEntityQueryBuilder().setKind("Group")
                        .setLimit(elements).setStartCursor(newCursor);
                results = txn.run(q.build());
                while(results.hasNext() && list.size() < elements) {
                    Entity aux = results.next();
                    String name = aux.getKey().getName();
                    if(name.contains(pattern)) {
                        GroupInfo data = new GroupInfo(name, aux.getString("privacy"),
                                aux.getString("owner"), aux.getList("participants"));
                        list.add(data);
                    }
                }
                newCursor = results.getCursorAfter();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("cursor", newCursor.toUrlSafe());
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    @GET
    @Path("/backOffice/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listGroupsBO(@PathParam("type") String type, @QueryParam("elements") int elements,
                              @QueryParam("page") int page, @QueryParam("pattern") String pattern,
                              @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        LOG.fine("Attempt to list users");
        Transaction txn = datastore.newTransaction();
        try {
            id = id.substring("Bearer".length()).trim();
            Jws<Claims> jwt = JWTValidation.parseJwt(id);
            if(jwt == null)
                return Response.status(Status.BAD_REQUEST).entity("Error: Try again later").build();
            int start = page*elements;
            EntityQuery.Builder q = newEntityQueryBuilder().setKind("Group")
                    .setOffset(start)
                    .setLimit(elements);
            QueryResults<Entity> results = txn.run(q.build());
            List<GroupInfo> list = new ArrayList<>();
            int maxNumber = start;
            while(results.hasNext()) {
                Entity aux = results.next();

                if(list.size() < elements){
                    String name = aux.getKey().getName();
                    if(name.contains(pattern)) {
                        GroupInfo data = new GroupInfo(name, aux.getString("privacy"),
                                aux.getString("owner"), aux.getList("participants"));
                        maxNumber++;
                        list.add(data);
                    }
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    @GET
    @Path("/received")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getReceivedMessages(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                                        @QueryParam("page") int page) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {

            String username = (String) values.get("username");
            Key msgKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                    .setKind("Feed").newKey("messages");
            Entity messages = txn.get(msgKey);
            List<MessageData> l = new ArrayList<>();
            if(messages == null) {
                txn.rollback();
                return Response.ok(g.toJson(l)).build();
            }
            List<Value<?>> list = messages.getList("received");
            int current = 0;
            int maxNumber = list.size();
            int start = page*elements;
            while(current < maxNumber){
                if(l.size() == elements || list.size() < current)
                    break;
                if(current < start)
                    current++;
                else{
                    String aux = (String) list.get(current).get();
                    Key msg = msgKeyFactory.newKey(aux);
                    Entity message = txn.get(msg);
                    String mid = message.getKey().getName();
                    MessageData data = new MessageData(mid, message.getString("sender"),
                            message.getString("dest"), message.getString("creation_date"),
                            message.getString("text"));
                    l.add(data);
                    current++;
                }
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(l));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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
    @Path("/sent")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getSentMessages(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                                    @QueryParam("page") int page) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {

            String username = (String) values.get("username");
            Key msgKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                    .setKind("Feed").newKey("messages");
            Entity messages = txn.get(msgKey);
            List<MessageData> l = new ArrayList<>();
            if(messages == null) {
                txn.rollback();
                return Response.ok(l).build();
            }

            List<Value<?>> list = messages.getList("sent");
            if(list.isEmpty()) {
                txn.rollback();
                return Response.ok(l).build();
            }

            int current = 0;
            int maxNumber = list.size();
            int start = page*elements;
            while(current < maxNumber){
                if(l.size() == elements || list.size() < current)
                    break;
                if(current < start)
                    current++;
                else{
                    String aux = (String) list.get(current).get();
                    Key msg = msgKeyFactory.newKey(aux);
                    Entity message = txn.get(msg);
                    String mid = message.getKey().getName();
                    MessageData data = new MessageData(mid, message.getString("sender"),
                            message.getString("dest"), message.getString("creation_date"),
                            message.getString("text"));
                    l.add(data);
                    current++;
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(l));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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
    @Path("/faqs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listFAQS(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                             @QueryParam("page") int page, @QueryParam("pattern") String pattern) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {

            int start = elements*page;
            EntityQuery.Builder query = newEntityQueryBuilder().setKind("FAQ").setOffset(start)
                    .setLimit(elements);
            if(!pattern.isEmpty())
                query.setFilter(PropertyFilter.ge("__key__", faqKeyFactory.newKey(pattern)));
            QueryResults<Entity> results = txn.run(query.build());
            int maxNumber = results.getSkippedResults();
            List<FAQData> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                maxNumber++;
                FAQData data = new FAQData(aux.getKey().getName(), aux.getString("answer"),aux.getString("tag"));
                list.add(data);

            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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
    @Path("/faqs/app")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listFAQS(@Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {

            EntityQuery.Builder query = newEntityQueryBuilder().setKind("FAQ");

            QueryResults<Entity> results = txn.run(query.build());
            List<FAQData> faqsList = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                FAQData data = new FAQData(aux.getKey().getName(), aux.getString("answer"),aux.getString("tag"));
                faqsList.add(data);

            }
            txn.commit();
            return Response.ok(g.toJson(faqsList)).build();
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
    @Path("/rooms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listRooms(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                              @QueryParam("page") int page, @QueryParam("pattern") String pattern) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {
            int start = elements*page;
            EntityQuery.Builder query = newEntityQueryBuilder().setKind("Room").setOffset(start)
                    .setLimit(elements);
            if(!pattern.isEmpty())
                query.setFilter(CompositeFilter.and(PropertyFilter.ge("name", pattern),
                        PropertyFilter.eq("availability", "Available")));
            else
                query.setFilter(PropertyFilter.eq("availability", "Available"));
            QueryResults<Entity> results = txn.run(query.build());
            int maxNumber = results.getSkippedResults();
            List<RoomInfo> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                maxNumber++;
                RoomInfo data = new RoomInfo(aux.getString("name"), aux.getString("department"),
                        aux.getString("openTime"), aux.getString("closeTime"),
                        aux.getString("weekDays"), aux.getString("availability"));
                list.add(data);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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
    @Path("/backOffice/rooms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listRoomsBO(@PathParam("type") String type, @QueryParam("elements") int elements,
                                 @QueryParam("page") int page, @QueryParam("pattern") String pattern,
                                 @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        LOG.fine("Attempt to list users");
        Transaction txn = datastore.newTransaction();
        try {
            id = id.substring("Bearer".length()).trim();
            Jws<Claims> jwt = JWTValidation.parseJwt(id);
            if(jwt == null)
                return Response.status(Status.BAD_REQUEST).entity("Error: Try again later").build();
            int start = page*elements;
            EntityQuery.Builder q = newEntityQueryBuilder().setKind("Room")
                    .setOffset(start)
                    .setLimit(elements);
            QueryResults<Entity> results = txn.run(q.build());
            List<RoomInfo> list = new ArrayList<>();
            int maxNumber = start;
            while(results.hasNext()) {
                Entity aux = results.next();

                if(list.size() < elements){
                    String name = aux.getKey().getName();
                    if(name.contains(pattern)) {
                        String[] roomData = name.split("-");
                        RoomInfo data = new RoomInfo(roomData[1], roomData[0], aux.getString("openTime"),
                                aux.getString("closeTime"), aux.getString("weekDays"),
                                aux.getString("availability"));
                        maxNumber++;
                        list.add(data);
                    }
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    @GET
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listReservations(@Context HttpServletRequest request, @QueryParam("elements") int elements,
                              @QueryParam("page") int page, @QueryParam("pattern") String pattern) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {
            int start = elements*page;
            EntityQuery.Builder query = newEntityQueryBuilder().setKind("Reservation").setOffset(start)
                    .setLimit(elements);
            if(!pattern.isEmpty())
                query.setFilter(PropertyFilter.ge("room", pattern));

            QueryResults<Entity> results = txn.run(query.build());
            int maxNumber = results.getSkippedResults();
            List<ReservationData> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                maxNumber++;
                String time = aux.getString("date") + " " + aux.getString("time");
                if(time.equals(convertTimestamp(System.currentTimeMillis()))){
                    String[] roomData = aux.getString("room").split("-");
                    ReservationData data = new ReservationData(roomData[1], roomData[0], time,
                            aux.getString("date"), aux.getString("weekDay"), aux.getString("user"));
                    list.add(data);
                }
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list",g.toJson(list));
            jsonObject.addProperty("maxNumber", maxNumber);
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    private String convertTimestamp(long time) {
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return triggerTime.format(myFormatObj);
    }

    @GET
    @Path("/activities")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listActivities(@QueryParam("elements") int elements, @QueryParam("cursor") String cursor,
                                   @QueryParam("pattern") String pattern, @Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        LOG.fine("Attempt to list users");
        Transaction txn = datastore.newTransaction();
        try {
            id = id.substring("Bearer".length()).trim();
            Jws<Claims> jwt = JWTValidation.parseJwt(id);
            if(jwt == null)
                return Response.status(Status.BAD_REQUEST).entity("Error: Try again later").build();
            cursor = cursor.split("\\\\")[0];
            QueryResults<Entity> results = txn.run(createActivityQuery(elements, cursor));
            List<ActivityCalendarDataList> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                String aid = aux.getKey().getName();
                String title = aux.getString("title");
                if(title.contains(pattern)) {
                    List<Value<?>> participants = aux.getList("participants");
                    String size = String.valueOf(participants.size());
                    ActivityCalendarDataList data = new ActivityCalendarDataList(aid, aux.getString("owner"),
                            aux.getString("startDate"), aux.getString("endDate"), title,
                            aux.getString("description"), aux.getString("maxParticipants"),
                            size);
                    list.add(data);
                }
            }
            Cursor newCursor = results.getCursorAfter();
            while(list.size() < elements && results.getMoreResults().getNumber() == 2) {
                results = txn.run(createActivityQuery(elements, newCursor.toUrlSafe()));
                while(results.hasNext() && list.size() < elements) {
                    Entity aux = results.next();
                    String aid = aux.getKey().getName();
                    String title = aux.getString("title");
                    if(title.contains(pattern)) {
                        List<Value<?>> participants = aux.getList("participants");
                        String size = String.valueOf(participants.size());
                        ActivityCalendarDataList data = new ActivityCalendarDataList(aid, aux.getString("owner"),
                                aux.getString("startDate"), aux.getString("endDate"), title,
                                aux.getString("description"), aux.getString("maxParticipants"),
                                size);
                        list.add(data);
                    }
                }
                newCursor = results.getCursorAfter();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("list", g.toJson(list));
            jsonObject.addProperty("cursor", newCursor.toUrlSafe());
            txn.commit();
            return Response.ok(g.toJson(jsonObject)).build();
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

    private EntityQuery createActivityQuery(int elements, String startCursor) {
        EntityQuery.Builder q = newEntityQueryBuilder().setKind("Activity")
                .setLimit(elements)
                .setFilter(StructuredQuery.PropertyFilter.ge("expireAt", Timestamp.now()));

        if(!startCursor.isEmpty())
            q.setStartCursor(Cursor.fromUrlSafe(startCursor));
        return q.build();
    }
}
