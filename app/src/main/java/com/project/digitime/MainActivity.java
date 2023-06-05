package com.project.digitime;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.project.digitime.alarm.AlarmHelper;
import com.project.digitime.alarm.AlarmReceiver;
import com.project.digitime.database.App;
import com.project.digitime.databinding.ActivityMainBinding;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.mvvm.StatsViewModel;
import com.project.digitime.ui.permission.UsagePermission;
import com.project.digitime.ui.permission.PermissionFragment;

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