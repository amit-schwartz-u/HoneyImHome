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
    private Context context;
    private LocationManager locationManager;
    private LocationInfo currentLocationInfo;

    private static final int REQUEST_LOCATION = 1;

    public LocationTracker(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;
        this.currentLocationInfo = new LocationInfo();
    }

    private LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double accuracy = location.getAccuracy();
            currentLocationInfo.setLatitude(String.valueOf(latitude));
            currentLocationInfo.setLongitude(String.valueOf(longitude));
            currentLocationInfo.setAccuracy(String.valueOf(accuracy));
            MainActivity.setLocationTextViesToCurrentLocation(currentLocationInfo);
            if (accuracy < 50) {
                Intent intent = new Intent("new_location");
                intent.putExtra("latitude", latitude); //todo needed?
                intent.putExtra("longitude", longitude); //todo needed?
                intent.putExtra("accuracy", accuracy);
                context.sendBroadcast(intent);
                MainActivity.addSetHomeLocationButtonToScreen();
            } else {
                MainActivity.removeSetHomeLocationButtonFromScreen();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            boolean hasTrackingPermission = ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
            if (!hasTrackingPermission) {
                // if there are not permissions- write error log to logcat
                Log.e("LOCATION PERMISSION", "error: no permission to track location");
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            locationManager.removeUpdates(locationListenerGPS);
        }
    };


    public void startTracking() {
        Log.d("got to ", "LocationTracker startTracking ***");
        getLocation();
    }

    private void getLocation() {
        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0, locationListenerGPS);
            Intent intent = new Intent("started_tracking");
            context.sendBroadcast(intent);
            Log.d("got to ", "LocationTracker getLocation requestLocationUpdates ***");
        }

    }

    public void stopTracking() {
        Log.d("LocationTracker", "LocationTracker stop tracking");
        locationManager.removeUpdates(locationListenerGPS);
        Intent intent = new Intent("stopped_tracking");
        context.sendBroadcast(intent);
    }
    public LocationInfo getCurrentLocationInfo(){
        return this.currentLocationInfo;
    }

}
