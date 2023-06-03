package com.example.smartnotifyer.ui.stats;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smartnotifyer.R;
import com.example.smartnotifyer.database.App;
import com.example.smartnotifyer.database.Stat;
import com.example.smartnotifyer.mvvm.AppsViewModel;
import com.example.smartnotifyer.mvvm.StatsViewModel;
import com.example.smartnotifyer.ui.apps.AppsFragment;
import com.example.smartnotifyer.ui.limits.LimitFragment;
import com.example.smartnotifyer.ui.permission.PermissionFragment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {
    Toolbar toolbar;

    private final long hour = 60 * 60 * 1000;
    private long end = System.currentTimeMillis();
    private long start = end - hour;

    private List<SelectedApp> selectedApps = new ArrayList<>();

    private StatAdapter statAdapter;
    private SelectedAppAdapter statAdapterSelected;
    private StatsViewModel statsViewModel;
    private AppsViewModel appsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stats, container, false);

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
        });


        RecyclerView recyclerViewSelected = root.findViewById(R.id.selected_stat_list);
        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        statAdapterSelected = new SelectedAppAdapter();
        recyclerViewSelected.setAdapter(statAdapterSelected);

        AsyncTask.execute(() -> {
            List<App> apps = appsViewModel.getAllApps();
            Log.i("KA TAGSS BIIH", "Apps: --> " + apps.size());

            for (int i = 0; i < apps.size(); i++) {
                selectedApps.add(new SelectedApp(apps.get(i).appName));
            }
            Log.i("KA TAGSS BIIH", "Selected: --> " + selectedApps.size());
            statAdapterSelected.setSelectedApps(selectedApps);
        });

        statsViewModel.deleteAllStats();
        statsViewModel.addStatsFromSystemDaily(start, end);
        statsViewModel.deleteDuplicates();

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
        } else if (id == R.id.Access) {
            PermissionFragment fragment = new PermissionFragment();
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