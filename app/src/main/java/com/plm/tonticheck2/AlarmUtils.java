package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.AlarmManagerCompat;

import com.plm.tonticheck2.model.TontiTaskList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtils extends BroadcastReceiver{
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");
    //private final Handler handler;

    /*public AlarmUtils(Handler handler){
        this.handler=handler;
    }*/
    




    @Override
    public void onReceive(Context context, Intent intent) {
        int id=intent.getExtras().getInt("id");
        TontiTaskList list=GsonUtils.getTontiTaskListById(context,id);
        //cancelAlarm(context,id);

        Toast.makeText(context,"alarma de "+list.name,Toast.LENGTH_LONG).show();
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        /*handler.post(new Runnable() {
            @Override
            public void run() {
                Message m=new Message();
                m.getData().putInt("id",id);
                handler.dispatchMessage(m);
            }
        });*/
    }
}
