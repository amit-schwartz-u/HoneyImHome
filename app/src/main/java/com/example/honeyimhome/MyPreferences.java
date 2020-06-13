package com.example.honeyimhome;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


class MyPreferences {

    public static void saveHomeLocationToMyPref(Context context, LocationInfo locationInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(locationInfo);
        editor.putString("homeLocation", json);
        editor.apply();
    }

    public static LocationInfo getHomeLocationFromMyPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("homeLocation", null);
        Type type = new TypeToken<LocationInfo>() {
        }.getType();
        LocationInfo locationInfo = gson.fromJson(json, type);
        return locationInfo;
    }

    public static void savePhoneNumberMyPref(Context context, String number) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", number);
        editor.apply();
    }

    public static String getPhoneNumberMyPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        return sharedPreferences.getString("phoneNumber", null);
    }
}
