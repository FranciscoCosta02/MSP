package utils;

import com.google.cloud.Timestamp;

import java.util.Date;


public class MyActivities {
    public String id;
    public String username; // owner username
    public String startDate;
    public String endDate;
    public String title;
    public String description;


    public MyActivities(){}

    public MyActivities(String id, String username, String startDate, String endDate, String title,String description) {
        this.id=username+"_"+id;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.description=description;
    }

    public boolean confirmInputs() {
        return checkNull(username) || checkNull(title);
    }

    public boolean checkNull(String word){
        return word.isEmpty();
    }

    public String generateActivityId(){
        return (username+Timestamp.now()).replaceAll("\\s+",""); // this means id will depend on username, title, and time
    }
}
