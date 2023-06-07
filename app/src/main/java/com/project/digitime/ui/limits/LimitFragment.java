package com.project.digitime.ui.limits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.digitime.MainActivity;
import com.project.digitime.R;
import com.project.digitime.TrackingService;
import com.project.digitime.adapter.SelectedAppAdapter;
import com.project.digitime.database.App;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.ui.stats.SelectedApp;
import com.project.digitime.ui.stats.UsageConverter;
import com.project.digitime.ui.apps.AppsFragment;
import com.project.digitime.ui.stats.StatsFragment;

import java.util.ArrayList;
import java.util.List;

public class LimitFragment extends Fragment {
    static int weeklyUsage;
    public static long usageLimit;
    public static boolean isLimitCreated = false;
    AppsViewModel appsViewModel;

    private List<SelectedApp> selectedApps = new ArrayList<>();
    private SelectedAppAdapter statAdapterSelected;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_limit, container, false);

        isLimitCreated = true;
        TrackingService.isNotificationSent = false;

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        weeklyUsage = sharedPreferences.getInt("averageWeeklyUsage", AppsFragment.weeklyUsage);
        usageLimit = sharedPreferences.getLong("usageLimit", 180);

        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);

        TextView tvUsage = root.findViewById(R.id.tv_usage_time);     tvUsage.setText(UsageConverter.convertMinuteToString(weeklyUsage));
        TextView tvTarget = root.findViewById(R.id.tv_limit); tvTarget.setText(tvUsage.getText());
        TextView tvComment = root.findViewById(R.id.tv_info_reduction);

        SeekBar barSetLimit = root.findViewById(R.id.bar_set_limit);
        barSetLimit.setProgress(weeklyUsage);

        RecyclerView recyclerViewSelected = root.findViewById(R.id.selected_stat_list);
        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        statAdapterSelected = new SelectedAppAdapter(requireContext());
        recyclerViewSelected.setAdapter(statAdapterSelected);

        AsyncTask.execute(() -> {
            List<App> apps = appsViewModel.getAllApps();
            Log.i("APP_SIZE_TAG", "Apps: --> " + apps.size());

            for (int i = 0; i < apps.size(); i++) {
                selectedApps.add(new SelectedApp(apps.get(i).appName));
            }
            Log.i("APP_SELECTED_TAG", "Selected: --> " + selectedApps.size());
            statAdapterSelected.setSelectedApps(selectedApps);
        });

        barSetLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTarget.setText(UsageConverter.convertMinuteToString(barSetLimit.getProgress()));

                if (progress > weeklyUsage) {
                    tvComment.setText("That's more than you use!");
                } else if (progress == weeklyUsage) {
                    tvComment.setText("That's how much you use");
                } else {
                    double reductionPercent = 100 - (double) (barSetLimit.getProgress()) / weeklyUsage * 100;
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
                usageLimit = barSetLimit.getProgress();
            }
        });

        Button btnNext = root.findViewById(R.id.btn_confirm);
        btnNext.setOnClickListener(v -> {
            usageLimit = barSetLimit.getProgress();
            editor.putLong("usageLimit", usageLimit);
            editor.apply();

            StatsFragment fragment = new StatsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        Button btnBack = root.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            AppsFragment fragment = new AppsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return root;
    }
}