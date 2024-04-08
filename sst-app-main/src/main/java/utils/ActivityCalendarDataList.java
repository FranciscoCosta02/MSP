package utils;

import com.google.cloud.Timestamp;


public class ActivityCalendarDataList {

    public String id;

    public String username; // owner username
    public String startDate;
    public String endDate;
    public String title;
    public String description;
    public String maxParticipants;
    public String numParticipants;


    public ActivityCalendarDataList(){}

    public ActivityCalendarDataList(String id, String username, String startDate, String endDate, String title,
                                    String description,String maxParticipants,String numParticipants) {
        this.id = id;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description = description;
        this.maxParticipants=maxParticipants;
        this.numParticipants=numParticipants;
    }

    public boolean confirmInputs() {
        return checkNull(username) || checkNull(startDate) || checkNull(endDate) || checkNull(title) ||
                checkNull(description);
    }

    public boolean checkNull(String word){
        return word.isEmpty();
    }

    public String generateActivityId(){
        return (username+Timestamp.now()).replaceAll("\\s+",""); // this means id will depend on username, title, and time
    }
}
