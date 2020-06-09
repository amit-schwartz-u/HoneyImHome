package com.example.honeyimhome;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {
    public static final String PHONE_NUMBER = "PHONE";
    public static final String CONTENT = "CONTENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isSendSmsRuntimePermissionGranted(context)) {
            String phoneNumber = intent.getStringExtra(PHONE_NUMBER);
            String smsContent = intent.getStringExtra(CONTENT);
            if (!isInputIsValid(phoneNumber, smsContent)) {
                Log.e("Invalid input", "Invalid input - check phone number or content");
                return;
            } else {
                SmsManager smgr = SmsManager.getDefault(); //todo check?
                smgr.sendTextMessage(phoneNumber, null, smsContent, null, null);
            }
        } else {
            Log.e("MISSING_PERMISSION", "Error - missing send-sms runtime permission");
            return;
        }
    }

    private boolean isInputIsValid(String phoneNumber, String smsContent) {
        if (phoneNumber == null | smsContent == null | phoneNumber.equals("") | smsContent.equals("")) {
            return false;
        }
        return true;

    }

    private boolean isSendSmsRuntimePermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }


}
