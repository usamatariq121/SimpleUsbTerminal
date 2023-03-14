package com.linkitsoft.beepvending.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocalDataManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");

    private static final String PREF_NAME = "BeepVendingData";

    private static LocalDataManager instance = null;

    public LocalDataManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static LocalDataManager createInstance(Context context) {
        if (instance == null) {
            instance = new LocalDataManager(context);
        }
        return instance;
    }

    public static LocalDataManager getInstance() {
        return instance;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return pref.getString(key, null);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key) {
        return pref.getFloat(key, 0);
    }

    public void putDouble(String key, double value) {
        editor.putFloat(key, Float.valueOf(String.valueOf(value)));
        editor.commit();
    }

    public double getDouble(String key) {
        return pref.getFloat(key, 0);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public Long getLong(String key) {
        return pref.getLong(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public void putBytes(String key, byte[] value) {
        editor.putString(key, Base64.encodeToString(value, Base64.DEFAULT));
        editor.commit();
    }

    public byte[] getBytes(String key) {
        byte[] bytes = null;
        String str = pref.getString(key, null);
        if (str != null) {
            bytes = Base64.decode(str, Base64.DEFAULT);
        }
        return bytes;
    }

    public void putDate(String key, Date value) {
        editor.putString(key, sdf.format(value));
        editor.commit();
    }

    public Date getDate(String key) {
        Date date = null;
        String strDate = pref.getString(key, null);
        if (strDate != null) {
            try {
                date = sdf.parse(strDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }


    public <T> void putData(String key, T data) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        editor.putString(key, gson.toJson(data));
        editor.commit();
    }

    public <T> T getData(String key, Type type) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(pref.getString(key, "{}"), type);
    }

    public <T> void putList(String key, List<T> list) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        editor.putString(key, gson.toJson(list));
        editor.commit();
    }

    public <T> List<T> getList(String key, Type type) {
        Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(pref.getString(key, "[]"), type);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}


