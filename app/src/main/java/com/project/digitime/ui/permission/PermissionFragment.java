package com.project.digitime.ui.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.digitime.MainActivity;
import com.project.digitime.R;
import com.project.digitime.mvvm.AppsViewModel;
import com.project.digitime.ui.apps.AppsFragment;
import com.project.digitime.ui.stats.StatsFragment;

public class PermissionFragment extends Fragment {
    TextView btnPermission;
    public static boolean isUsageGranted;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;

    AppsViewModel appsViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isButtonClicked = sharedPreferences.getBoolean("isNextClicked", false);

        View root = inflater.inflate(R.layout.fragment_permission, container, false);
        appsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);

        if (isUsageGranted) {
            if (isButtonClicked){
                StatsFragment fragment = new StatsFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_main, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            } else {
                AppsFragment fragment = new AppsFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_main, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else {
            AsyncTask.execute(() -> {
                appsViewModel.deleteAllApps();
            });
        }

        btnPermission = root.findViewById(R.id.btn_permission);
        btnPermission.setOnClickListener(v -> {
            if (isUsageGranted) {
                    AppsFragment fragment = new AppsFragment();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_main, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
            } else {
                buildDialog();
            }
        });

        return root;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();

        if (isUsageGranted){
            btnPermission.setBackgroundColor(Color.GREEN);
            btnPermission.setText("Usage Granted!\n Click to continue");
        }
    }

    public void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Request app usage permission");
        builder.setMessage("You have to give permission to view device usage statistics for this app to work. Only the amount of time spent in each app will be accessed.\n\n");

        builder.setPositiveButton("Enable", (dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS));
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}