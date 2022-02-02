package com.plm.tonticheck2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TontiTaskList {
    public TontiTaskList(){
        id=new Random().nextInt();
    }

    public int id;
    public String name = "New Ckecklist...";
    public String alarm;
    public List<TontiTask> list=new ArrayList();
    public String toString(){
        return name;
    }

    public TontiTaskList getClone() {
        TontiTaskList ttl=new TontiTaskList();
        ttl.id=id;
        ttl.name=name;
        ttl.alarm=alarm;

        ttl.list=new ArrayList();

        for(TontiTask tt:this.list){
            ttl.list.add(tt);
        }



        return ttl;
    }
}
