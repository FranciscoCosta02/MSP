package servlets;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import utils.JWTValidation;


@SuppressWarnings("serial")
public class MediaResourceUserPhotosServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MediaResourceUserPhotosServlet.class.getName());


    /**
     * Retrieves a file from GCS and returns it in the http response.
     * If the request path is /gcs/Foo/Bar this will be interpreted as
     * a request to read the GCS file named Bar in the bucket Foo.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("doGET!!");
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

        Blob blob = storage.get(BlobId.of(bucketName, "users/"+srcFilename));

        // Download object to the output stream. See Google's documentation.
        resp.setContentType(blob.getContentType());
        blob.downloadTo(resp.getOutputStream());
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        LOG.info("doPOST!!");
        // Upload file to specified bucket. The request must have the form /gcs/<bucket>/<object>
        Path objectPath = Paths.get(req.getPathInfo());
        if ( objectPath.getNameCount() != 3 ) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }
        // Get the bucket and object from the URL
        String bucketName = objectPath.getName(0).toString();
        String srcFilename = objectPath.getName(1).toString();
        String oldName = objectPath.getName(2).toString();

        // Upload to Google Cloud Storage (see Google's documentation)
        Storage storage = StorageOptions.getDefaultInstance().getService();
        //delete old photo

        BlobId blobIdoldName = BlobId.of(bucketName, "users/"+oldName);
        boolean deleted = storage.delete(blobIdoldName);
        LOG.warning("old photo deleted? "+deleted);

        BlobId blobId = BlobId.of(bucketName, "users/"+srcFilename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setAcl(Collections.singletonList(Acl.newBuilder(Acl.User.ofAllUsers(),Acl.Role.READER).build()))
                .setContentType(req.getContentType())
                .build();
        // The following is deprecated since it is better to upload directly to GCS from the client
        Blob blob = storage.create(blobInfo, req.getInputStream());
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOG.info("doDELETE!!");

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

        // Delete the file
        BlobId blobId = BlobId.of(bucketName, "users/"+fileName);
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