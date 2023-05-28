package com.example.smartnotifyer.ui.limits;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartnotifyer.MainActivity;
import com.example.smartnotifyer.R;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.apps.AppsFragment;
import com.example.smartnotifyer.ui.stats.StatsFragment;

public class LimitFragment extends Fragment {
    static int weeklyUsage = AppsFragment.weeklyUsage;
    public static long usageLimit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_limit, container, false);

        TextView tvUsage = root.findViewById(R.id.tv_usage_time);     tvUsage.setText(UsageConverter.convertMinuteToString(AppsFragment.weeklyUsage));
        TextView tvTarget = root.findViewById(R.id.tv_limit);
        TextView tvComment = root.findViewById(R.id.tv_info_reduction);

        Button btnNext = root.findViewById(R.id.btn_confirm);
        btnNext.setOnClickListener(v -> {
            StatsFragment fragment = new StatsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_apps_list, fragment); // R.id.container is the ID of the container in your activity's layout
            transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
            transaction.commit();
        });
        Button btnBack = root.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            AppsFragment fragment = new AppsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_apps_list, fragment); // R.id.container is the ID of the container in your activity's layout
            transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
            transaction.commit();
        });

        SeekBar barSetLimit = root.findViewById(R.id.bar_set_limit);
        barSetLimit.setMax(weeklyUsage * 2);
        barSetLimit.setProgress(weeklyUsage);

        barSetLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTarget.setText(UsageConverter.convertMinuteToString(barSetLimit.getMax() - barSetLimit.getProgress()));

                if (progress < weeklyUsage) {
                    tvComment.setText("That's more than you normally use!");
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

        return root;
    }
}