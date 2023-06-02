package com.example.smartnotifyer.ui.limits;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartnotifyer.R;
import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.apps.AppsFragment;
import com.example.smartnotifyer.ui.stats.StatsFragment;

import java.util.List;

public class LimitFragment extends Fragment {
    static int weeklyUsage = AppsFragment.weeklyUsage;
    public static long usageLimit;
    AppsViewModel appsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_limit, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);

        TextView tvUsage = root.findViewById(R.id.tv_usage_time);     tvUsage.setText(UsageConverter.convertMinuteToString(AppsFragment.weeklyUsage));
        TextView tvTarget = root.findViewById(R.id.tv_limit); tvTarget.setText(tvUsage.getText());
        TextView tvComment = root.findViewById(R.id.tv_info_reduction);

        SeekBar barSetLimit = root.findViewById(R.id.bar_set_limit);
        barSetLimit.setMax(weeklyUsage * 2);
        barSetLimit.setProgress(weeklyUsage);
        barSetLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTarget.setText(UsageConverter.convertMinuteToString(barSetLimit.getMax() - barSetLimit.getProgress()));

                if (progress < weeklyUsage) {
                    tvComment.setText("That's more than you normally use!");
                } else if (progress == weeklyUsage) {
                    tvComment.setText("That's how much you use");
                } else {
                    double reductionPercent = (double) (barSetLimit.getProgress() - barSetLimit.getMax() / 2) / weeklyUsage * 100;
                    tvComment.setText("That's a reduction of " + UsageConverter.decimalFormat.format(reductionPercent) + "% !");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Called when the user starts interacting with the SeekBar
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Called when the user stops interacting with the SeekBar
                usageLimit = barSetLimit.getMax() - barSetLimit.getProgress();
            }
        });

        Button btnNext = root.findViewById(R.id.btn_confirm);
        btnNext.setOnClickListener(v -> {
            StatsFragment fragment = new StatsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_permission, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        Button btnBack = root.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            appsViewModel.deleteAllApps();
            editor.putBoolean("isNextClicked", false);
            editor.apply();

            AppsFragment fragment = new AppsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_permission, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return root;
    }
}