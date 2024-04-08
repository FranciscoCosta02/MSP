package resources;


import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.*;

import static resources.SendEmailResource.sendEmail;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import static com.google.cloud.datastore.Query.newEntityQueryBuilder;

@Path("/rooms")
public class RoomResource {
    private static final Logger LOG = Logger.getLogger(RoomResource.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson g = new Gson();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");
    private static final KeyFactory roomKeyFactory = datastore.newKeyFactory().setKind("Room");
    private static final KeyFactory resKeyFactory = datastore.newKeyFactory().setKind("Reservation");

    public RoomResource(){}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response createRoom(@Context HttpServletRequest request, RoomData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User has no permission to create rooms!").build();
            }
            String key = data.department + "-" + data.name;
            Key roomKey = roomKeyFactory.newKey(key);
            Entity room = txn.get(roomKey);
            if(room != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Room already exists in the database")
                        .build();
            }

            room = Entity.newBuilder(roomKey).set("name", data.name).set("department", data.department)
                    .set("openTime", nonIndexedString(data.openTime)).set("closeTime", nonIndexedString(data.closeTime))
                    .set("weekDays", nonIndexedString(data.weekDays)).set("availability", "Available").build();
            txn.put(room);
            txn.commit();
            return Response.ok("Room created successfully").build();
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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateRoomHours(@Context HttpServletRequest request, RoomDataAvailability data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User has no permission to create rooms!").build();
            }

            String key = data.department + "-" + data.name;
            Key roomKey = roomKeyFactory.newKey(key);
            Entity room = txn.get(roomKey);
            if(room == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Room does not exist in the database")
                        .build();
            }

            room = Entity.newBuilder(room).set("openTime", nonIndexedString(data.openTime))
                    .set("closeTime", nonIndexedString(data.closeTime))
                    .set("weekDays", nonIndexedString(data.weekDays)).build();
            txn.update(room);
            txn.commit();
            return Response.ok("Room hours updated successfully").build();
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
    @Path("/availability")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateRoomsAvailability(@Context HttpServletRequest request, RoomInfo data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("STAFF") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User has no permission to update rooms!").build();
            }
            String key = data.department + "-" + data.name;
            Key roomKey = roomKeyFactory.newKey(key);
            Entity room = txn.get(roomKey);
            if(room == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Room does not exist in the database")
                        .build();
            }
            String msg;
            if(data.availability.equals("Available")) {
                room = Entity.newBuilder(room).set("availability", "Unavailable").build();
                msg = "Room is now unavailable to reserve!";
            } else {
                room = Entity.newBuilder(room).set("availability", "Available").build();
                msg = "Room is now available to reserve!";
            }
            txn.update(room);
            txn.commit();
            return Response.ok(msg).build();
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
    @Path("/allRooms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response updateAllRoomsAvailability(@Context HttpServletRequest request,
                                               @QueryParam("state") String state) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("STAFF") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User has no permission to update rooms!").build();
            }
            Query<Entity> query =  newEntityQueryBuilder().setKind("Room")
                    .setFilter(PropertyFilter.neq("availability", state)).build();
            QueryResults<Entity> results = txn.run(query);
            int num = 0;
            while(results.hasNext()) {
                Entity aux = results.next();
                aux = Entity.newBuilder(aux).set("availability", state).build();
                txn.update(aux);
                num++;
            }
            txn.commit();
            return Response.ok(num + " rooms were changed successfully!").build();
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
    public Response deleteRoom(@Context HttpServletRequest request, RoomDataAvailability data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String role = (String) values.get("role");
            if(!role.equals("SU") && !role.equals("STAFF") && !role.equals("STAFF")) {
                txn.rollback();
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("User has no permission to create rooms!").build();
            }

            String key = data.department + "-" + data.name;
            Key roomKey = roomKeyFactory.newKey(key);
            Entity room = txn.get(roomKey);
            if(room == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Room does not exist in the database")
                        .build();
            }
            txn.delete(roomKey);
            txn.commit();
            return Response.ok("Room deleted successfully").build();
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

    @POST
    @Path("/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response reserveRoom(@Context HttpServletRequest request, CreateReservationData data) {
        LOG.warning("reserveRoom");
        LOG.warning(data.fullTime);
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Claims values = jwt.getBody();
        Transaction txn = datastore.newTransaction();
        try {
            String key = data.roomDepartment + "-" + data.roomName;
            Key roomKey = roomKeyFactory.newKey(key);
            Entity room = txn.get(roomKey);
            if(room == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Room does not exist in the database")
                        .build();
            }
            String username = (String) values.get("username");
            Key userKey = userKeyFactory.newKey(username);
            Entity user = txn.get(userKey);
            if(user == null && username.equals(data.username)) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("User does not exist!").build();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateInString = formatter.format(new Date());
            if(dateInString.compareTo(data.date) > 0) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Selected day already passed!").build();
            }
            if(!room.getString("weekDays").contains(data.weekDay) ||
                    room.getString("availability").equals("Unavailable")) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Room is closed on selected day").build();
            }
            String role = (String) values.get("role");
            String[] times = data.time.split(",");
            int[] responses = new int[times.length];
            for (int i = 0; i < times.length; i++) {
                data = new CreateReservationData(data.roomName, data.roomDepartment, times[i], data.date, data.weekDay,
                        data.username,data.fullTime);
                Response response = reserveRoom(txn, data, room, key, role);
                responses[i] = response.getStatus();
            }
            txn.commit();

            String[] answers = new String[times.length];
            for(int i = 0; i < responses.length; i++) {
                int response = responses[i];
                switch (response) {
                    case 200:
                        answers[i] = "Room reserved for " + times[i];
                        break;
                    case 400:
                        answers[i] = "Room is closed for the selected time!";
                        break;
                    case 406:
                        answers[i] = "Room is closed for the hours selected!";
                        break;
                }
            }
            return Response.ok(g.toJson(answers)).build();
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

    private TimestampValue nonIndexedTimeStamp(Timestamp data) {
        return TimestampValue.newBuilder(data).setExcludeFromIndexes(true).build();
    }

    private Response reserveRoom(Transaction txn, CreateReservationData data, Entity room, String key, String role) {
        try{
            int openTime = Integer.parseInt(room.getString("openTime").split(":")[0]);
            int closeTime = Integer.parseInt(room.getString("closeTime").split(":")[0]);
            int selectedTime = Integer.parseInt(data.time.split(":")[0]);
            if(openTime > selectedTime || selectedTime > closeTime) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Room is closed for the selected time!")
                        .build();
            }

            String res = key + "-" + data.time + " " + data.date;
            Key resKey = resKeyFactory.newKey(res);
            Entity reservation = txn.get(resKey);
            if(role.equals("STAFF") || role.equals("SU")) {
                if(reservation != null) {
                    String user = reservation.getString("user");
                    Key userKey = userKeyFactory.newKey(user);
                    Entity userE = txn.get(userKey);
                    if(userE != null) {
                        String type = "reservation," + res;
                        sendEmail(userE.getString("email"), type);
                    }
                }
            } else {
                if(reservation != null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Room is already booked for the hours selected!").build();
                }
            }
            reservation = Entity.newBuilder(resKey).set("room", key).set("time", nonIndexedString(data.time)).set("date", data.date)
                    .set("weekDay",nonIndexedString(data.weekDay)).set("user", data.username).set("expireAt", nonIndexedTimeStamp(Timestamp.parseTimestamp(data.fullTime))).build();
            txn.put(reservation);
            LOG.info("Room reserved for " + data.time);
            return Response.ok("Room reserved for " + data.time).build();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity("Error: Try again later").build();
        }
    }

