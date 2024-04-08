package resources;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.FeedsInfo;
import utils.JWTValidation;
import utils.MessageData;
import utils.MessageInfo;


import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.newEntityQueryBuilder;

@Path("/feeds")
public class FeedsResource {

    private static final Logger LOG = Logger.getLogger(FeedsResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private static final KeyFactory groupKeyFactory = datastore.newKeyFactory().setKind("Group");
    private static final KeyFactory msgKeyFactory = datastore.newKeyFactory().setKind("Message");
    private final Gson g = new Gson();

    private enum Roles {
        GUEST, STUDENT, TEACHER, STAFF, SU
    }

    public FeedsResource(){}

    private StringValue nonIndexedString(String data) {
        return StringValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response postMessage(@Context HttpServletRequest request, MessageInfo info,
                                @QueryParam("type") String type) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        String sender = (String) values.get("username");
        String[] destinations = info.dest;
        int[] responses = new int[destinations.length];
        MessageData data = new MessageData(sender, destinations[0], info.text);
        Response spam = checkSpam(data);
        if(spam.getStatus() != Response.Status.OK.getStatusCode())
            return spam;
        switch (type) {
            case "user":
                responses[0] = postMessageToUser(values, data).getStatus();
                for(int i = 1; i < destinations.length; i++){
                    data = new MessageData(sender, destinations[i], info.text);
                    responses[i] = postMessageToUser(values, data).getStatus();
                }
                break;
            case "group":
                responses[0] = postMessageToGroup(values, data).getStatus();
                for(int i = 1; i < destinations.length; i++){
                    data = new MessageData(sender, destinations[i], info.text);
                    responses[i] = postMessageToGroup(values, data).getStatus();
                }
                break;
            case "role":
                responses[0] = postMessageToRole(values, data).getStatus();
                for(int i = 1; i < destinations.length; i++){
                    data = new MessageData(sender, destinations[i], info.text);
                    responses[i] = postMessageToGroup(values, data).getStatus();
                }
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST).entity("Type selected does not exist").build();
        }
        return Response.ok(g.toJson(responses)).build();
    }

