package com.plm.tonticheck2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.plm.tonticheck2.databinding.FragmentSecondBinding;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTask;
import com.plm.tonticheck2.model.TontiTaskList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SecondFragment extends Fragment implements TaskListener{
    private FragmentSecondBinding binding;
    private AlarmUtils alarmUtils;
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    //TODO pasar esta variable desde firstfragment de otra forma
    public static Integer position;
    public static TontiApp app;

    private ListView listView;
    private TaskAdapter taskAdapter;
    private Handler handler;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        handler=new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                int id=message.getData().getInt("id");
                Toast.makeText(getContext().getApplicationContext(),"id="+id,Toast.LENGTH_LONG);
                return false;
            }
        });

        alarmUtils=new AlarmUtils();
        
        if(position==null || app==null){
            throw new RuntimeException("Debes sumisistrar position y app");
        }

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setTitle
        MainActivity ma=(MainActivity)getActivity();

        //Setup taskAdapter
        taskAdapter=new TaskAdapter(this.getContext(),R.layout.task);


        for(TontiTask task:app.list.get(position).list){
            taskAdapter.add(task);
        }

        taskAdapter.addListener(this);

        //Setup listview
        listView = (ListView) view.findViewById(R.id.tontitaskview);
        listView.setAdapter(taskAdapter);

        //setupPreviousButton();
        setupAddButton();
        setupCancelAlarm();
        setupAlarmText();

        try {
            setupAlarmButton(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupAlarmText() {
        if(app.list.get(position).alarm==null){
            binding.alarmDateText.setText("");
            binding.alarmTimeText.setText("");
        }else{
            String text=app.list.get(position).alarm;
            int idx=text.indexOf(" ");
            binding.alarmDateText.setText(text.substring(0,idx));
            binding.alarmTimeText.setText(text.substring(idx));
        }
    }

    public void cancelAlarm(){
        app.list.get(position).alarm = null;
        save();
        binding.buttonCancelAlarm.setVisibility(View.INVISIBLE);
        setupAlarmText();
        cancelAlarm(getContext(),app.list.get(position).id);
    }

    private void setupCancelAlarm() {
        if(app.list.get(position).alarm!=null) {

            binding.buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAlarm();
                }
            });
            binding.buttonCancelAlarm.setVisibility(View.VISIBLE);
        }else{
            binding.buttonCancelAlarm.setVisibility(View.INVISIBLE);
        }
    }

    private void setupAlarmButton(View view) throws ParseException{
        //getting day, month, year, hour, minute for pickers
        String alarm=app.list.get(position).alarm;
        Calendar calendar=Calendar.getInstance();

        if(alarm!=null){
            calendar.setTime(sdf.parse(alarm));
        }else{
            calendar.add(Calendar.MINUTE,1);
        }




        binding.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Date Picker
                DatePickerDialog dp=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {
                        //Time Picker
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {
                                Calendar calendar=Calendar.getInstance();
                                calendar.set(Calendar.YEAR,_year);
                                calendar.set(Calendar.MONTH,_month);

                                calendar.set(Calendar.DAY_OF_MONTH,_day);
                                calendar.set(Calendar.HOUR_OF_DAY,_hour);
                                calendar.set(Calendar.MINUTE,_minute);
                                calendar.set(Calendar.SECOND,0);

                                app.list.get(position).alarm=sdf.format(calendar.getTime());
                                binding.buttonAlarm.setBackgroundResource(R.drawable.alarm_on);

                                setAlarm(getContext(),calendar,app.list.get(position).id);


                                setupCancelAlarm();
                                setupAlarmText();
                                save();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                dp.setTitle("Select Date");
                dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });
    }

    private void setAlarm(Context context, Calendar calendar, int id){
        Intent intent=new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager aMgr=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        aMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(Context context, int id){
        TontiTaskList list=GsonUtils.getTontiTaskListById(context,id);
        Toast.makeText(context,"quitando alarma de "+list.name,Toast.LENGTH_LONG).show();

        Intent intent =  new Intent(context.getApplicationContext(),AlarmUtils.class);
        intent.putExtra("id",id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),id,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void setupAddButton() {
        //setup button add
        binding.buttonTaskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskAdapter.add(new TontiTask());
                save();
            }
        });
    }

    /*
    private void setupPreviousButton() {
        //setup previous button
        binding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }*/



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void save(TontiApp app){

    }

    @Override
    public void save() {
        taskAdapter.notifyDataSetChanged();
        listView.invalidate();
        boolean result = GsonUtils.saveApp(adapterToTontiTask(taskAdapter), GsonUtils.getFile(this.getContext()));
    }



    public TontiApp adapterToTontiTask(TaskAdapter adapter){
        List<TontiTask> list=new ArrayList<>();

        for(int i=0;i<adapter.getCount();i++){
            list.add(adapter.getItem(i));
        }

        app.list.get(position).list=list;

        return app;
    }
}