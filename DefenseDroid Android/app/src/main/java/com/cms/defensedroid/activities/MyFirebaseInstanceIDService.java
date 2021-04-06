package com.cms.defensedroid.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "PUSH_Android";

    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            firebaseDownlaod(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    public void firebaseDownlaod(String title, String uniqueid){

        Log.i("DownloadingFile", "File is Being Download...");

        // Get a non-default Storage bucket
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://your-bucket-name-here");

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        String appName = new ArrayList<String>(Arrays.asList(uniqueid.split("@", 2))).get(0).replace("_", " ");

        storageRef.child("reports/" + uniqueid +".pdf").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(title);
                request.setDescription("Click to Open...");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath() + "/DefenseDroid/" + uniqueid +".pdf"));

                downloadmanager.enqueue(request);
                Log.i("SuccessDownload", "Download Successful: " + uri);
                Toast.makeText(getApplicationContext(), "Generating Report for " + appName, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("FailedDownload", "Download Unsuccessful");
                Toast.makeText(getApplicationContext(), "File Downloaded Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
