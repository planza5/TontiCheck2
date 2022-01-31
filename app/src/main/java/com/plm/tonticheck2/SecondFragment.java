package com.plm.tonticheck2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.plm.tonticheck2.databinding.FragmentSecondBinding;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SecondFragment extends Fragment implements TaskListener{
    private FragmentSecondBinding binding;
    private static SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy HH:mm");

    //TODO pasar esta variable desde firstfragment de otra forma
    public static Integer position;
    public static TontiApp app;

    private ListView listView;
    private TaskAdapter taskAdapter;

    private AlarmUtils alarmUtils;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        if(position==null || app==null){
            throw new RuntimeException("Debes sumisistrar position y app");
        }

        alarmUtils=new AlarmUtils();

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
        setupDelAlarm();
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
        }else{
            String text=app.list.get(position).alarm;
            int idx=text.indexOf(" ");
            binding.alarmDateText.setText(text.substring(0,idx));
            binding.alarmTimeText.setText(text.substring(idx));
        }
    }

    private void setupDelAlarm() {
        if(app.list.get(position).alarm!=null) {

            binding.buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.list.get(position).alarm = null;
                    save();
                    binding.buttonCancelAlarm.setVisibility(View.INVISIBLE);
                    binding.alarmTimeText.setText("");
                    binding.alarmDateText.setText("");
                    alarmUtils.cancelAlarm(getContext(),app.list.get(position).id);
                }
            });
            binding.buttonCancelAlarm.setVisibility(View.VISIBLE);
        }else{
            binding.buttonCancelAlarm.setVisibility(View.INVISIBLE);
            binding.alarmTimeText.setText("");
            binding.alarmDateText.setText("");
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

                                alarmUtils.setAlarm(getContext(),calendar,app.list.get(position).id);


                                setupDelAlarm();
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