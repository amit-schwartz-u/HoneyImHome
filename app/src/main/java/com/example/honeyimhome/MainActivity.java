package com.example.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EMPTY_TEXT = "";
    public static final String REQUEST_PERMISSION_MSG = "we need this permission or we can't operate";
    public static final int FINE_LOCATION_REQUEST_CODE = 12; //todo is 12 ok?
    public static final String HOME_LOCATION_PATTERN = "your home location is defined as: ";
    LocationTracker locationTracker;
    Button btnTracking;
    LocationManager locationManager;
    BroadcastReceiver broadcastReceiver;
    private static final String LATITUDE_PATTERN = "latitude: ";
    private static final String LONGITUDE_PATTERN = "longitude: ";
    private static final String ACCURACY_PATTERN = "accuracy: ";
    private static TextView tvLocation;
    private static TextView tvLongitude;
    private static TextView tvAccuracy;
    private LocationInfo homeLocationInfo;
    private static Button btnSetHomeLocation;
    private static Button btnClearHomeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTracking = (Button) findViewById(R.id.btn_tracking);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationTracker = new LocationTracker(this, locationManager);
        homeLocationInfo = MyPreferences.getHomeLocationFromMyPref(getApplicationContext());
        btnSetHomeLocation = (Button) findViewById(R.id.btn_set_home_location);
        btnClearHomeLocation = (Button) findViewById(R.id.btn_clear_home);
        broadcastReceiver = new BroadcastReceiver() { //todo is this ok?
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //todo should I use action here and call stop/start tracking from here
            }
        };
        setHomeLocationTextView();
    }

    private void initializeTextViews() {
        tvLocation = findViewById(R.id.tv_latitude);
        tvLongitude = findViewById(R.id.tv_longitude);
        tvAccuracy = findViewById(R.id.tv_accuracy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    public void trackingButtonOnClick(View view) {
        if (isTrackingOff()) {
            if (checkForFineLocationPermission()) {
                startTrackingLocation();
                Log.e("myDebug", "***call -startTrackingLocation");
            } else {
                askForPermission();
            }
        } else {//if the tracking is already running and the user asked to stop
            stopTrackingLocation();
        }
    }

    private boolean isTrackingOff() {
        return btnTracking.getText().equals("start tracking location");
    }

    public void startTrackingLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //call to enable gps
            OnGPS();
            //todo what happens when stop after a..
        } else {
            locationTracker.startTracking();
            btnTracking.setText("stop tracking");
            addSetHomeLocationButtonToScreen();
            sendBroadcastMessage("started");//todo action?
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void stopTrackingLocation() {
        locationTracker.stopTracking();
        btnTracking.setText("start tracking location");
        removeSetHomeLocationButtonFromScreen();
        setLocationTextViewsToDefaultPatterns();
        sendBroadcastMessage("stopped");//todo action?
    }

    public void sendBroadcastMessage(String message){
        Intent intent = new Intent(message);
        sendBroadcast(intent);
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTrackingLocation();
            } else {
                Toast.makeText(getApplicationContext(), REQUEST_PERMISSION_MSG, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkForFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            return true;
        }
        return false;
    }
    //*********************************************************************************************

    private void setHomeLocationTextView() {
        if (homeLocationInfo != null && homeLocationInfo.getLongitude() != null) {
            addHomeLocationToScreen(true);
            addClearHomeButtonToScreen();
        } else {
            removeHomeLocationTextViewFromScreen();
            removeClearHomeLocationButtonFromScreen();
        }
        initializeTextViews();
        if (isTrackingOff()){
            removeSetHomeLocationButtonFromScreen();
        }
    }

    private void removeClearHomeLocationButtonFromScreen() {
        btnClearHomeLocation.setVisibility(View.INVISIBLE);
    }

    private void addClearHomeButtonToScreen() {
        btnClearHomeLocation.setVisibility(View.VISIBLE);
    }

    public static void addSetHomeLocationButtonToScreen() {
        btnSetHomeLocation.setVisibility(View.VISIBLE);
    }

    public static void removeSetHomeLocationButtonFromScreen() {
        btnSetHomeLocation.setVisibility(View.INVISIBLE);
    }

    private void addHomeLocationToScreen(Boolean calledFromOnCreate) {
        TextView tvHomeLocation = findViewById(R.id.tv_home_location);
        if (!calledFromOnCreate) {
            homeLocationInfo = locationTracker.currentLocationInfo;
        }
        tvHomeLocation.setText(HOME_LOCATION_PATTERN + homeLocationInfo.getLatitude() + " "
                + homeLocationInfo.getLongitude());
        tvHomeLocation.setVisibility(View.VISIBLE);
    }

    public void setHomeButtonOnClick(View view) {
        homeLocationInfo = locationTracker.currentLocationInfo;
        MyPreferences.saveHomeLocationToMyPref(getApplicationContext(), homeLocationInfo);
        addHomeLocationToScreen(false);
        addClearHomeButtonToScreen();
    }

    private void removeHomeLocationTextViewFromScreen() {
        TextView tvHomeLocation = findViewById(R.id.tv_home_location);
        tvHomeLocation.setVisibility(View.INVISIBLE);
    }

    //*********************************************************************************************

    public static void setLocationTextViesToCurrentLocation(LocationInfo locationInfo) {
        setLocationTextVies(LATITUDE_PATTERN + locationInfo.getLatitude(),
                LONGITUDE_PATTERN + locationInfo.getLongitude(),
                ACCURACY_PATTERN + locationInfo.getAccuracy());
    }

    public static void setLocationTextViewsToDefaultPatterns() {
        setLocationTextVies(LATITUDE_PATTERN, LONGITUDE_PATTERN, ACCURACY_PATTERN);
    }

    private static void setLocationTextVies(String latitude, String longitude, String accuracy) {
        tvLocation.setText(latitude);
        tvLongitude.setText(longitude);
        tvAccuracy.setText(accuracy);
    }

    public void clearHomeButtonOnClick(View view) {
        removeHomeLocationTextViewFromScreen();
        removeSetHomeLocationButtonFromScreen();
        removeClearHomeLocationButtonFromScreen();
        MyPreferences.saveHomeLocationToMyPref(this, null);
    }
}
