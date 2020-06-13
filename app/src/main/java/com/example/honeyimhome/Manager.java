package com.example.honeyimhome;


import android.app.Application;
import android.content.IntentFilter;

public class Manager extends Application {
    public LocalSendSmsBroadcastReceiver localSendSmsBroadcastReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("POST_PC.ACTION_SEND_SMS");
        localSendSmsBroadcastReceiver = new LocalSendSmsBroadcastReceiver();
    }
}
