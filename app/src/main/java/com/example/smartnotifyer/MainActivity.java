package com.example.smartnotifyer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartnotifyer.alarm.AlarmHelper;
import com.example.smartnotifyer.databinding.ActivityMainBinding;
import com.example.smartnotifyer.ui.UsageAccessPermissionChecker;
import com.example.smartnotifyer.ui.permission.PermissionFragment;

public class MainActivity extends AppCompatActivity {
    private AlarmHelper alarmHelper;;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        splashAnimation();
        checkPermission();

        alarmHelper = new AlarmHelper();
        alarmHelper.setAlarmInNextMinute(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    public void checkPermission(){
        PermissionFragment.isUsageGranted = UsageAccessPermissionChecker.isUsageAccessGranted(getApplicationContext());
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