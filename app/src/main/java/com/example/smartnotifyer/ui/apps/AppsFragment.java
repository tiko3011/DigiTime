package com.example.smartnotifyer.ui.apps;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotifyer.R;
import com.example.smartnotifyer.alarm.AlarmReceiver;
import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.limits.LimitFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppsFragment extends Fragment {
    public static int weeklyUsage = 0, count = 0;
    public List<App> appList = new ArrayList<>();

    private AppsFragment.AppAdapter appAdapter;
    private AppsViewModel appsViewModel;
    
    TextView tvWeeklyUsage;
    Button btnNext;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        RecyclerView recyclerView = root.findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appAdapter = new AppAdapter();
        recyclerView.setAdapter(appAdapter);

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        appsViewModel.deleteAllApps();

        AlarmReceiver.selectedApps.clear();

        PackageManager packageManager = requireActivity().getApplication().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        @SuppressLint("QueryPermissionsNeeded")
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        UsageStatsManager usageStatsManager = (UsageStatsManager) requireActivity().getApplication().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, 0, System.currentTimeMillis());

        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            String appName = resolveInfo.activityInfo.packageName;;
            long usageWeekly = 0L;

            for (int j = 0; j < usageStatsList.size(); j++) {
                UsageStats usageStats = usageStatsList.get(j);

                if (usageStats.getPackageName().equals(appName)) {
                    usageWeekly = usageStats.getTotalTimeInForeground();
                    break;
                }
            }
            long appUsage = usageWeekly / 7;
            appList.add(new App(appName, appUsage));
        }

        weeklyUsage = 0; count = 0;
        Collections.sort(appList);
        appAdapter.setAppList(appList);

        tvWeeklyUsage = root.findViewById(R.id.tv_weekly_usage);
        btnNext = root.findViewById(R.id.btn_next);

        if (tvWeeklyUsage.getText().toString().equals("Please select apps to continue")){
            btnNext.setEnabled(false);
        }

        btnNext.setOnClickListener(v -> {
            editor.putBoolean("isNextClicked", true);
            editor.putInt("averageWeeklyUsage", weeklyUsage);
            editor.apply();

            LimitFragment fragment = new LimitFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main, fragment); // R.id.container is the ID of the container in your activity's layout
            transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
            transaction.commit();
        });

        return root;
    }

    private class AppAdapter extends RecyclerView.Adapter<AppsFragment.AppAdapter.AppCardHolder> {
        List<App> appsList;

        public void setAppList(List<App> appsList) {
            this.appsList = new ArrayList<>(appsList);
            notifyDataSetChanged();
        }
        public void setFilteredList(List<App> filteredList){
            this.appsList = filteredList;
            notifyDataSetChanged();
        }
        public AppsFragment.AppAdapter.AppCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            return new AppsFragment.AppAdapter.AppCardHolder(inflater.inflate(R.layout.view_app_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder (@NonNull AppsFragment.AppAdapter.AppCardHolder holder, int position) {
            holder.setIsRecyclable(false);
            String packageName = appsList.get(position).appName;
            long usageWeekly = appsList.get(position).appUsageWeekly;
            boolean isChecked = appsList.get(position).isChecked;

            try {
                PackageManager packageManager = requireActivity().getApplication().getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);

                Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                String name = packageManager.getApplicationLabel(applicationInfo).toString();
                String appUsage = UsageConverter.convertMilliToString(usageWeekly);

                holder.icon.setImageDrawable(icon);
                holder.nameText.setText(name);
                holder.timeText.setText(appUsage);
                holder.checkBox.setChecked(isChecked);
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("ERROR_APPS", packageName);
            }
        }

        @Override
        public int getItemCount() {
            return appsList != null ? appsList.size() : 0;
        }

        public class AppCardHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView timeText;
            ImageView icon;
            CheckBox checkBox;

            public AppCardHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.item_app_name_tv);
                timeText = view.findViewById(R.id.item_app_usage_tv);
                icon = view.findViewById(R.id.item_app_icon_iv);
                checkBox = view.findViewById(R.id.item_checkbox_select_app);

                view.setOnClickListener(v -> {
                    int adapterPosition = getAdapterPosition();

                    if (checkBox.isChecked()){
                        checkBox.setChecked(false);
                        count--;
                        weeklyUsage -= UsageConverter.convertStringToHour(timeText.getText().toString());
                        appsViewModel.deleteApp(appsList.get(adapterPosition));
                    } else {
                        count++;
                        checkBox.setChecked(true);
                        weeklyUsage += UsageConverter.convertStringToHour(timeText.getText().toString());
                        appsViewModel.addApp(appsList.get(adapterPosition));
                    }
                    appsList.get(adapterPosition).setChecked(checkBox.isChecked());
                    
                    setWeeklyUsage();
                });

                checkBox.setOnClickListener(v -> {
                    int adapterPosition = getAdapterPosition();
                    appsList.get(adapterPosition).setChecked(true);

                    if (checkBox.isChecked()){
                        count++;
                        weeklyUsage += UsageConverter.convertStringToHour(timeText.getText().toString());
                        appsViewModel.addApp(appsList.get(adapterPosition));
                    } else {
                        count--;
                        weeklyUsage -= UsageConverter.convertStringToHour(timeText.getText().toString());
                        appsViewModel.deleteApp(appsList.get(adapterPosition));
                    }

                    setWeeklyUsage();
                });
            }
        }
    }

    public void setWeeklyUsage(){
        if (count > 0){
            String strWeeklyUsage = UsageConverter.convertMinuteToString(weeklyUsage);
            tvWeeklyUsage.setText("You used these apps " + strWeeklyUsage);
        } else {
            tvWeeklyUsage.setText("Please select apps to continue");
        }

        if (tvWeeklyUsage.getText().toString().equals("Please select apps to continue")){
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }
}