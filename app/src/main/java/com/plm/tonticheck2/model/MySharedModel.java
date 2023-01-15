package com.plm.tonticheck2.model;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.plm.tonticheck2.GsonUtils;
import com.plm.tonticheck2.R;

public class MySharedModel extends ViewModel {
    private TontiApp app;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    
    public TontiApp getApp(Context ctx) throws Exception{
        if (app == null) {
            app=GsonUtils.loadApp(GsonUtils.getFile(ctx));
        }

        if(app==null){
            app=new TontiApp();
            boolean result=GsonUtils.saveApp(app, GsonUtils.getFile(ctx));

            if(result==false){
                throw new Exception("No fue posible cargar la app del modelo");
            }
        }


        return app;
    }

    public void setApp(TontiApp app) {
        this.app=app;
    }
}