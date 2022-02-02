package com.plm.tonticheck2.model;

public class TontiTask {
    public String name = "New Check...";
    public boolean done = false;
    public String toString(){
        return name;
    }

    public TontiTask getClone() {
        TontiTask tt=new TontiTask();
        tt.name=name;
        tt.done=done;
        return tt;
    }
}
