package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.plm.tonticheck2.model.TontiTaskList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtils extends BroadcastReceiver {
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static boolean isInThePast(String alarm) {
        try{
            return sdf.parse(alarm).before(new Date());
        }catch(Exception ex){
            return true;
        }
    }

    public void setAlarm(Context context, Calendar calendar, int id){
        Intent intent=new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        AlarmManager aMgr=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        aMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    public void cancelAlarm(Context context, int id){
        Intent intent =  new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int id=intent.getExtras().getInt("id");
        TontiTaskList list=GsonUtils.getTontiTaskListById(context,id);
        list.alarm=null;

        //ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        Toast.makeText(context,"Checklist named "+list.name+" is going to expire!!",Toast.LENGTH_SHORT).show();
        createNotification(context,context.getString(R.string.app_name)+ "alert","Checklist named "+list.name+" is going to expire!!",context.getString(R.string.channel_id));
    }


    private  void createNotification(Context context, String title, String text, String channelId){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.checkjpg)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION))
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat nmp=NotificationManagerCompat.from(context);
        nmp.notify(100,builder.build());
    }
}
