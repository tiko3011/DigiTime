package com.example.smartnotifyer.ui.apps;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotifyer.R;
import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.database.Stat;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.limits.LimitActivity;
import com.example.smartnotifyer.ui.limits.LimitFragment;
import com.example.smartnotifyer.ui.stats.StatsFragment;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {

    public static int weeklyUsage = 0, count = 0;

    private AppsFragment.AppAdapter appAdapter;
    private AppsViewModel appsViewModel;

    TextView tvWeeklyUsage;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appAdapter = new AppAdapter();
        recyclerView.setAdapter(appAdapter);

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);

        appsViewModel.getApps().observe(getViewLifecycleOwner(), apps -> {
            appAdapter.setAppList(apps);
        });

        appsViewModel.deleteAllApps();
        appsViewModel.addInstalledApps();

        tvWeeklyUsage = root.findViewById(R.id.tv_weekly_usage);
        Button btnNext = root.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LimitFragment fragment = new LimitFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_apps_list, fragment); // R.id.container is the ID of the container in your activity's layout
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
                transaction.commit();
            }
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

            try {
                PackageManager packageManager = requireActivity().getApplication().getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);

                Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                String name = packageManager.getApplicationLabel(applicationInfo).toString();
                String appUsage = UsageConverter.convertMilliToString(usageWeekly);

                holder.icon.setImageDrawable(icon);
                holder.nameText.setText(name);
                holder.timeText.setText(appUsage);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
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
                    appsList.get(adapterPosition).setChecked(true);
                    if (checkBox.isChecked()){
                        checkBox.setChecked(false);
                        weeklyUsage -= UsageConverter.convertStringToHour(timeText.getText().toString());
                    } else {
                        checkBox.setChecked(true);
                        weeklyUsage += UsageConverter.convertStringToHour(timeText.getText().toString());
                    }

                    setWeeklyUsage(appsList);
                });

                checkBox.setOnClickListener(v -> {
                    int adapterPosition = getAdapterPosition();
                    appsList.get(adapterPosition).setChecked(true);

                    if (checkBox.isChecked()){
                        weeklyUsage += UsageConverter.convertStringToHour(timeText.getText().toString());
                    } else {
                        weeklyUsage -= UsageConverter.convertStringToHour(timeText.getText().toString());
                    }

                    setWeeklyUsage(appsList);
                });
            }
        }
    }

    public void setWeeklyUsage(List<App> apps){
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).isChecked){
                count++;
                break;
            }
        }

        if (count > 0){
            String strWeeklyUsage = UsageConverter.convertMinuteToString(weeklyUsage);
            tvWeeklyUsage.setText("You used these apps " + strWeeklyUsage);
        } else {
            tvWeeklyUsage.setText("Please select apps to continue");
        }
    }
}