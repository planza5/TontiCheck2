package com.plm.tonticheck2.model;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.plm.tonticheck2.GsonUtils;

public class MyViewModel extends ViewModel {
    private TontiApp app;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    
    public TontiApp getApp(Context ctx) {
        if (app == null) {
            app=GsonUtils.loadApp(GsonUtils.getFile(ctx));
        }
        
        return app;
    }

    public void setApp(TontiApp app) {
        this.app=app;
    }
}