package utils;

import javax.ws.rs.core.Response;
import java.util.regex.Pattern;

public class RecoverPasswordData {

    public String newPwd;
    public String confirmation;

    public RecoverPasswordData() {}

    public RecoverPasswordData(String oldPwd, String newPwd, String confirmation) {
        this.newPwd = newPwd;
        this.confirmation = confirmation;
    }

    public Response validPwd() {
        Pattern specialChars = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern upperCase = Pattern.compile("[A-Z ]");
        Pattern digitCase = Pattern.compile("[0-9 ]");
        if(!newPwd.equals(confirmation))
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Passwords do not match").build();
        if(newPwd.length() < 7)
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password length must be at least 7 characters").build();
        if(!specialChars.matcher(newPwd).find())
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password must have at least 1 special character").build();
        if(!upperCase.matcher(newPwd).find())
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password must have at least 1 upper case character").build();
        if(!digitCase.matcher(newPwd).find())
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("Password must have at least 1 digit character").build();
        return Response.ok().entity("new Password created with success").build();
    }


}
