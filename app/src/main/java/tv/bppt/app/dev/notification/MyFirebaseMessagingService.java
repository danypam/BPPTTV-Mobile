package tv.bppt.app.dev.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import tv.bppt.app.dev.R;
import tv.bppt.app.dev.activity.CustomUrlActivity;
import tv.bppt.app.dev.activity.NotificationDetailsActivity;
import tv.bppt.app.dev.data.constant.AppConstant;
import tv.bppt.app.dev.data.preference.AppPreference;
import tv.bppt.app.dev.data.sqlite.NotificationDbController;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();

            if (AppPreference.getInstance(MyFirebaseMessagingService.this).isNotificationOn()) {

                sendNotification(params.get("title"), params.get("message"), params.get("url"));
                broadcastNewNotification();
            }
        }
    }

    private void sendNotification(String title, String messageBody, String url) {

        // insert data into database
        NotificationDbController notificationDbController = new NotificationDbController(MyFirebaseMessagingService.this);
        notificationDbController.insertData(title, messageBody, url);


        Intent intent;
        if (url != null || url.trim().length() != 0) {
            intent = new Intent(this, CustomUrlActivity.class);
            intent.putExtra(AppConstant.BUNDLE_KEY_TITLE, title);
            intent.putExtra(AppConstant.BUNDLE_KEY_URL, url);
            intent.putExtra(AppConstant.BUNDLE_FROM_PUSH, true);
        } else {
            intent = new Intent(this, NotificationDetailsActivity.class);
            intent.putExtra(AppConstant.BUNDLE_KEY_TITLE, title);
            intent.putExtra(AppConstant.BUNDLE_KEY_MESSAGE, messageBody);
            intent.putExtra(AppConstant.BUNDLE_KEY_URL, url);
            intent.putExtra(AppConstant.BUNDLE_FROM_PUSH, true);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        // create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500});
            notificationChannel.setLockscreenVisibility(
                    Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // send notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void broadcastNewNotification() {
        Intent intent = new Intent(AppConstant.NEW_NOTI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
