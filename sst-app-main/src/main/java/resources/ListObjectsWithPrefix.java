package resources;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.util.ArrayList;
import java.util.List;

public class ListObjectsWithPrefix {
    public static List listObjectsWithPrefix(String directoryPrefix) {
        // The ID of your GCP project
         String projectId = "portalnova";

        // The ID of your GCS bucket
         String bucketName = "portalnova.appspot.com";

        // The directory prefix to search for
        // String directoryPrefix = "myDirectory/"

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        /**
         * Using the Storage.BlobListOption.currentDirectory() option here causes the results to display
         * in a "directory-like" mode, showing what objects are in the directory you've specified, as
         * well as what other directories exist in that directory. For example, given these blobs:
         *
         * <p>a/1.txt a/b/2.txt a/b/3.txt
         *
         * <p>If you specify prefix = "a/" and don't use Storage.BlobListOption.currentDirectory(),
         * you'll get back:
         *
         * <p>a/1.txt a/b/2.txt a/b/3.txt
         *
         * <p>However, if you specify prefix = "a/" and do use
         * Storage.BlobListOption.currentDirectory(), you'll get back:
         *
         * <p>a/1.txt a/b/
         *
         * <p>Because a/1.txt is the only file in the a/ directory and a/b/ is a directory inside the
         * /a/ directory.
         */
        Page<Blob> blobs =
                storage.list(
                        bucketName,
                        Storage.BlobListOption.prefix("activities/"+directoryPrefix),
                        Storage.BlobListOption.currentDirectory());


        List blobNamesList = new ArrayList();
        for (Blob blob : blobs.iterateAll()) {
            blobNamesList.add(blob.getName());
        }

        return blobNamesList;
    }
}