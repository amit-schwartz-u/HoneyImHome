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
            Log.e("debug", "**" + String.valueOf(latitude) + String.valueOf(longitude));
            currentLocationInfo.setLatitude(String.valueOf(latitude));
            currentLocationInfo.setLongitude(String.valueOf(longitude));
            currentLocationInfo.setAccuracy(String.valueOf(accuracy));
            MainActivity.setLocationTextViesToCurrentLocation(currentLocationInfo);
            if (accuracy<50){
                MainActivity.addSetHomeLocationButtonToScreen();
            }
            else {
                MainActivity.removeSetHomeLocationButtonFromScreen();
            }
            Intent intent = new Intent("new_location");
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
        getLocation();
    }

    private void getLocation() {
        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {// && ActivityCompat.checkSelfPermission(context,
            //todo delete?
            // Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListenerGPS);
//            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//            LocationInfo currentLocationInfo = new LocationInfo();
//            if (LocationGps != null) {
//                double lat = LocationGps.getLatitude();
//                double longi = LocationGps.getLongitude();
//
//                latitude = String.valueOf(lat);
//                longitude = String.valueOf(longi);
//
//                Log.e("myDebug","Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude);
//                Log.e("myDebug","1***call -LocationGps.getLatitude");
//
//            } else if (LocationNetwork != null) {
//                double lat = LocationNetwork.getLatitude();
//                double longi = LocationNetwork.getLongitude();
//                double accuracy = LocationNetwork.getAccuracy();
//                currentLocationInfo.setLatitude(String.valueOf(lat));
//                currentLocationInfo.setLongitude(String.valueOf(longi));
//                currentLocationInfo.setAccuracy(String.valueOf(accuracy));
//
//                Log.e("myDebug","Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude);
//                Log.e("myDebug","2*call -LocationGps.getLatitude");
//
//            } else if (LocationPassive != null) {
//                double lat = LocationPassive.getLatitude();
//                double longi = LocationPassive.getLongitude();
//
//                latitude = String.valueOf(lat);
//                longitude = String.valueOf(longi);
//                MainActivity.setLocationTextViesToCurrentLocation(currentLocationInfo);
//                showLocationTxt.setText("Your Location:" + "\n" + "Latitude= " + latitude + "\n" + "Longitude= " + longitude);
//            } else {
//                Log.e("Error", "Can't Get Your Location");
//            }
        }

    }

    public void stopTracking() {
    }

}
