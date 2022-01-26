package com.plm.tonticheck2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TontiTaskList {
    public TontiTaskList(TontiApp app){
        id=new Random().nextInt();
    }

    public int id;
    public String name = "New Ckecklist...";
    public String alarm;
    public List<TontiTask> list=new ArrayList();
    public String toString(){
        return name;
    }
}
