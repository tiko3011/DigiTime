package com.project.digitime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.project.digitime.alarm.AlarmHelper;
import com.project.digitime.alarm.AlarmReceiver;
import com.project.digitime.database.App;
import com.project.digitime.database.Stat;
import com.project.digitime.databinding.ActivityMainBinding;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.mvvm.StatsViewModel;
import com.project.digitime.ui.permission.UsagePermission;
import com.project.digitime.ui.permission.PermissionFragment;
import com.project.digitime.ui.stats.UsageConverter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AlarmHelper alarmHelper;
    private ActivityMainBinding binding;
    AppsViewModel appsViewModel;
    StatsViewModel statsViewModel;

    public static List<App> selectedApps = new ArrayList<>();
    public static List<Stat> stats = new ArrayList<>();
    public static List<Stat> selectedStats = new ArrayList<>();

    private long usageLimit;
    public static long usage;
    boolean isLimitReached = false;

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashAnimation();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermission();

        appsViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        AsyncTask.execute(() -> {
            List<App> apps = appsViewModel.getAllApps();

            AlarmReceiver.selectedApps.clear();
            AlarmReceiver.selectedApps.addAll(apps);
        });

        ////////////////////////////////////
        ////////////////////////////////////
        ////////////////////////////////////


//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        usageLimit = sharedPreferences.getLong("usageLimit", 0);
//
//        statsViewModel.getStats().observe(this, stats -> {
//             MainActivity.stats.addAll(stats);
//        });
//
//        UsageConverter.deleteDuplicates(stats);
//        getSelectedStats();
//
//        if (stats.size() != 0) {
//            usage = 0;
//            for (int i = 0; i < stats.size(); i++) {
//                usage += stats.get(i).statTime / 60000;
//            }
//
//            isLimitReached = usage >= usageLimit;
//        }
//        Log.i("Limits", "UsageLimit: --> " + usageLimit);
//        Log.i("Limits", "Usage: --> " + usage);

        ////////////////////////////////////
        ////////////////////////////////////
        ////////////////////////////////////

        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(getApplicationContext());
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

    public void getSelectedStats(){
        selectedStats.clear();
        for (int i = 0; i < selectedApps.size(); i++) {
            for (int j = 0; j < stats.size(); j++) {
                if (selectedApps.get(i).appName.equals(stats.get(j).statName)){
                    selectedStats.add(stats.get(j));
                }
            }
        }
    }
}