package com.project.digitime.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitime.R;
import com.project.digitime.ui.stats.SelectedApp;

import java.util.ArrayList;
import java.util.List;

public class SelectedAppAdapter extends RecyclerView.Adapter<SelectedAppAdapter.SelectedAppCardHolder> {
    List<SelectedApp> selectedApps;
    Context context;

    public SelectedAppAdapter(Context context){
        this.context = context;
    }

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
            PackageManager packageManager = context.getPackageManager();
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