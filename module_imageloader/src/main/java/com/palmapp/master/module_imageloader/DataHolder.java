package com.palmapp.master.module_imageloader;

import android.net.Uri;

import java.io.File;

/**
 * @author :     xiemingrui
 * @since :      2019/8/23
 */
public class DataHolder {
    public String string;
    public int id;
    public Uri uri;
    public File file;

    public static DataHolder create(String string){
        DataHolder holder = new DataHolder();
        holder.string = string;
        return holder;
    }

    public static DataHolder create(int id){
        DataHolder holder = new DataHolder();
        holder.id = id;
        return holder;
    }

    public static DataHolder create(Uri uri){
        DataHolder holder = new DataHolder();
        holder.uri = uri;
        return holder;
    }
    public static DataHolder create(File file){
        DataHolder holder = new DataHolder();
        holder.file = file;
        return holder;
    }
}
