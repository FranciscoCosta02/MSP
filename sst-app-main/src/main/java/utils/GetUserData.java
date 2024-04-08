package utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.regex.Pattern;

public class GetUserData {

    public String username;
    public String password;
    public String confirmation;
    public String email;
    public String name;
    public String role;
    public String activity;
    public String privacy;
    public String phone;
    public String department;
    public String photo;
    public String groups;
    public String average;
    public String ects;

    public GetUserData(){}
    public GetUserData(String username, String email, String name, String password, String confirmation,
                       String role, String activity, String privacy, String phone,
                       String department, String photo, String groups,String average,String ects) {
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email=email;
        this.name = name;
        this.role = role;
        this.activity = activity;
        this.privacy = privacy;
        this.phone = phone;
        this.department = department;
        this.photo = photo;
        this.groups = groups;
        this.average = average;
        this.ects = ects;
    }


}
