package com.plm.tonticheck2;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.plm.tonticheck2.model.TontiTask;
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

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list, parent, false);
        }

        setupEditbox(position,convertView);
        setupPopupMenu(position,convertView);

        final ImageButton nextButton = (ImageButton) convertView.findViewById(R.id.buttonShowTasks);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.taskListSelected(position);
            }
        });

        return convertView;
    }

    private void setupPopupMenu(int position, View convertView) {
        final ImageButton menuButton = (ImageButton) convertView.findViewById(R.id.taskListMenuItem);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMenu(position,v);
            }
        });
    }

    private void displayMenu(int position, View menuButtonView) {
        PopupMenu popupmenu=new PopupMenu(this.getContext(),menuButtonView);
        popupmenu.inflate(R.menu.menu_popup_task);
        popupmenu.show();

        if(position==0){
            popupmenu.getMenu().findItem(R.id.item_up).setVisible(false);
        }else if(position==getCount()-1){
            popupmenu.getMenu().findItem(R.id.item_down).setVisible(false);
        }


        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.item_up){
                    moveTask(position,  0,  true);
                }else if(item.getItemId()==R.id.item_down){
                    moveTask(position,  getCount()-1,  false);
                }else if(item.getItemId()==R.id.delete_item){
                    TontiTaskList task=getItem(position);
                    remove(task);
                    listener.save();
                }

                return false;
            }
        });
    }

    private void moveTask(int position, int limit, boolean up) {
        int i1,i2;

        if(up){
            i1=position-1;
            i2=position;
        }else{
            i1=position;
            i2=position+1;
        }

        if (getCount() < 2) {
            return;
        } else if (position == limit) {
            return;
        } else {
            TontiTaskList ttA = getItem(i1);
            TontiTaskList ttB = getItem(i2);

            remove(ttA);
            remove(ttB);

            insert(ttB, i1);
            insert(ttA, i2);

            listener.save();
        }
    }

    public void addListener(TaskListListener listener){
        this.listener=listener;
    }




    private void setupEditbox(int position, View convertView) {
        final EditText editbox = (EditText) convertView.findViewById(R.id.editTaskListItemName);
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