    @GET
    @Path("/{room}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response listReservationsRoom(@Context HttpServletRequest request, @PathParam("room") String room,
                                         @QueryParam("date") String date) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {
            Query<Entity> query = newEntityQueryBuilder().setKind("Reservation")
                    .setFilter(CompositeFilter.and(
                            PropertyFilter.eq("room", room),
                            PropertyFilter.eq("date", date)
                    )).build();
            QueryResults<Entity> results = txn.run(query);
            List<ReservationData> list = new ArrayList<>();
            while(results.hasNext()) {
                Entity aux = results.next();
                String[] roomData = room.split("-");
                ReservationData data = new ReservationData(roomData[1], roomData[0], aux.getString("time"),
                        aux.getString("date"), aux.getString("weekDay"), aux.getString("user"));
                list.add(data);
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

    @DELETE
    @Path("/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response deleteReservation(@Context HttpServletRequest request, ReservationData data) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {
            Claims values = jwt.getBody();
            String username = (String) values.get("username");
            String role = (String) values.get("role");
            if(!username.equals(data.username) && !role.equals("SU") && !role.equals("STAFF") && !role.equals("ADMIN")) {
                txn.rollback();
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("User can not delete this reservation!")
                        .build();
            }
            String res = data.roomDepartment + "-" + data.roomName + "-" + data.time + " " + data.date;
            Key resKey = resKeyFactory.newKey(res);
            Entity reservation = txn.get(resKey);
            if(reservation == null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Reservation does not exist").build();
            }
            txn.delete(resKey);
            txn.commit();
            return Response.ok("Reservation deleted successfully").build();
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
    @Path("/reservations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getUserReservations(@Context HttpServletRequest request) {
        String id = request.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt = JWTValidation.parseJwt(id);
        if(jwt == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Try again later").build();
        Transaction txn = datastore.newTransaction();
        try {
            Claims values = jwt.getBody();
            String username = (String) values.get("username");
            Query<Entity> query = newEntityQueryBuilder().setKind("Reservation")
                    .setFilter(PropertyFilter.eq("user", username)).build();

            QueryResults<Entity> results = txn.run(query);
            List<ReservationData> list = new ArrayList<>();
            String comparing = convertTimestamp(System.currentTimeMillis());
            while(results.hasNext()) {
                Entity aux = results.next();
                String date = aux.getString("date");
                String time = aux.getString("time");
                String combined = aux.getString("date") + " " + aux.getString("time");
                if(combined.compareTo(comparing)>0){
                    String[] roomData = aux.getString("room").split("-");
                    ReservationData data = new ReservationData(roomData[1], roomData[0], time,
                            date, aux.getString("weekDay"), username);
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

    private String convertTimestamp(long creationTime) {
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTime),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return  triggerTime.format(myFormatObj);
    }
}
