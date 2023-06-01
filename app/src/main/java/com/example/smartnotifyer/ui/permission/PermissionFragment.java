package com.example.smartnotifyer.ui.permission;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartnotifyer.MainActivity;
import com.example.smartnotifyer.R;
import com.example.smartnotifyer.ui.UsageAccessPermissionChecker;
import com.example.smartnotifyer.ui.apps.AppsFragment;
import com.example.smartnotifyer.ui.limits.LimitFragment;

public class PermissionFragment extends Fragment {
    TextView btnPermission;
    public static boolean isUsageGranted;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_permission, container, false);

        if (isUsageGranted){
            AppsFragment fragment = new AppsFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_permission, fragment); // R.id.container is the ID of the container in your activity's layout
            transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
            transaction.commit();
        }

        btnPermission = root.findViewById(R.id.btn_permission);
        btnPermission.setOnClickListener(v -> {
            if (isUsageGranted) {
                AppsFragment fragment = new AppsFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_permission, fragment); // R.id.container is the ID of the container in your activity's layout
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
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

        Log.i("TAGGGS ARAA", String.valueOf(isUsageGranted));
    }

    public void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Request app usage permission?");
        builder.setMessage("You have to give permission to view device usage statistics for this app to work. Only the amount of time spent in each app will be accessed. No personal information will be exposed.");

        // add the buttons
        builder.setPositiveButton("Enable", (dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS));
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}