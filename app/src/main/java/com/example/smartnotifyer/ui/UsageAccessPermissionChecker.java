package com.example.smartnotifyer.ui;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class UsageAccessPermissionChecker {
    private static final String USAGE_ACCESS_PERMISSION = "android.permission.PACKAGE_USAGE_STATS";

    public static boolean isUsageAccessGranted(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return (mode == AppOpsManager.MODE_ALLOWED);
    }
}