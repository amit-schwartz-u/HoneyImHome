package com.example.honeyimhome;

import android.Manifest;

import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationTracker {
    Context context;
    LocationManager locationManager;
    LocationInfo currentLocationInfo;

    private static final int REQUEST_LOCATION = 1;

    public LocationTracker(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;
        currentLocationInfo = new LocationInfo();
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double accuracy = location.getAccuracy();
            Log.e("debug", "onLocationChanged**" + String.valueOf(latitude) + String.valueOf(longitude));
            currentLocationInfo.setLatitude(String.valueOf(latitude));
            currentLocationInfo.setLongitude(String.valueOf(longitude));
            currentLocationInfo.setAccuracy(String.valueOf(accuracy));
            MainActivity.setLocationTextViesToCurrentLocation(currentLocationInfo);
            if (accuracy < 50) {
                Log.e("debug", " accuracy < 50 ***");
                MainActivity.addSetHomeLocationButtonToScreen();
            } else {
                MainActivity.removeSetHomeLocationButtonFromScreen();
            }
            Intent intent = new Intent("new_location");
            intent.putExtra(LocalSendSmsBroadcastReceiver.PHONE_NUMBER, MyPreferences.getPhoneNumberMyPref(context));
            intent.putExtra(LocalSendSmsBroadcastReceiver.CONTENT, "Honey I'm Home!");
            intent.setAction("POST_PC.ACTION_SEND_SMS");
            context.sendBroadcast(intent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void startTracking() {
        Log.e("got to ", "LocationTracker startTracking ***");
        getLocation();
    }

    private void getLocation() {
        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListenerGPS);
            Log.e("got to ", "LocationTracker getLocation requestLocationUpdates ***");
        }

    }

    public void stopTracking() {
        Intent intent = new Intent("stop_tracking");
        context.sendBroadcast(intent);
    }

}
