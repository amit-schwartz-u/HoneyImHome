package com.example.honeyimhome;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

public class Manager extends Application {
    public BroadcastReceiver broadcastReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("POST_PC.ACTION_SEND_SMS");
        broadcastReceiver = new LocalSendSmsBroadcastReceiver(this);
        this.registerReceiver(broadcastReceiver, intentFilter);
        Log.e("Manager got to", "registerReceiver LocalSendSmsBroadcastReceiver");

    }
}
