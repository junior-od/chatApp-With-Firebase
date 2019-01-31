package com.example.ooduberu.chatapp.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.activities.AccountSettingsActivity;
import com.example.ooduberu.chatapp.activities.HomeActivity;
import com.example.ooduberu.chatapp.activities.LoginActivity;
import com.example.ooduberu.chatapp.activities.ProfileActivity;
import com.example.ooduberu.chatapp.activities.SettingsActivity;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    Intent dataIntent;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //Crashlytics.setUserIdentifier(token);
       // sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload or notification payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            if (true) {
//                Bundle bundle = new Bundle();
//                Map<String, String> data = remoteMessage.getData();
//                for (String key : data.keySet()) {
//                    String value = data.get(key);
//                    Log.d(TAG, "Key: " + key + " Value: " + value);
//                    bundle.putString(key, value);
//                }
//
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob(bundle);
//            } else {
            receiveDataMessage(remoteMessage);
//            }
        }
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotificationMessage(remoteMessage);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }

//    private void scheduleJob(Bundle bundle) {
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(JobServices.class)
//                .setTag("my-job-tag")
//                .setExtras(bundle)
//                .build();
//        //dispatcher.schedule(myJob);
//        dispatcher.mustSchedule(myJob);
//
//        //   Bundle myExtrasBundle = new Bundle();
//        //   myExtrasBundle.putString("some_key", "some_value");
//
////        Job myJob = dispatcher.newJobBuilder()
////                // the JobService that will be called
////                .setService(MyJobService.class)
////                // uniquely identifies the job
////                .setTag("my-unique-tag")
////                // one-off job
////                .setRecurring(false)
////                // don't persist past a device reboot
////                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
////                // start between 0 and 60 seconds from now
////                .setTrigger(Trigger.executionWindow(0, 60))
////                // don't overwrite an existing job with the same tag
////                .setReplaceCurrent(false)
////                // retry with exponential backoff
////                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
////                // constraints that need to be satisfied for the job to run
////                .setConstraints(
////                        // only run on an unmetered network
////                        Constraint.ON_UNMETERED_NETWORK,
////                        // only run when the device is charging
////                        Constraint.DEVICE_CHARGING
////                )
////                .setExtras(myExtrasBundle)
////                .build();
////
////        dispatcher.mustSchedule(myJob);
//
//        // dispatcher.cancel("my-unique-tag");
//        // dispatcher.cancelAll();
//    }

    //normal notification
    private void sendNotificationMessage(RemoteMessage remoteMessage) {
        int notificationId = new Random().nextInt(60000);
        String channelId = "channel id";
        CharSequence adminChannelName = "channel name";
        String adminChannelDescription = "channel desc";

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(adminChannelDescription);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }


    //data type notification
    private void receiveDataMessage(RemoteMessage remoteMessage) {
        // Map<String, String> data = remoteMessage.getData();
        //for(String key : data.keySet()){
        //    String value = data.get(key);
        //    Log.d(TAG, "Key: " + key + " Value: " + value);
        // }
        int notificationId = new Random().nextInt(60000);
        String channelId = "channel id";
        CharSequence adminChannelName = "channel name";
        String adminChannelDescription = "channel desc";

//       if(remoteMessage.getData().get("user_id").equals(AppPreference.getCurrentUserId())){
            if(remoteMessage.getData().get("type").equalsIgnoreCase("follower request")){
                dataIntent = new Intent(this, HomeActivity.class);
                dataIntent.putExtra("position",2);
                dataIntent.putExtra("userId",remoteMessage.getData().get("user_id"));

            }
            else{
                dataIntent = new Intent(this, ProfileActivity.class);
            }
      //  }
//        else{
//            dataIntent = new Intent(this, LoginActivity.class);
//           // dataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }


        //dataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, dataIntent, PendingIntent.FLAG_ONE_SHOT);

//      Intent save = new Intent(this, SaveService.class);
//      save.putExtra(SaveService.NOTIFICATION_ID_EXTRA, notificationId);
//      save.putExtra(SaveService.IMAGE_URL_EXTRA, remoteMessage.getData().get("imageurl"));
//      PendingIntent savePendingIntent = PendingIntent.getService(this, notificationId+1, save, PendingIntent.FLAG_ONE_SHOT);

        Bitmap bitmap = getBitmapFromUrl(remoteMessage.getData().get("icon"));//obtain the image
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(remoteMessage.getData().get("icon"))) {
            notificationBuilder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .setBigContentTitle(remoteMessage.getData().get("title"))
                            .setSummaryText(remoteMessage.getData().get("body"))
                            .bigPicture(bitmap)
            );
            // .addAction(R.mipmap.ic_launcher, getString(R.string.notification_save_button), savePendingIntent)
        } else {
            notificationBuilder
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"));
            //.setLargeIcon(bitmap)
            //.setSmallIcon(R.mipmap.ic_launcher)
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(adminChannelDescription);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

//        NotificationDAO notificationDAO = AppDatabase.getInstance(this).getNotificationDAO();
//        Notification notification = new Notification(NotificationType.TYPE_BACKEND);
//        notification.setDate(new Date());
//        notification.setMessage(remoteMessage.getData().get("body"));
//        notification.setTitle(remoteMessage.getData().get("title"));
//        notification.setUnRead(true);
//        notificationDAO.insert(notification);
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
