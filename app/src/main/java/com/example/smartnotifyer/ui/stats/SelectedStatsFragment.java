package com.example.smartnotifyer.ui.stats;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

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

public class SelectedStatsFragment extends Fragment {
    Toolbar toolbar;

    private final long hour = 60 * 60 * 1000;
    private long end = System.currentTimeMillis();
    private long start = end - hour;

    private SelectedStatAdapter statAdapterSelected;
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

        RecyclerView recyclerViewSelected = root.findViewById(R.id.selected_stat_list);
        recyclerViewSelected.setLayoutManager(new LinearLayoutManager(requireContext()));
        statAdapterSelected = new SelectedStatAdapter();
        recyclerViewSelected.setAdapter(statAdapterSelected);

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        statsViewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        statsViewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            statAdapterSelected.setStatsList(stats);
        });

        statsViewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            List<Stat> selectedStats = new ArrayList<>();

            AsyncTask.execute(() -> {
                List<App> apps = appsViewModel.getAllApps();

                for (int i = 0; i < stats.size(); i++) {
                    for (int j = 0; j < apps.size(); j++) {
                        if (stats.get(i).statName.equals(apps.get(j).appName)){
                            selectedStats.add(stats.get(i));
                            Log.i("TAGSSS KA MEKA", apps.get(j).toString());
                        }
                    }
                }
            });

            statAdapterSelected.setStatsList(selectedStats);
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

    private class SelectedStatAdapter extends RecyclerView.Adapter<SelectedStatAdapter.StatCardHolder> {
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
}