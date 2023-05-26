package com.example.smartnotifyer.ui.apps;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.smartnotifyer.mvvm.StatsViewModel;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.stats.StatsFragment;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {

    private AppsFragment.AppAdapter appAdapter;
    private AppsViewModel appsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_apps, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        appAdapter = new AppAdapter();
        recyclerView.setAdapter(appAdapter);

        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        appsViewModel.deleteAllApps();

        appsViewModel.getApps().observe(getViewLifecycleOwner(), apps -> {
            appAdapter.setAppList(apps);
        });

        appsViewModel.addInstalledApps();

        Button btnNext = root.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_apps_list, new StatsFragment());
                transaction.addToBackStack(null);
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
            String name = appsList.get(position).appName;
            holder.nameText.setText(name);
        }


        @Override
        public int getItemCount() {
            return appsList != null ? appsList.size() : 0;
        }

        public class AppCardHolder extends RecyclerView.ViewHolder {
            TextView nameText;

            public AppCardHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.item_app_name_tv);

            }
        }
    }
}