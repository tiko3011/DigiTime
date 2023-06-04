package com.example.smartnotifyer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartnotifyer.alarm.AlarmHelper;
import com.example.smartnotifyer.alarm.AlarmReceiver;
import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.databinding.ActivityMainBinding;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.mvvm.StatsViewModel;
import com.example.smartnotifyer.ui.permission.UsagePermission;
import com.example.smartnotifyer.ui.permission.PermissionFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AlarmHelper alarmHelper;
    private ActivityMainBinding binding;
    AppsViewModel appsViewModel;
    StatsViewModel statsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashAnimation();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermission();

        appsViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(getApplicationContext());

        AsyncTask.execute(() -> {
            List<App> apps = appsViewModel.getAllApps();

            AlarmReceiver.selectedApps.clear();
            AlarmReceiver.selectedApps.addAll(apps);
        });
    }
    @Override
    public void onBackPressed() {
        // Disallowing Back Pressing
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }
    public void checkPermission(){
        PermissionFragment.isUsageGranted = UsagePermission.isUsageAccessGranted(getApplicationContext());
    }
    private void splashAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        0f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(500L);

                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                    }
                });
                slideUp.start();
            });
        }
    }
}