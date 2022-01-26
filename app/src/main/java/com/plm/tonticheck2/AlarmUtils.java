package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.AlarmManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtils extends BroadcastReceiver {
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static void setAlarm(Context context, Calendar calendar, int id){
        Intent intent=new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager aMgr=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        aMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(Context context, int id){
        Intent intent =  new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int id=intent.getExtras().getInt("id");
        TontiTaskList list=GsonUtils.getTontiTaskListById(id);
        Toast.makeText(context,"ALARM",Toast.LENGTH_SHORT).show();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }
}
