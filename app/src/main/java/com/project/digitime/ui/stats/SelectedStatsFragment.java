package com.project.digitime.ui.stats;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.project.digitime.MainActivity;
import com.project.digitime.R;
import com.project.digitime.adapter.SelectedAppAdapter;
import com.project.digitime.database.App;
import com.project.digitime.database.Stat;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.mvvm.StatsViewModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectedStatsFragment extends Fragment {
    TextView tvUsageSelectedApps;
    TextView tvUsageLimit;

    LocalTime localTime = LocalTime.now();

    private final long hour = (long) 60 * 60 * 1000 * localTime.getHour();
    private final long minute = (long) 60 * 1000 * localTime.getMinute();
    private long end = System.currentTimeMillis();
    private long start = end - hour - minute;

    private List<Stat> selectedStats = new ArrayList<>();
    private List<Stat> stats = new ArrayList<>();
    private long usageSelected;

    private StatAdapter statAdapter;
    private StatsViewModel statsViewModel;
    private AppsViewModel appsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_selected_stats, container, false);

        tvUsageSelectedApps = root.findViewById(R.id.tv_usage_apps_selected);
        tvUsageLimit = root.findViewById(R.id.info_usage_limit_toolbar_selected);

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.stat_list_selected);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        statAdapter = new SelectedStatsFragment.StatAdapter();
        recyclerView.setAdapter(statAdapter);

//        AsyncTask.execute(() -> {
//            List<App> selectedApps = new ArrayList<>();
//            selectedApps.addAll(appsViewModel.getAllApps());
//
//            stats.addAll(statsViewModel.getAllStats());
//            int usageSelected = 0;
//            for (int i = 0; i < selectedApps.size(); i++) {
//                for (int j = 0; j < stats.size(); j++) {
//                    if (selectedApps.get(i).appName.equals(stats.get(j).statName)){
//                        usageSelected += stats.get(j).statTime;
//                    }
//                }
//            }
//        });

        AsyncTask.execute(() -> {
            List<Stat> stats = new ArrayList<>();
            stats.addAll(statsViewModel.getAllStats());

            int usage = 0;
            for (int i = 0; i < stats.size(); i++) {
                usage += stats.get(i).statTime;
            }

            List<App> selectedApps = new ArrayList<>();
            selectedApps.addAll(appsViewModel.getAllApps());

            usageSelected = 0;
            for (int i = 0; i < selectedApps.size(); i++) {
                for (int j = 0; j < stats.size(); j++) {
                    if (selectedApps.get(i).appName.equals(stats.get(j).statName)){
                        selectedStats.add(stats.get(j));
                        usageSelected += stats.get(j).statTime;
                    }
                }
            }
            Collections.sort(selectedApps);
            statAdapter.setStatsList(selectedStats);

            requireActivity().runOnUiThread(() -> {
                tvUsageSelectedApps.setText(UsageConverter.convertMilliToString(this.usageSelected));
                tvUsageLimit.setText(UsageConverter.convertMinuteToString(MainActivity.usageLimit));
            });
        });

        Button btnGoBack = root.findViewById(R.id.btn_back_selected);
        btnGoBack.setOnClickListener(v -> {
            StatsFragment fragment = new StatsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return root;
    }

    private class StatAdapter extends RecyclerView.Adapter<SelectedStatsFragment.StatAdapter.StatCardHolder> {
        List<Stat> statsList;

        public void setStatsList(List<Stat> statsList) {
            this.statsList = new ArrayList<>(statsList);
            notifyDataSetChanged();
        }
        public void setFilteredList(List<Stat> filteredList){
            this.statsList = filteredList;
            notifyDataSetChanged();
        }
        public SelectedStatsFragment.StatAdapter.StatCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return new SelectedStatsFragment.StatAdapter.StatCardHolder(inflater.inflate(R.layout.view_stat_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder (@NonNull SelectedStatsFragment.StatAdapter.StatCardHolder holder, int position) {
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
}