package com.example.honeyimhome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.concurrent.futures.CallbackToFutureAdapter;


import com.google.common.util.concurrent.ListenableFuture;

public class LocationUploadWorker extends ListenableWorker {

    private Context context;
    static String TAG = "LOCATION_TRACKER_WORKER";
    LocationTracker locationTracker;
    LocationManager locationManager;
    public LocationInfo myCurrentLocationInfo;
    private String phoneNumber = "";
    private LocationInfo homeLocation;
    private BroadcastReceiver broadcastReceiver;
    private CallbackToFutureAdapter.Completer<Result> callback = null;


    public LocationUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.locationTracker = new LocationTracker(this.context, locationManager);
        this.myCurrentLocationInfo =new LocationInfo();
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        ListenableFuture<Result> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Nullable
            @Override
            public Object attachCompleter(@NonNull CallbackToFutureAdapter.Completer<Result> completer) {
                callback = completer;
                return null;
            }
        });
        if (!checkForFineLocationPermission() || !isSendSmsRuntimePermissionGranted()) {
            this.callback.set(Result.success());
        }
        setBroadcastReceiver();
        this.homeLocation = MyPreferences.getHomeLocationFromMyPref(this.context);
        this.phoneNumber = MyPreferences.getPhoneNumberMyPref(this.context);
        if ("".equals(phoneNumber) || homeLocation == null) {
            this.callback.set(Result.success());
        } else {
            Log.d("LocationUploadWorker", "started tracking");
            this.locationTracker.startTracking();
        }
        return future;
    }

    private void setBroadcastReceiver() {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("LocationUploadWorker", "GOT TO ON RECEIVE");
                if ("new_location".equals(intent.getAction())) {
                    LocationInfo locationInfo = locationTracker.getCurrentLocationInfo();
                    if (locationInfo != null) {
                        onReceivedBroadcastNewLocation();

                        myCurrentLocationInfo.setLatitude(String.valueOf(intent.getDoubleExtra("latitude",0)));
                        myCurrentLocationInfo.setLongitude(String.valueOf(intent.getDoubleExtra("longitude",0)));
                        myCurrentLocationInfo.setAccuracy(String.valueOf(intent.getDoubleExtra("accuracy",0)));
                    }
                }
            }
        };
        this.context.registerReceiver(this.broadcastReceiver, new IntentFilter("new_location"));
        Log.d("LocationUploadWorker", "registerReceiver broadcastReceiver in LocationUploadWorker");
    }

    private void onReceivedBroadcastNewLocation() {
        Log.d("LocationUploadWorker", "got to onReceivedBroadcastNewLocation");
        this.context.unregisterReceiver(this.broadcastReceiver);
        LocationInfo currentLocation = this.locationTracker.getCurrentLocationInfo();
        if (currentLocation.getLongitude() == null || currentLocation.getLatitude() == null){
            Log.d("LocationUploadWorker", "myBurrentLocationInfo latitude" + myCurrentLocationInfo.getLatitude());
            Log.d("LocationUploadWorker", "currentLocation is null");
            return;
        }
        double currentLocationLatitude = Double.parseDouble(currentLocation.getLatitude());
        double currentLocationLongitude = Double.parseDouble(currentLocation.getLongitude());
        this.locationTracker.stopTracking();
        LocationInfo previousLocation = MyPreferences.getCurrentLocationFromMyPref(this.context);
        if (previousLocation == null) {
            Log.d("LocationUploadWorker", "previousLocation was null. saved new location");
            MyPreferences.saveCurrentLocationToMyPref(getApplicationContext(), currentLocation);
            this.callback.set(Result.success());
            return;
        }
        float[] results = checkIfPreviousCloseEnoughToCurrent(currentLocation, currentLocationLatitude, currentLocationLongitude, previousLocation);
        if (results == null) {
            Log.d("LocationUploadWorker", "Previous is Close Enough To Current (<50)");
            return;
        }
        checkIfDiffLessThenFiftyMetersThenSendSms(currentLocationLatitude, currentLocationLongitude, results);
        this.callback.set(Result.success());
    }

    private void checkIfDiffLessThenFiftyMetersThenSendSms(double currentLocationLatitude, double currentLocationLongitude, float[] results) {
        double home_lat = Double.parseDouble(this.homeLocation.getLatitude());
        double home_lon = Double.parseDouble(this.homeLocation.getLongitude());
        Location.distanceBetween(currentLocationLatitude, currentLocationLongitude, home_lat, home_lon, results);
        if (results[0] < 50) {
            Log.d("LocationUploadWorker", "got to under 50 meters - send POST_PC.ACTION_SEND_SMS***");
            Intent intent = new Intent("POST_PC.ACTION_SEND_SMS");
            intent.putExtra("PHONE", this.phoneNumber);
            intent.putExtra("CONTENT", "Honey I'm Home!");
            this.context.sendBroadcast(intent);
        }
    }

    private float[] checkIfPreviousCloseEnoughToCurrent(LocationInfo currentLocation, double currentLocationLatitude, double currentLocationLongitude, LocationInfo previousLocation) {
        Log.d("LocationUploadWorker", "got to checkIfPreviousCloseEnoughToCurrent");
        MyPreferences.saveCurrentLocationToMyPref(getApplicationContext(), currentLocation);
        double previousLatitude = Double.parseDouble(previousLocation.getLatitude());
        double previousLongitude = Double.parseDouble(previousLocation.getLongitude());
        float[] results = new float[1];
        Location.distanceBetween(previousLatitude, previousLongitude, currentLocationLatitude, currentLocationLongitude, results);
        if (previousLongitude == -1 || results[0] < 50) {
            this.callback.set(Result.success());
            return null;
        }
        return results;
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