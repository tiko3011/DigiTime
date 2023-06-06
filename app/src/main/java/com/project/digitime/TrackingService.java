package com.project.digitime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackingService extends Service {
    @NotNull
    public static String CHANNEL_ID = "Stopwatch_Notifications_Java";
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
                this.startStopwatch();
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
    }

    private void moveToBackground() {
        this.stopForeground(true);
    }

    private void startStopwatch() {
        Intent stopwatchIntent = new Intent();
        stopwatchIntent.setAction(START);
    }

    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Tracking", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(this, NotificationManager.class);
    }

    private Notification buildNotification() {
        String title = "Tracking usage";

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setOngoing(true)
                .setContentText("You using your phone too much!!")
                .setColorized(true)
                .setColor(Color.parseColor("#BEAEE2"))
                .setSmallIcon(R.drawable.ic_menu_recent_history)
                .setOnlyAlertOnce(true)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
    }
}

