package com.example.honeyimhome;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {
    public static final String PHONE_NUMBER = "PHONE";
    public static final String CONTENT = "CONTENT";
    private Context context;
    private static final String CHANNEL_ID = "CHANNEL_ID_FOR_SMS_NOTIFICATIONS";

    public LocalSendSmsBroadcastReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("got to", "got to LocalSendSmsBroadcastReceiver on receive");

        if (isSendSmsRuntimePermissionGranted(context)) {
            String phoneNumber = intent.getStringExtra(PHONE_NUMBER);
            String smsContent = intent.getStringExtra(CONTENT);
            if (!isInputIsValid(phoneNumber, smsContent)) {
                Log.e("Invalid input", "Invalid input - check phone number or content");
                return;
            } else {
                Log.d("LocalSendSms", " got to send - sending sms");
                SmsManager smgr = SmsManager.getDefault();
                smgr.sendTextMessage(phoneNumber, null, smsContent, null, null);
                fireNotification(context, phoneNumber, smsContent);
            }
        } else {
            Log.e("LocalSendSms", "Error - missing send-sms runtime permission");
            return;
        }
    }

    private void fireNotification(Context context, String phoneNum, String messageContent){
        NotificationManager mNotificationManager;
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID);

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        notificationCompatBuilder.setContentIntent(pendingIntent);
        notificationCompatBuilder.setContentTitle("Honey I'm Home");
        notificationCompatBuilder.setContentText(String.format("Sending sms to %s : %s", phoneNum, messageContent));
        notificationCompatBuilder.setPriority(Notification.PRIORITY_MAX);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelText = String.format("Sending sms to %s : %s", phoneNum, messageContent);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    channelText,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            notificationCompatBuilder.setChannelId(CHANNEL_ID);
        }
        mNotificationManager.notify(0, notificationCompatBuilder.build());
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
