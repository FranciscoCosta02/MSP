package servlets;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.JWTValidation;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public class MediaResourceActivitiesPhotosServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MediaResourceActivitiesPhotosServlet.class.getName());
    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final KeyFactory myActivitiesKeyFactory = datastore.newKeyFactory().setKind("MyActivities");

    /**
     * Retrieves a file from GCS and returns it in the http response.
     * If the request path is /gcs/Foo/Bar this will be interpreted as
     * a request to read the GCS file named Bar in the bucket Foo.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("doGET!! activities");
        String id = req.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
            assert jwt != null;
        }
        catch (NoSuchElementException e){
            throw new AccessDeniedException("you dont have permission to continue (1)");
        }
        // Download file from a specified bucket. The request must have the form /gcs/<bucket>/<object>
        Storage storage = StorageOptions.getDefaultInstance().getService();
        // Parse the request URL
        Path objectPath = Paths.get(req.getPathInfo());
        if ( objectPath.getNameCount() != 2 ) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        // Get the bucket and the object names
        String bucketName = objectPath.getName(0).toString();
        String srcFilename = objectPath.getName(1).toString();

        Blob blob = storage.get(BlobId.of(bucketName, "activities/"+srcFilename));

        // Download object to the output stream. See Google's documentation.
        resp.setContentType(blob.getContentType());
        blob.downloadTo(resp.getOutputStream());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("doPOST!! activities");

        String id = req.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
            assert jwt != null;
        }
        catch (NoSuchElementException e){
            throw new AccessDeniedException("you dont have permission to continue (1)");
        }

        Claims values = jwt.getBody();
        String username = (String) values.get("username");

        // Upload file to specified bucket. The request must have the form /gcs/<bucket>/<object>
        Path objectPath = Paths.get(req.getPathInfo());
        //req.getQueryString()
        if ( objectPath.getNameCount() != 3 ) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        // Get the bucket and object from the URL
        String bucketName = objectPath.getName(0).toString();
        String srcFilename = objectPath.getName(1).toString();
        String activityID = objectPath.getName(2).toString();
        Key activityKey = myActivitiesKeyFactory.newKey(username+"_"+activityID);
        Transaction txn = datastore.newTransaction();
        try{
            Entity myActivity = txn.get(activityKey);
            LOG.warning("doPost act 2");
            if(myActivity == null) {
                txn.commit();
                LOG.warning("doPost act 3");
                if(!activityID.split("_")[0].equals(username)){
                    txn.rollback();
                    // activity created by this user at this time already exists!
                    throw new AccessDeniedException("Activity does not exist or you didnt subscribe the activity!!");
                }
            }
            else{
                txn.commit();
                LOG.warning("doPost act 4");
                Timestamp startDate = Timestamp.parseTimestamp(myActivity.getString("startDate"));
                if(Timestamp.now().compareTo(startDate)<0){
                    throw new AccessDeniedException("activity has not started yet! wait until the activity starts ");
                }
            }

            // Upload to Google Cloud Storage (see Google's documentation)
            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of(bucketName, "activities/"+activityID+"_"+username+"_"+srcFilename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setAcl(Collections.singletonList(Acl.newBuilder(Acl.User.ofAllUsers(),Acl.Role.READER).build()))
                    .setContentType(req.getContentType())
                    .build();
            // The following is deprecated since it is better to upload directly to GCS from the client
            Blob blob = storage.create(blobInfo, req.getInputStream());



        }catch(Exception e){
            txn.rollback();
            // activity created by this user at this time already exists!
            throw new AccessDeniedException("You are not able to post a photo!");
        }


    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("doDELETE!! activities");

        String id = req.getHeader("Authorization");
        id = id.substring("Bearer".length()).trim();
        Jws<Claims> jwt;
        try {
            jwt = JWTValidation.parseJwt(id);
            assert jwt != null;
        }
        catch (NoSuchElementException e){
            throw new AccessDeniedException("you dont have permission to continue (1)");
        }

        Claims values = jwt.getBody();
        String role = (String) values.get("role");
        String username = (String) values.get("username");


        /*Claims values = jwt.getBody();
        String role = (String) values.get("role");
        String username = (String) values.get("username");*/

        // Delete a file from a specified bucket. The request must have the form /gcs/<bucket>/<object>
        Storage storage = StorageOptions.getDefaultInstance().getService();
        // Parse the request URL
        Path objectPath = Paths.get(req.getPathInfo());
        if (objectPath.getNameCount() != 2) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        // Get the bucket and the object names
        String bucketName = objectPath.getName(0).toString();
        String fileName = objectPath.getName(1).toString();

        if(!role.equals("SU") && !role.equals("ADMIN") && !role.equals("STAFF") && !username.equals(fileName.split("_")[0])){
            throw new AccessDeniedException("you dont have permission to delete the photo...");
        }

        // Delete the file
        BlobId blobId = BlobId.of(bucketName, "activities/"+fileName);
        boolean deleted = storage.delete(blobId);

        // Return success or failure
        resp.setContentType("text/plain");
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("File deleted successfully.");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Failed to delete file.");
        }
    }


}