package com.project.digitime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.project.digitime.database.App;
import com.project.digitime.database.Stat;
import com.project.digitime.databinding.ActivityMainBinding;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.mvvm.StatsViewModel;
import com.project.digitime.ui.limits.LimitFragment;
import com.project.digitime.ui.permission.UsagePermission;
import com.project.digitime.ui.permission.PermissionFragment;
import com.project.digitime.ui.stats.UsageConverter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    AppsViewModel appsViewModel;
    StatsViewModel statsViewModel;

    public static List<App> selectedApps = new ArrayList<>();
    public static List<Stat> stats = new ArrayList<>();
    public static List<Stat> selectedStats = new ArrayList<>();

    public static long startTime;
    public static long endTime;
    public static long usageLimit = 0;
    public static long usage = 0;

    public static long usageLimitMilli = 0;
    public static long usageMilli = 0;

    boolean isLimitReached = false;
    boolean isButtonClicked = false;

    SharedPreferences sharedPreferences;
    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashAnimation();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermission();
        startTracking();

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        appsViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        statsViewModel.deleteAllStats();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateUI();

                handler.postDelayed(this, 5 * 1000);
            }
        }; handler.post(runnable);
    }


    @Override
    protected void onStart() {
        super.onStart();
        this.moveToBackground();
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.moveToForeground();
    }
    @Override
    public void onBackPressed() {
        // Disallowing Back Pressing
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();

        IntentFilter statusFilter = new IntentFilter();
        statusFilter.addAction(TrackingService.SERVICE_STATUS);
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

    private void updateUI() {
        usageLimit = sharedPreferences.getLong("usageLimit", 300);
        usageLimitMilli = usageLimit * 60 * 1000;

        isButtonClicked = UsagePermission.isUsageAccessGranted(this);

        AsyncTask.execute(() -> {
            stats.clear();
//            stats.addAll(statsViewModel.getAllStats());

            endTime = System.currentTimeMillis(); long interval = (long) LocalTime.now().getHour() * 60 * 60 * 1000 + (long) LocalTime.now().getMinute() * 60 * 1000;
            startTime = endTime - interval;
            getStatsDailyFromSystem(startTime, endTime, isButtonClicked);

            List<App> selectedApps = new ArrayList<>();
            selectedApps.addAll(appsViewModel.getAllApps());

            int usageSelected = 0;
            for (int i = 0; i < selectedApps.size(); i++) {
                for (int j = 0; j < stats.size(); j++) {
                    if (selectedApps.get(i).appName.equals(stats.get(j).statName)){
                        usageSelected += stats.get(j).statTime;
                    }
                }
            }

            MainActivity.usage = usageSelected / 60000;
            usageMilli = usageSelected;
        });

        Log.i("Limits Of MAIN ACTIVITY", "UsageLimit: --> " + usageLimit);
        Log.i("Limits Of MAIN ACTIVITY", "Usage: --> " + usage);
    }
    public void getStatsDailyFromSystem(long startTime, long endTime, boolean isNextClicked) {
        if (isNextClicked) {

            UsageStatsManager usageStatsManager = (UsageStatsManager) getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

            stats.clear();
            for (int i = 0; i < usageStatsList.size(); i++) {
                UsageStats usageStats = usageStatsList.get(i);

                if (usageStats.getTotalTimeInForeground() / 60000 > 0 && usageStats.getLastTimeUsed() >= startTime) {
                    stats.add(new Stat(usageStats.getPackageName(), usageStats.getTotalTimeInForeground()));
                }
            }
            for (int i = 0; i < stats.size(); i++) {
                Log.i("STATS JUST ADDED", stats.get(i).toString());
            }
        }
        Log.i("STATS JUST ADDED","");
        Log.i("STATS JUST ADDED","");
        Log.i("STATS JUST ADDED","");
        Log.i("STATS JUST ADDED","");
    }
    private void startTracking() {
        Intent stopwatchService = new Intent(this, TrackingService.class);
        stopwatchService.putExtra(TrackingService.STOPWATCH_ACTION, TrackingService.START);
        this.startService(stopwatchService);
    }
    private void moveToForeground() {
        Intent stopwatchService = new Intent(this, TrackingService.class);
        stopwatchService.putExtra(TrackingService.STOPWATCH_ACTION, TrackingService.MOVE_TO_FOREGROUND);
        this.startService(stopwatchService);
    }
    private void moveToBackground() {
        Intent stopwatchService = new Intent(this, TrackingService.class);
        stopwatchService.putExtra(TrackingService.STOPWATCH_ACTION, TrackingService.MOVE_TO_BACKGROUND);
        this.startService(stopwatchService);
    }
}