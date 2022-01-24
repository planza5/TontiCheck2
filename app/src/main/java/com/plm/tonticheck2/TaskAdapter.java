package com.plm.tonticheck2;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.plm.tonticheck2.model.TontiTask;


public class TaskAdapter extends ArrayAdapter<TontiTask> {
    private final Context context;
    private TaskListener listener;


    public TaskAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context=context;
    }


    public void addListener(TaskListener listener){
        this.listener=listener;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task, parent, false);
        }

        setupCheckButton(position, convertView);
        setupUpButton(position, convertView, (ListView) parent);
        setupDownButton(position, convertView, (ListView) parent);
        setupRemoveButton(position, convertView);
        setupEditbox(position, convertView);
        setupLongTap(position,convertView);

        return convertView;
    }

    private void setupLongTap(int position, View convertView) {
        final ImageButton upButton = (ImageButton) convertView.findViewById(R.id.buttonTaskUpItem);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMenu(position,v);
            }
        });
    }

    private void displayMenu(int position, View v) {
        PopupMenu pmenu=new PopupMenu(this.getContext(),v);
        pmenu.inflate(R.menu.menu_popup_task);
        pmenu.show();
    }

    private void setupCheckButton(int position, View convertView) {
        final CheckBox cbTask=(CheckBox) convertView.findViewById(R.id.cbTask);
        cbTask.setChecked(getItem(position).done);

        cbTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(position).done=cbTask.isChecked();
                listener.save();
            }
        });
    }

    private void setupUpButton(int position, View convertView, ListView parent) {
        final ImageButton upButton = (ImageButton) convertView.findViewById(R.id.buttonTaskUpItem);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCount()<2){
                    return;
                }else if(position ==0){
                    return;
                }else{
                    ListView listview= parent;

                    TontiTask ttA=getItem(position -1);
                    TontiTask ttB=getItem(position);

                    remove(ttA);
                    remove(ttB);

                    insert(ttB, position -1);
                    insert(ttA, position);

                    listener.save();
                }
            }
        });
    }

    private void setupDownButton(int position, View convertView, ListView parent) {
        final ImageButton downButton = (ImageButton) convertView.findViewById(R.id.buttonTaskDownItem);

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCount()<2){
                    return;
                }else if(position ==getCount()-1){
                    return;
                }else{
                    ListView listview= parent;

                    TontiTask ttListA=getItem(position);
                    TontiTask ttListB=getItem(position +1);

                    remove(ttListA);
                    remove(ttListB);

                    insert(ttListB, position);
                    insert(ttListA, position +1);

                    listener.save();
                }
            }
        });
    }

    private void setupRemoveButton(int position, View convertView) {
        final ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.buttonRemoveTaskItem);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TontiTask task=getItem(position);
                remove(task);
                listener.save();
            }
        });
    }

    private void setupEditbox(int position, View convertView) {
        final EditText editbox = (EditText) convertView.findViewById(R.id.editTaskItemName);
        editbox.setText(getItem(position).name);
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
    }


}
