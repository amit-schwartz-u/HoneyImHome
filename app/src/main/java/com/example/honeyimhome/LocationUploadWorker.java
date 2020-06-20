package com.example.honeyimhome;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class LocationUploadWorker extends Worker {

    LocationTracker locationTracker;
    LocationManager locationManager;
    LocalSendSmsBroadcastReceiver localSendSmsBroadcastReceiver = new LocalSendSmsBroadcastReceiver(getApplicationContext());//todo needed?

    public LocationUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("got to", "LocationUploadWorker doWork");

        if (checkForFineLocationPermission() && isSendSmsRuntimePermissionGranted()) {
            Log.e("got to", "LocationUploadWorker doWork with Permissions");

            LocationInfo locationInfo = MyPreferences.getHomeLocationFromMyPref(getApplicationContext());
            String phoneNumber = MyPreferences.getPhoneNumberMyPref(getApplicationContext());
            if (phoneNumber != null && locationInfo != null){
                Log.e("got to", "LocationUploadWorker doWork with Permissions with number and location");
                Looper.prepare();
                //locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//                locationTracker = new LocationTracker(getApplicationContext(), locationManager);
//                locationTracker.startTracking();
            }

        }
        return Result.success();
    }

    private boolean checkForFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            return true;
        }
        return false;
    }

    private boolean isSendSmsRuntimePermissionGranted() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}