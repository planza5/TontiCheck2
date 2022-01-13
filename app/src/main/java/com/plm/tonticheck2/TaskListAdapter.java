package com.plm.tonticheck2;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.plm.tonticheck2.model.TontiTaskList;


public class TaskListAdapter extends ArrayAdapter<TontiTaskList> {
    private final Context context;
    private TaskListListener listener;
    private ImageButton white1=null;
    private ImageButton white2=null;

    public TaskListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context=context;
    }


    public void addListener(TaskListListener listener){
        this.listener=listener;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list, parent, false);
        }

        final ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.buttonRemoveTaskListItem);
        final ImageButton downButton = (ImageButton) convertView.findViewById(R.id.buttonTaskListDownItem);
        final ImageButton nextButton = (ImageButton) convertView.findViewById(R.id.buttonShowTasks);




        final ImageButton upButton = (ImageButton) convertView.findViewById(R.id.buttonTaskListUpItem);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p=1;
                if(getCount()<2){
                    return;
                }else if(position==0){
                    return;
                }else{
                    ListView listview=(ListView)parent;

                    TontiTaskList ttA=getItem(position-1);
                    TontiTaskList ttB=getItem(position);

                    remove(ttA);
                    remove(ttB);

                    insert(ttB,position-p);
                    insert(ttA,position);

                    listener.save();
                }
            }
        });

        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d("PABLO","down");
                }

                if(event.getAction()==MotionEvent.ACTION_UP){
                    Log.d("PABLO","up");
                }

                return true;
            }
        });

        upButton.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCount()<2){
                    return;
                }else if(position==getCount()-1){
                    return;
                }else{
                    ListView listview=(ListView)parent;

                    TontiTaskList ttListA=getItem(position);
                    TontiTaskList ttListB=getItem(position+1);

                    remove(ttListA);
                    remove(ttListB);

                    insert(ttListB,position);
                    insert(ttListA,position+1);


                    listener.save();
                }
            }
        });



        final EditText editbox = (EditText) convertView.findViewById(R.id.editTaskListItemName);


        editbox.setText(getItem(position).name);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TontiTaskList taskList=getItem(position);
                remove(taskList);
                listener.save();
            }
        });


        editbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getItem(position).name=v.getText().toString();
                    listener.save();
                }

                return false;
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.taskListSelected(position);
            }
        });

        return convertView;
    }




}
