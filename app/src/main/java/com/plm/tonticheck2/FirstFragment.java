package com.plm.tonticheck2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.plm.tonticheck2.databinding.FragmentFirstBinding;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTaskList;

import java.io.File;

public class FirstFragment extends Fragment implements TaskListListener {

    private FragmentFirstBinding binding;
    private ListView listView;
    private TaskListAdapter taskListAdapter;
    private TontiApp tontiApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.tontitasklistview);
        tontiApp= GsonUtils.loadApp(GsonUtils.getFile(this.getContext()));

        if (tontiApp == null) {
            tontiApp = new TontiApp();
        }



        taskListAdapter=new TaskListAdapter(this.getContext(),R.layout.task_list);


        for(TontiTaskList tasklist:tontiApp.list){
            taskListAdapter.add(tasklist);
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
        boolean result = GsonUtils.saveApp(adapterToTontiTask(taskListAdapter), GsonUtils.getFile(this.getContext()));
    }

    @Override
    public void taskListSelected(int position) {
        SecondFragment.position=position;
        SecondFragment.app=tontiApp;

        NavController nhf = NavHostFragment.findNavController(FirstFragment.this);

        FirstFragment.this.setArguments(new Bundle());
        nhf.navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    public TontiApp adapterToTontiTask(TaskListAdapter adapter){
        tontiApp.list.clear();

        for(int i=0;i<adapter.getCount();i++){
            tontiApp.list.add(adapter.getItem(i));
        }

        return tontiApp;
    }


}