package utils;

import javax.ws.rs.core.Response;
import java.util.regex.Pattern;

public class GroupData {

    public String name;
    public String privacy;
    public String password;
    public String confirmation;

    public GroupData() {}

    public GroupData(String name, String privacy, String password, String confirmation) {
        this.name = name;
        this.privacy = privacy;
        this.password = password;
        this.confirmation = confirmation;
    }

    public boolean checkNull(String word){
        return word.isEmpty() || word == null;
    }

    public Response pwdValid() {
        Pattern upperCase = Pattern.compile("[A-Z ]");
        Pattern digitCase = Pattern.compile("[0-9 ]");
        if (!password.equals(confirmation))
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Passwords do not match").build();
        if(!upperCase.matcher(password).find())
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password must have at least 1 upper case character").build();
        if(!digitCase.matcher(password).find())
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password must have at least 1 digit character").build();
        return Response.ok().entity("Password is valid").build();
    }
}
