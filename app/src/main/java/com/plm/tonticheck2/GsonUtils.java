package com.plm.tonticheck2;

import static com.plm.tonticheck2.Ctes.TAG;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.gson.Gson;
import com.plm.tonticheck2.model.TontiApp;
import com.plm.tonticheck2.model.TontiTaskList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class GsonUtils {
    private static Gson gson = new Gson();

    public static TontiApp loadApp(Context ctx) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(GsonUtils.getFile(ctx))));
            return gson.fromJson(reader, TontiApp.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static boolean saveApp(Context ctx, TontiApp app) {
        try {
            Log.d(TAG,"Saving app");
            OutputStreamWriter owriter = new OutputStreamWriter(new FileOutputStream(GsonUtils.getFile(ctx)));
            BufferedWriter writer = new BufferedWriter(owriter);
            String json = gson.toJson(app);
            writer.write(json);
            writer.flush();
            owriter.flush();
            writer.close();
            owriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void deleteFile(Context ctx){
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.json_file));
        file.delete();
    }

    public static File getFile(Context ctx) {
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.json_file));
        return file;
    }

    public static TontiTaskList getTontiTaskListById(Context ctx,int id) {
        TontiApp app=loadApp(ctx);

        for(TontiTaskList ttl:app.list){
            if(ttl.id==id)
                return ttl;
        }

        return null;
    }

    public static byte[] readUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor pdf = context.getContentResolver().openFileDescriptor(uri, "r");

        assert pdf != null;
        assert pdf.getStatSize() <= Integer.MAX_VALUE;
        byte[] data = new byte[(int) pdf.getStatSize()];

        FileDescriptor fd = pdf.getFileDescriptor();
        FileInputStream fileStream = new FileInputStream(fd);
        fileStream.read(data);

        return data;
    }

    public static void saveAppToUri(Context ctx, Uri uri) throws IOException{
        TontiApp app=loadApp(ctx);

        OutputStreamWriter os_writer = new OutputStreamWriter(ctx.getContentResolver().openOutputStream(uri));
        BufferedWriter writer = new BufferedWriter(os_writer);
        String json = gson.toJson(app);
        writer.write(json);
        writer.flush();
        os_writer.flush();
        writer.close();
        os_writer.close();
    }

    public static TontiApp loadAppFromUri(Context ctx, Uri uri) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(ctx.getContentResolver().openInputStream(uri)));
        return gson.fromJson(reader, TontiApp.class);
    }
}
