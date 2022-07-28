package com.plm.tonticheck2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.plm.tonticheck2.databinding.FragmentFirstBinding;
import com.plm.tonticheck2.model.MySharedModel;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTaskList;

public class FirstFragment extends Fragment implements TaskListListener {
    private FragmentFirstBinding binding;
    private ListView listView;
    private TaskListAdapter taskListAdapter;
    MySharedModel model=null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        model=new ViewModelProvider(requireActivity()).get(MySharedModel.class);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.tontitasklistview);


        taskListAdapter=new TaskListAdapter(this.getContext(),R.layout.task_list);


        //Añadimos tassklist y borramos alarmas pasadas
        boolean thereIsAlarmsInThePast=false;

        for(TontiTaskList tasklist:model.getApp(getContext()).list){
            taskListAdapter.add(tasklist);

            if(tasklist.alarm!=null && AlarmUtils.isInThePast(tasklist.alarm)){
                tasklist.alarm=null;
                thereIsAlarmsInThePast=true;
            }
        }

        if(thereIsAlarmsInThePast){
            save();
        }

        taskListAdapter.addListener(this);
        listView.setAdapter(taskListAdapter);


        binding.buttonTaskListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskListAdapter.add(new TontiTaskList());
                save();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void save() {
        taskListAdapter.notifyDataSetChanged();
        listView.invalidate();
        TontiApp app=adapterToTontiTask(taskListAdapter);

        boolean result = GsonUtils.saveApp(app, GsonUtils.getFile(this.getContext()));
        model.setApp(app);
    }


    @Override
    public void taskListSelected(int position) {
        model.setPosition(position);

        NavController nhf = NavHostFragment.findNavController(FirstFragment.this);

        FirstFragment.this.setArguments(new Bundle());
        nhf.navigate(R.id.action_FirstFragment_to_SecondFragment);
    }


    public TontiApp adapterToTontiTask(TaskListAdapter adapter){
        TontiApp tontiApp=new TontiApp();

        for(int i=0;i<adapter.getCount();i++){
            tontiApp.list.add(adapter.getItem(i));
        }

        return tontiApp;
    }

}