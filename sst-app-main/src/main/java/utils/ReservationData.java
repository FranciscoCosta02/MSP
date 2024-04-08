package utils;

public class ReservationData {

    public String roomName;
    public String roomDepartment;
    public String time;
    public String date;
    public String weekDay;
    public String username;
    public ReservationData() {}

    public ReservationData(String roomName, String roomDepartment, String time, String date, String weekDay,
                           String username) {
        this.roomName = roomName;
        this.roomDepartment = roomDepartment;
        this.time = time;
        this.date = date;
        this.weekDay = weekDay;
        this.username = username;
    }
}
