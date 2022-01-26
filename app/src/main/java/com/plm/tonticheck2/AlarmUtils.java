package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.AlarmManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtils extends BroadcastReceiver {
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static void setAlarm(Context context, Calendar calendar, String tag){
        Intent intent=new Intent(context,AlarmUtils.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager aMgr=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        aMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(context,"Alarm will vibrate at "+sdf.format(calendar.getTime()),Toast.LENGTH_SHORT).show();
        Log.d("PABLO",aMgr.getNextAlarmClock()==null?"null":aMgr.getNextAlarmClock().getTriggerTime()+"");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PABLO","ALARM!!");
    }
}
