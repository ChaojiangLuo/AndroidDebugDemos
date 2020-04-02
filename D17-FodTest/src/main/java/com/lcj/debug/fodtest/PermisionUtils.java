package com.lcj.debug.fodtest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * Created by luocj on 2/6/18.
 */

public class PermisionUtils {
    // Permissions
    private static final int REQUEST_PERMISIONS_FLAG = 1;
    private static String[] REQUEST_PERMISIONS = {
            Manifest.permission.SYSTEM_ALERT_WINDOW};

    /**
     * @param activity
     */
    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.SYSTEM_ALERT_WINDOW);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, REQUEST_PERMISIONS,
                    REQUEST_PERMISIONS_FLAG);
        }
    }
}