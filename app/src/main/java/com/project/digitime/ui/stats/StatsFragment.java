package com.project.digitime.ui.stats;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitime.R;
import com.project.digitime.alarm.AlarmReceiver;
import com.project.digitime.database.App;
import com.project.digitime.database.Stat;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.mvvm.StatsViewModel;
import com.project.digitime.ui.apps.AppsFragment;
import com.project.digitime.ui.limits.LimitFragment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment{
    Toolbar toolbar;
    TextView tvUsageApps;
    TextView tvUsageLimit;
    TextView tvUsageSelectedApps;

    LocalTime localTime = LocalTime.now();

    private final long hour = (long) 60 * 60 * 1000 * localTime.getHour();
    private long end = System.currentTimeMillis();
    private long start = end - hour;

    private long usageSelected;
    private long usageLimit;
    private long usage;

    private List<SelectedApp> selectedApps = new ArrayList<>();

    private StatAdapter statAdapter;
    private SelectedAppAdapter statAdapterSelected;
    private StatsViewModel statsViewModel;
    private AppsViewModel appsViewModel;

    private Handler handler = new Handler();
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int DELAY_MILLISECONDS = 4000; // 4 seconds
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        SharedPreferences sharedPreferences = requireContext().getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        usageLimit = sharedPreferences.getLong("usageLimit", 0);

        toolbar = root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(toolbar);
        activity.setTitle("");

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.stat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        statAdapter = new StatAdapter();
        recyclerView.setAdapter(statAdapter);

        statsViewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            statAdapter.setStatsList(stats);

            AlarmReceiver.stats.addAll(stats);
        });


        RecyclerView recyclerViewSelected = root.findViewById(R.id.selected_stat_list);
        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        statAdapterSelected = new SelectedAppAdapter();
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

        statsViewModel.deleteAllStats();
        statsViewModel.addStatsFromSystemDaily(start, end);
        statsViewModel.deleteDuplicates();

        tvUsageSelectedApps = root.findViewById(R.id.tv_usage_selected_apps);
        tvUsageLimit = root.findViewById(R.id.tv_usage_limit);
        tvUsageApps = root.findViewById(R.id.tv_usage_apps);

        AsyncTask.execute(() -> {
            List<Stat> stats = new ArrayList<>();
            stats.addAll(statsViewModel.getAllStats());

            int usage = 0;
            for (int i = 0; i < stats.size(); i++) {
                usage += stats.get(i).statTime;
            }

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

            this.usage = usage;
            this.usageSelected = usageSelected;

            requireActivity().runOnUiThread(() -> {
                tvUsageApps.setText(UsageConverter.convertMilliToString(this.usage));
                tvUsageSelectedApps.setText(UsageConverter.convertMilliToString(this.usageSelected));
                tvUsageLimit.setText(UsageConverter.convertMinuteToString(usageLimit));
            });
        });

        checkNotificationPermission();
        return root;
    }

    private void checkNotificationPermission() {
        handler.postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
                }
            }
        }, DELAY_MILLISECONDS);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Apps){
            AppsFragment fragment = new AppsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.Limit) {
            LimitFragment fragment = new LimitFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatCardHolder> {
        List<Stat> statsList;

        public void setStatsList(List<Stat> statsList) {
            this.statsList = new ArrayList<>(statsList);
            notifyDataSetChanged();
        }
        public void setFilteredList(List<Stat> filteredList){
            this.statsList = filteredList;
            notifyDataSetChanged();
        }
        public StatCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return new StatCardHolder(inflater.inflate(R.layout.view_stat_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder (@NonNull StatCardHolder holder, int position) {
            String name = statsList.get(position).statName;
            long time = statsList.get(position).statTime;

            try {
                PackageManager packageManager = requireActivity().getApplication().getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(name, 0);
                Drawable icon = packageManager.getApplicationIcon(applicationInfo);

                holder.nameText.setText(packageManager.getApplicationLabel(applicationInfo).toString());
                holder.icon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }

            holder.timeText.setText(UsageConverter.convertMilliToString(time));
        }


        @Override
        public int getItemCount() {
            return statsList != null ? statsList.size() : 0;
        }

        public class StatCardHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView timeText;
            ImageView icon;

            public StatCardHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.item_stat_name_tv);
                timeText = view.findViewById(R.id.item_stat_time_tv);
                icon = view.findViewById(R.id.item_stat_icon_iv);
            }
        }
    }

    private class SelectedAppAdapter extends RecyclerView.Adapter<SelectedAppAdapter.SelectedAppCardHolder> {
        List<SelectedApp> selectedApps;

        public void setSelectedApps(List<SelectedApp> selectedApps) {
            this.selectedApps = new ArrayList<>(selectedApps);
            notifyDataSetChanged();
        }
        public void setFilteredList(List<SelectedApp> filteredList){
            this.selectedApps = filteredList;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public SelectedAppCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return new SelectedAppCardHolder(inflater.inflate(R.layout.view_icon_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SelectedAppCardHolder holder, int position) {
            String name = selectedApps.get(position).selectedAppName;

            try {
                PackageManager packageManager = requireActivity().getApplication().getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(name, 0);
                Drawable icon = packageManager.getApplicationIcon(applicationInfo);

                holder.icon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return selectedApps != null ? selectedApps.size() : 0;
        }

        public class SelectedAppCardHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            public SelectedAppCardHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.item_icon);
            }
        }
    }
}