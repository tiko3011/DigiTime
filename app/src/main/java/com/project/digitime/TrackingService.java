package com.project.digitime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.project.digitime.ui.stats.UsageConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackingService extends Service {
    @NotNull
    public static String FOREGROUND_CHANNEL_ID = "DIGITIME_FOREGROUND";
    public static String LIMIT_CHANNEL_ID = "DIGITIME_LIMIT";

    @NotNull
    public static String START = "START";
    @NotNull
    public static String MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND";
    @NotNull
    public static String MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND";
    @NotNull
    public static String SERVICE_STATUS = "STOPWATCH_STATUS";
    @NotNull
    public static final String STOPWATCH_ACTION = "STOPWATCH_ACTION";


    public static boolean isUsageLimitReached = false;
    String contentText = "";
    String title = "Tracking usage";

    int testInt = 0;

    private Handler handler;
    private Runnable runnable;
    private NotificationManager notificationManager;
    @org.jetbrains.annotations.Nullable
    public IBinder onBind(@Nullable Intent intent) {
        return null;
    }

    public int onStartCommand(@NotNull Intent intent, int flags, int startId) {
        this.createChannel();
        this.getNotificationManager();

        String action = intent.getStringExtra(STOPWATCH_ACTION);
        switch (action) {
            case "START":
                this.startTracking();
                break;
            case "MOVE_TO_BACKGROUND":
                this.moveToBackground();
                break;
            case "MOVE_TO_FOREGROUND":
                this.moveToForeground();
        }

        return START_STICKY;
    }

    private void moveToForeground() {
        this.startForeground(1, this.buildNotification());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                boolean isRunning = MainActivity.usage < MainActivity.usageLimit;

                updateNotification();
                if (isRunning) {
                    isUsageLimitReached = false;
                    handler.postDelayed(this, 100);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        sentNotification();
                    }
                    isUsageLimitReached = true;
                    handler.removeCallbacks(runnable);
                }

            }
        }; handler.post(runnable);
    }

    private void moveToBackground() {
        this.stopForeground(true);
    }

    private void startTracking() {
        Intent stopwatchIntent = new Intent();
        stopwatchIntent.setAction(START);
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, "Tracking", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setShowBadge(false);

        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(this, NotificationManager.class);
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setContentTitle(title)
                .setOngoing(true)
                .setContentText(String.valueOf(contentText))
                .setColorized(true)
                .setColor(Color.parseColor("#BEAEE2"))
                .setSmallIcon(R.drawable.ic_menu_recent_history)
                .setOnlyAlertOnce(true)
                .setProgress((int) MainActivity.usageLimit, (int) MainActivity.usage, false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    private void updateNotification() {
        testInt++;
        double usagePercent = (double) MainActivity.usageMilli / (double) MainActivity.usageLimitMilli * 100;

        if (usagePercent <= 100){
            title = "Tracking usage";
            contentText = "Used " + UsageConverter.decimalFormat.format(usagePercent) + "% of daily usage limit" ;
        } else {
            title = "Usage limit Reached !";
            contentText = "You reached your daily usage limit" ;
        }


        notificationManager.notify(1, buildNotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void sentNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, LIMIT_CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Usage")
                .setContentText("You reached today's usage limit")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("data", "Some data");

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel =
                notificationManager.getNotificationChannel(LIMIT_CHANNEL_ID);

        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            notificationChannel = new NotificationChannel(LIMIT_CHANNEL_ID, "Description", importance);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(1016, builder.build());
    }
}

