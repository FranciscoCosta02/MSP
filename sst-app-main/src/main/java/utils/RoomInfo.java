package utils;

public class RoomInfo {

        public String name;
        public String department;
        public String openTime;
        public String closeTime;
        public String weekDays;
        public String availability;


        public RoomInfo(){}

        public RoomInfo(String name, String department, String openTime, String closeTime, String weekDays,
                        String availability) {
            this.name = name;
            this.department = department;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.weekDays = weekDays;
            this.availability = availability;
        }
}
