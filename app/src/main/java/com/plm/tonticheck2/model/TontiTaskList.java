package com.plm.tonticheck2.model;

import java.util.ArrayList;
import java.util.List;

public class TontiTaskList {
    public String name = "New Ckecklist...";
    public String alarm;
    public List<TontiTask> list=new ArrayList();
    public String toString(){
        return name;
    }
}
