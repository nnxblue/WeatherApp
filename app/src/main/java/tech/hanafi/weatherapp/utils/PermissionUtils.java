package tech.hanafi.weatherapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import tech.hanafi.weatherapp.R;


/**
 * Created by Han on 10/3/17.
 */

public class PermissionUtils {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 10;


    private static boolean isPermissionGranted(final Activity activity, final String permission, final int requestCode, String rationale) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

                new AlertDialog.Builder(activity)
                        .setMessage(rationale)
                        .setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
            return false;
        }

        return true;

    }

    public static boolean isLocationPermitted(final Activity activity) {
        boolean locationPermissionGranted = isPermissionGranted(activity, Manifest.permission.ACCESS_COARSE_LOCATION,
                PermissionUtils.LOCATION_PERMISSION_REQUEST_CODE, activity.getString(R.string.permission_location));


        return locationPermissionGranted;
    }

}
