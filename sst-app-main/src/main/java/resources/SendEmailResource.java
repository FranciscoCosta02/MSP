package resources;

import java.util.Properties;
import java.util.Date;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailResource {

    private static final Logger LOG = Logger.getLogger(SendEmailResource.class.getName());


    public static void sendEmail(String toEmail, String type){
        LOG.warning("sendEmail email:"+toEmail+" | type:"+type);
        // Sender's email ID needs to be mentioned
        String from = "nova.sst07@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";


        // Setup mail server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(from, "luvnwmwfdmvuqaeq");

            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            // Set From: header field of the header.
            msg.setFrom(new InternetAddress(from, "NOVA-SST"));

            // Set To: header field of the header.
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Set Subject: header field
            msg.setSubject("VALIDATE YOUR ACCOUNT", "UTF-8");

            // Now set the actual message
            msg.setText("Press the link to validate your account", "UTF-8");

            if(type.contains("recover")){
                String[] values = type.split(",");
                msg.setSubject("PASSWORD RECOVERY (PORTAL NOVA)", "UTF-8");
                msg.setContent(
                        "<h1>NOVA STT APP</h1><br/>Use this code to recover your password:" +
                                values[1], "text/html");
            } else if(type.contains("reservation")) {
                String[] values = type.split(",");
                msg.setSubject("RESERVATION WAS OVERRULED (PORTAL NOVA)");
                msg.setContent("Your reservation " + values[1] +  " was cancelled due to that room being unavailable! ",
                        "text/html" );
            }

            msg.setSentDate(new Date());

            System.out.println("sending...");
            // Send message
            Transport.send(msg);
            System.out.println("Sent message successfully....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActivationEmail(String toEmail, String token,String username){

        // Sender's email ID needs to be mentioned
        String from = "nova.sst07@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";


        // Setup mail server
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(from, "luvnwmwfdmvuqaeq");

            }
        });


        try {
            // Create a default MimeMessage object.
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            // Set From: header field of the header.
            msg.setFrom(new InternetAddress(from, "NOVA-SST"));

            // Set To: header field of the header.
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Set Subject: header field
            msg.setSubject("VALIDATE YOUR ACCOUNT", "UTF-8");

            // Now set the actual message
            msg.setText("Press the link to validate your account", "UTF-8");

            msg.setSubject("VALIDATE YOUR ACCOUNT (PORTAL NOVA)", "UTF-8");
            msg.setContent(
                    "<h1>PORTAL NOVA</h1><br/><h3>Hi! "+username+", welcome to our community</h3> " +
                            "<br/> Press <a href=\"https://portalnova.oa.r.appspot.com/rest/activation?tk="+token+"\">here</a> to activate your account" +
                            "<p>If you are having problems to activate your account please follow:</p>"+
                            "<p> 1 - Try to open the email on your browser</p>"+
                            "<p> 2 - contact us by email (nova.sst07@gmail.com) or go to the school secretary</p>"
                            , "text/html");


            msg.setSentDate(new Date());
            System.out.println("sending...");
            // Send message
            Transport.send(msg);
            System.out.println("Sent message successfully....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
