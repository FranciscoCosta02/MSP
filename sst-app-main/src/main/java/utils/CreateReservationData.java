package utils;

public class CreateReservationData {

    public String roomName;
    public String roomDepartment;
    public String time;
    public String date;
    public String weekDay;
    public String username;
    public String fullTime;
    public CreateReservationData() {}

    public CreateReservationData(String roomName, String roomDepartment, String time, String date, String weekDay,
                                 String username,String fullTime) {
        this.roomName = roomName;
        this.roomDepartment = roomDepartment;
        this.time = time;
        this.date = date;
        this.weekDay = weekDay;
        this.username = username;
        this.fullTime = fullTime;
    }
}
