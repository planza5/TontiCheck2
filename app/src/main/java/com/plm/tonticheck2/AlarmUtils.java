package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.plm.tonticheck2.model.TontiTaskList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Clase utilizada para establecer y cancelar alarmas para las tareas de la lista.
 */
public class AlarmUtils extends BroadcastReceiver {
    private static Observable observable;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Método que comprueba si una fecha y hora dada ya ha pasado o no.
     * @param alarm La fecha y hora a comprobar en formato de cadena (dd/MM/yyyy HH:mm).
     * @return Verdadero si la fecha y hora ya ha pasado, falso en caso contrario.
     */
    public static boolean isInThePast(String alarm) {
        try {
            return sdf.parse(alarm).before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }

    /**
     * Método utilizado para establecer una alarma para una tarea de la lista.
     * @param context El contexto de la aplicación.
     * @param calendar El calendario que contiene la fecha y hora de la alarma.
     * @param id El ID de la tarea para la cual se establecerá la alarma.
     */
    public void setAlarm(Context context, Calendar calendar, int id) {
        if (observable == null) {
            observable = new Observable();
        }

        // Crea un intent para la clase AlarmUtils con el id de la tarea
        Intent intent = new Intent(context.getApplicationContext(), AlarmUtils.class);
        intent.putExtra("id", id);

        // Crea un PendingIntent para la alarma
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        // Obtiene el servicio AlarmManager
        AlarmManager aMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Establece la alarma en el tiempo exacto especificado en el calendario
        aMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * Método utilizado para cancelar una alarma para una tarea de la lista.
     * @param context El contexto de la aplicación.
     * @param id El ID de la tarea para la cual se cancelará la alarma.
     */
    public void cancelAlarm(Context context, int id) {
        // Crea un intent para la clase AlarmUtils con el id de la tarea
        Intent intent = new Intent(context.getApplicationContext(), AlarmUtils.class);
        intent.putExtra("id", id);

        // Crea un PendingIntent para la alarma
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        // Obtiene el servicio AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancela la alarma
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Método utilizado para recibir la señal de que una tarea de la lista ha alcanzado su hora de alarma.
     * @param context El contexto de la aplicación.
     * @param intent El intent que recibió la señal.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getExtras().getInt("id");
        TontiTaskList list = GsonUtils.getTontiTaskListById(context, id);
        list.alarm = null;

        Log.d(Ctes.TAG, "onReceive");

        //ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        //toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        Toast.makeText(context, "La lista de verificación llamada " + list.name + " está a punto de expirar!!",
                Toast.LENGTH_SHORT).show();
        createNotification(context, context.getString(R.string.app_name) + " alerta",
                "La lista de verificación llamada " + list.name + " está a punto de expirar!!",
                context.getString(R.string.channel_id), id);

    }

    /**
     * Método utilizado para crear una notificación en la barra de notificaciones del sistema.
     * @param context El contexto de la aplicación.
     * @param title El título de la notificación.
     * @param text El texto de la notificación.
     * @param channelId El ID del canal de notificación.
     * @param id El ID de la tarea asociada a la notificación.
     */
    private void createNotification(Context context, String title, String text, String channelId, int id) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.checkjpg)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat nmp = NotificationManagerCompat.from(context);
        nmp.notify(100, builder.build());
    }
}

