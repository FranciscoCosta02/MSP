package utils;

import com.google.cloud.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ActivityCalendarData {

    public String id;

    public String username; // owner username
    public String startDate;
    public String endDate;
    public String title;
    public String description;
    public String maxParticipants;


    public ActivityCalendarData(){}

    public ActivityCalendarData(String id, String username, String startDate, String endDate, String title,
                                String description,String maxParticipants) {
        this.id = id;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.maxParticipants = maxParticipants;
    }

    public boolean confirmInputs() {
        return checkNull(username) || checkNull(startDate) || checkNull(endDate) || checkNull(title) ||
                checkNull(description);
    }

    public boolean checkNull(String word){
        return word.isEmpty();
    }

    public String generateActivityId(){
        return (username+"_"+Timestamp.now()).replaceAll("\\s+",""); // this means id will depend on username, title, and time
    }
}