    private Response checkSpam(MessageData msg) {
        Key senderKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", msg.sender))
                .setKind("Feed").newKey("messages");
        Transaction txn = datastore.newTransaction();
        try {
            Entity messages = txn.get(senderKey);
            if(messages == null) {
                txn.rollback();
                return Response.ok().entity("No spam").build();
            }
            Entity m;
            List<Value<?>> list = messages.getList("sent");
            for(Value<?> v: list) {
                String mid = (String) v.get();
                m = txn.get(msgKeyFactory.newKey(mid));
                String text = m.getString("text");
                if(msg.text.equals(text) && msg.checkSpam(m.getString("creation_date")))
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Spam detected").build();
            }
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

    private void post(MessageData msg, String username) {
        Key destKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                .setKind("Feed").newKey("messages");
        Transaction txn = datastore.newTransaction();
        try {
            Entity messages = txn.get(destKey);
            String type;
            if(username.equals(msg.dest))
                type = "dest";
            else
                type = "sender";

            switch(type){
                case("dest"):
                    if(messages == null) {
                        ListValue rec = ListValue.newBuilder().addValue(msg.id).build();
                        ListValue sent = ListValue.newBuilder().build();
                        messages = Entity.newBuilder(destKey)
                                .set("received", rec)
                                .set("sent", sent).build();
                    } else {
                        ListValue rec = ListValue.newBuilder().set(messages.getList("received"))
                                .addValue(msg.id).build();
                        messages = Entity.newBuilder(destKey)
                                .set("received", rec)
                                .set("sent", messages.getList("sent")).build();
                    }
                    break;
                case("sender"):
                    if(messages == null) {
                        ListValue sent = ListValue.newBuilder().addValue(msg.id).build();
                        ListValue rec = ListValue.newBuilder().build();
                        messages = Entity.newBuilder(destKey)
                                .set("received", rec)
                                .set("sent", sent).build();
                    } else {
                        List<Value<?>> s = messages.getList("sent");
                        ListValue sent = ListValue.newBuilder().set(s)
                                .addValue(msg.id).build();
                        messages = Entity.newBuilder(destKey)
                                .set("received", messages.getList("received"))
                                .set("sent", sent).build();
                    }
                    break;
            }
            txn.put(messages);
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private void postToGroup(MessageData msg) {
        Key destKey = datastore.newKeyFactory().addAncestors(PathElement.of("Group", msg.dest))
                .setKind("Feed").newKey("messages");
        Transaction txn = datastore.newTransaction();
        try{
            Entity messages = txn.get(destKey);
            if(messages == null) {
                ListValue rec = ListValue.newBuilder().addValue(msg.id).build();
                messages = Entity.newBuilder(destKey)
                        .set("received", rec).build();
            } else {
                ListValue rec = ListValue.newBuilder().set(messages.getList("received"))
                        .addValue(msg.id).build();
                messages = Entity.newBuilder(destKey)
                        .set("received", rec).build();
            }
            txn.put(messages);
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private Response postMessageToUser(Claims values, MessageData data) {

        Transaction txn = datastore.newTransaction();
        try {
            String dest = data.dest;
            Key userKey = userKeyFactory.newKey(dest);
            Entity user = txn.get(userKey);
            if(user == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User does not exist!").build();
            }
            String sender = (String) values.get("username");
            if(sender.equals(dest)){
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE)
                        .entity("User cant send messages to himself!").build();
            }
            Key mKey = msgKeyFactory.newKey(data.id);
            Entity message = txn.get(mKey);
            while(message != null) {
                data = new MessageData(sender, dest, data.text);
                mKey = msgKeyFactory.newKey(data.id);
                message = txn.get(mKey);
            }

            message = Entity.newBuilder(mKey)
                    .set("sender", sender)
                    .set("dest", dest)
                    .set("creation_date", nonIndexedString(data.formattedDate))
                    .set("text", nonIndexedString(data.text)).build();
            post(data, sender);
            post(data, dest);
            txn.put(message);
            txn.commit();
            LOG.info("Message was sent successfully.");
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

    private Response postMessageToGroup(Claims values, MessageData data) {

        Transaction txn = datastore.newTransaction();
        try {
            String dest = data.dest;
            Key groupKey = groupKeyFactory.newKey(dest);
            Entity group = txn.get(groupKey);
            if(group == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Group does not exist!").build();
            }
            List<Value<?>> list = group.getList("participants");
            String text = data.text;
            String sender = (String) values.get("username");
            boolean found = false;
            for(Value<?> v: list) {
                String aux = (String) v.get();
                if(aux.equals(sender)) {
                    found = true;
                    break;
                }
            }
            if(!found){
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User is not in group " + dest).build();
            }

            Key mKey = msgKeyFactory.newKey(data.id);
            Entity message = txn.get(mKey);
            while(message != null) {
                data = new MessageData(sender, dest, text);
                mKey = msgKeyFactory.newKey(data.id);
                message = txn.get(mKey);
            }

            for(Value<?> v: list) {
                String aux = (String) v.get();
                if(!aux.equals(sender)) {
                    MessageData msg = new MessageData(data.id, sender, aux, data.formattedDate, data.text);
                    post(msg, aux);
                }
            }
            postToGroup(data);
            post(data, sender);
            message = Entity.newBuilder(mKey)
                    .set("sender", sender)
                    .set("dest", dest)
                    .set("creation_date", nonIndexedString(data.formattedDate))
                    .set("text", nonIndexedString(data.text)).build();
            txn.put(message);
            txn.commit();
            LOG.info("Message was sent successfully.");
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

    private boolean roleExists(String role) {
        boolean found = false;
        for(Roles v: Roles.values()) {
            String var = v.toString();
            if (var.equals(role)) {
                found = true;
                break;
            }
        }
        return found;
    }
    private Response postMessageToRole(Claims values, MessageData data) {
        Transaction txn = datastore.newTransaction();
        try {
            String dest = data.dest;
            if(!roleExists(dest)) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Group does not exist!").build();
            }
            String sender = (String) values.get("username");
            Key mKey = msgKeyFactory.newKey(data.id);
            Entity message = txn.get(mKey);
            while(message != null) {
                data = new MessageData(sender, dest, data.text);
                mKey = msgKeyFactory.newKey(data.id);
                message = txn.get(mKey);
            }
            message = Entity.newBuilder(mKey)
                    .set("sender", sender)
                    .set("dest", dest)
                    .set("creation_date", nonIndexedString(data.formattedDate))
                    .set("text", nonIndexedString(data.text)).build();
            Query<Entity> query = newEntityQueryBuilder().setKind("User")
                    .setFilter(PropertyFilter.eq("role", dest))
                    .build();
            QueryResults<Entity> results = txn.run(query);
            while(results.hasNext()) {
                Entity aux = results.next();
                dest = aux.getKey().getName();
                data = new MessageData(data.id, sender, dest, data.formattedDate, data.text);
                post(data, dest);
            }
            post(data, sender);
            txn.put(message);
            txn.commit();
            LOG.info("Message was sent successfully.");
            return Response.ok(g.toJson(data)).build();
        }  catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }


    /*public Response getUsersFromFeed(@Context HttpServletRequest request, @QueryParam("elements") int elements,
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

            Query<Entity> query = newEntityQueryBuilder().setKind("Message")
                    .setFilter(PropertyFilter.eq("dest", username))
                    .build();
            QueryResults<Entity> results = txn.run(query);

            HashMap<String, List<String>> timers = new HashMap<>();
            PriorityQueue<String> queue = new PriorityQueue<>(Collections.reverseOrder());
            while(results.hasNext()){
                Entity message = results.next();
                String sender = message.getString("sender");
                String creation = message.getString("creation_date");
                List<String> users = timers.get(creation);
                if(users == null) {
                    users = new ArrayList<>(1);
                }
                users.add(sender);
                timers.put(creation, users);
                queue.add(creation);
            }

            query = newEntityQueryBuilder().setKind("Message")
                    .setFilter(PropertyFilter.eq("sender", username))
                    .build();
            results = txn.run(query);
            while(results.hasNext()) {
                Entity message = results.next();
                String dest = message.getString("dest");
                String creation = message.getString("creation_date");
                List<String> users = timers.get(creation);
                if(users == null) {
                    users = new ArrayList<>(1);
                }
                users.add(dest);
                timers.put(creation, users);
                queue.add(creation);
            }

            HashMap<String, String> list = new HashMap<>();
            int start = page*elements;
            while(list.size() < elements && !queue.isEmpty()) {
                String creation_date = queue.poll();
                if(start != 0)
                    start--;
                else {
                    List<String> users = timers.get(creation_date);
                    String username1 = users.remove(0);
                    Key userKey = userKeyFactory.newKey(username1);
                    Entity user = txn.get(userKey);
                    if(list.get(username1) == null) {
                        list.put(username1, user.getString("photo"));
                    }
                }
            }

            txn.commit();
            return Response.ok(g.toJson(list)).build();
        } catch (Exception e) {
            txn.rollback();
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }*/

    @GET
    @Path("/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUsersFromFeed2(@Context HttpServletRequest request, @QueryParam("elements") int elements,
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
            Entity user = txn.get(userKeyFactory.newKey(username));
            HashMap<String, FeedsInfo> list = new HashMap<>();
            if(user == null) {
                txn.rollback();
                return Response.ok(g.toJson(list)).entity("User does not exist").build();
            }
            Key feedKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                    .setKind("Feed").newKey("messages");
            Entity feed = txn.get(feedKey);;
            if(feed == null) {
                txn.rollback();
                return Response.ok(g.toJson(list)).entity("User does not have any messages").build();
            }

            HashMap<String, List<String>> timers = new HashMap<>();
            PriorityQueue<String> queue = new PriorityQueue<>(Collections.reverseOrder());
            String myGroups = user.getString("myGroups");
            int current = 0;
            int start = elements*page;
            List<Value<String>> received = feed.getList("received");
            for(int i = received.size()-1; i >= 0; i--){
                if(start > current)
                    current++;
                else{
                    if(current < elements + start) {
                        String mid = received.get(i).get();
                        Entity msg = txn.get(msgKeyFactory.newKey(mid));
                        if(msg != null) {
                            String sender = msg.getString("sender");
                            String creation = msg.getString("creation_date");
                            List<String> users = timers.get(creation);
                            if(users == null) {
                                users = new ArrayList<>(1);
                            }
                            String dest = msg.getString("dest");
                            Entity destUser = txn.get(userKeyFactory.newKey(dest));
                            if(destUser == null) {
                                Entity destGroup = txn.get(groupKeyFactory.newKey(dest));
                                if(destGroup == null)
                                    users.add("Portal Nova Team");
                                else {
                                    if(myGroups.contains(dest))
                                        users.add(dest);
                                }
                            } else
                                users.add(sender);
                            timers.put(creation, users);
                            queue.add(creation);
                            current++;
                        }
                    } else break;
                }
            }

            current = 0;
            List<Value<String>> sent = feed.getList("sent");
            for(int i =  sent.size() - 1; i >= 0; i--) {
                if(start > current)
                    current++;
                else{
                    if(current < elements + start) {
                        String mid = sent.get(i).get();
                        Entity msg = txn.get(msgKeyFactory.newKey(mid));
                        if(msg != null) {
                            String dest = msg.getString("dest");
                            String creation = msg.getString("creation_date");
                            List<String> users = timers.get(creation);
                            if(users == null) {
                                users = new ArrayList<>(1);
                            }
                            users.add(dest);
                            timers.put(creation, users);
                            queue.add(creation);
                            current++;
                        }
                    } else break;
                }
            }

            while(list.size() < elements && !queue.isEmpty()) {
                String creation_date = queue.poll();
                if(start != 0)
                    start--;
                else {
                    List<String> users = timers.get(creation_date);
                    String name = users.remove(0);
                    Key userKey = userKeyFactory.newKey(name);
                    user = txn.get(userKey);
                    if(user == null)
                        list.putIfAbsent(name, new FeedsInfo("", "group"));
                    else
                        list.putIfAbsent(name, new FeedsInfo(user.getString("photo"), "user"));
                }
            }
            txn.commit();
            return Response.ok(g.toJson(list)).build();
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
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getMessagesWith(@Context HttpServletRequest request, @PathParam("id") String uid,
                                    @QueryParam("elements") int elements, @QueryParam("page") int page) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            if(uid.equals("Portal Nova Team")) {
                String role = (String) values.get("role");
                return getMessagesWithGroup(role, elements, page, txn);
            }
            String username = (String) values.get("username");
            Key userKey = userKeyFactory.newKey(uid);
            Entity user = txn.get(userKey);
            if(user == null)
                return getMessagesWithGroup(uid, elements, page, txn);

            Query<Entity> query = newEntityQueryBuilder().setKind("Message")
                    .setFilter(StructuredQuery.CompositeFilter.and(
                            PropertyFilter.eq("dest", username),
                            PropertyFilter.eq("sender", uid)
                            ))
                    .build();
            QueryResults<Entity> results = txn.run(query);
            HashMap<String, MessageData> timers = new HashMap<>();
            PriorityQueue<String> queue = new PriorityQueue<>(Collections.reverseOrder());
            while(results.hasNext()){
                Entity message = results.next();
                String creation = message.getString("creation_date");
                MessageData data = new MessageData(message.getKey().getName(), uid, username, creation,
                        message.getString("text"));
                timers.put(creation, data);
                queue.add(creation);
            }

            query = newEntityQueryBuilder().setKind("Message")
                    .setFilter(StructuredQuery.CompositeFilter.and(
                            PropertyFilter.eq("sender", username),
                            PropertyFilter.eq("dest", uid)
                    ))
                    .build();
            results = txn.run(query);
            while(results.hasNext()){
                Entity message = results.next();
                String creation = message.getString("creation_date");
                MessageData data = new MessageData(message.getKey().getName(), username, uid, creation,
                        message.getString("text"));
                timers.put(creation, data);
                queue.add(creation);
            }

            List<MessageData> list = new ArrayList<>();
            int start = page*elements;
            while(list.size() < elements && !queue.isEmpty()) {
                String creation_date = queue.poll();
                if(start != 0)
                    start--;
                else {
                    MessageData data = timers.get(creation_date);
                    list.add(data);
                }
            }

            txn.commit();
            return Response.ok(g.toJson(list)).build();
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

    private Response getMessagesWithGroup(String name, int elements, int page, Transaction txn){
        Query<Entity> query = newEntityQueryBuilder().setKind("Message")
                .setFilter(PropertyFilter.eq("dest", name)).build();
        QueryResults<Entity> results = txn.run(query);
        HashMap<String, MessageData> timers = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Collections.reverseOrder());
        while(results.hasNext()){
            Entity message = results.next();
            String creation = message.getString("creation_date");
            String uid = message.getString("sender");
            MessageData data = new MessageData(message.getKey().getName(), uid, name, creation,
                    message.getString("text"));
            timers.put(creation, data);
            queue.add(creation);
        }

        List<MessageData> list = new ArrayList<>();
        int start = page*elements;
        while(list.size() < elements && !queue.isEmpty()) {
            String creation_date = queue.poll();
            if(start != 0)
                start--;
            else {
                MessageData data = timers.get(creation_date);
                list.add(data);
            }
        }
        txn.commit();
        return Response.ok(g.toJson(list)).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeMessage(@Context HttpServletRequest request, @QueryParam("mid") String mid) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String username = (String) values.get("username");
            Key messagesKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", username))
                    .setKind("Feed").newKey("messages");
            Entity messages = txn.get(messagesKey);
            if(messages == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User has no messages!").build();
            }

            List<Value<?>> l = messages.getList("received");
            boolean found = false;
            ListValue.Builder builder = ListValue.newBuilder();
            for (Value<?> value : l) {
                String aux = (String) value.get();
                if (aux.equals(mid)) {
                    found = true;
                } else {
                    builder.addValue(aux);
                }
            }

            if(!found) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User does not have access to this message")
                        .build();
            }

            ListValue list = builder.build();
            messages = Entity.newBuilder(messagesKey)
                    .set("received", list)
                    .set("sent", messages.getList("sent")).build();
            txn.put(messages);
            Key msgKey = msgKeyFactory.newKey(mid);
            Entity msg = txn.get(msgKey);
            if(msg == null) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Message does not exist").build();
            }
            found = false;
            l = msg.getList("dest");
            builder = ListValue.newBuilder();
            for (Value<?> value : l) {
                String aux = (String) value.get();
                if (aux.equals(username)) {
                    found = true;
                } else {
                    builder.addValue(aux);
                }
            }
            if(!found) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Message is not viewed by " + username)
                        .build();
            }
            if(l.size() - 1 == 0) {
                messagesKey = datastore.newKeyFactory().addAncestors(PathElement.of("User", msg.getString("sender")))
                        .setKind("Feed").newKey("messages");
                messages = txn.get(messagesKey);
                l = messages.getList("sent");
                builder = ListValue.newBuilder();
                for (Value<?> value : l) {
                    String aux = (String) value.get();
                    if (!aux.equals(mid)) {
                        builder.addValue(aux);
                    }
                }
                list = builder.build();
                messages = Entity.newBuilder(messagesKey)
                                .set("received", messages.getList("received"))
                                .set("sent", list).build();
                txn.put(messages);
                txn.delete(msgKey);
                txn.commit();
                return Response.ok().entity("Message removed completely from datastore").build();
            }
            list = builder.build();
            msg = Entity.newBuilder(msgKey)
                    .set("sender", msg.getString("sender"))
                    .set("dest", list)
                    .set("creation_date", msg.getString("creation_date"))
                    .set("text", msg.getString("text")).build();
            txn.put(msg);
            txn.commit();
            return Response.ok().entity("Message removed from " + username + "'s feed").build();
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
