package utils;


public class RoomData {

    public String name;
    public String department;
    public String openTime;
    public String closeTime;
    public String weekDays;


    public RoomData(){}

    public RoomData(String name, String department, String openTime, String closeTime, String weekDays) {
        this.name = name;
        this.department = department;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.weekDays = weekDays;
    }
}
