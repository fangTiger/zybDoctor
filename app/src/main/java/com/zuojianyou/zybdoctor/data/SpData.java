package com.zuojianyou.zybdoctor.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.zuojianyou.zybdoctor.application.MyApplication;

public class SpData {

    private static SharedPreferences sp;

    private static SharedPreferences get() {
        if (sp == null)
            sp = MyApplication.getAppContext().getSharedPreferences("SpData", Context.MODE_PRIVATE);
        return sp;
    }

    public static void setUser(String user) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_user", user);
        editor.commit();
    }

    public static void clearUser() {
        SharedPreferences.Editor editor = get().edit();
        editor.remove("sp_user");
        editor.commit();
    }

    public static String getUser() {
        String user = get().getString("sp_user", null);
        return user;
    }

    public static void setPwd(String pwd) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_pwd", pwd);
        editor.commit();
    }

    public static void clearPwd() {
        SharedPreferences.Editor editor = get().edit();
        editor.remove("sp_pwd");
        editor.commit();
    }

    public static String getPwd() {
        String pwd = get().getString("sp_pwd", null);
        return pwd;
    }

    public static void setToken(String token) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_token", token);
        editor.commit();
    }

    public static String getToken() {
        String token = get().getString("sp_token", "123456789");
        return token;
    }

    public static void setPersonId(String id) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_person_id", id);
        editor.commit();
    }

    public static void clearPersonId() {
        SharedPreferences.Editor editor = get().edit();
        editor.remove("sp_person_id");
        editor.commit();
    }

    public static String getPersonId() {
        String user = get().getString("sp_person_id", null);
        return user;
    }

    public static void setMbrId(String id) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_mbr_id", id);
        editor.commit();
    }

    public static void clearMbrId() {
        SharedPreferences.Editor editor = get().edit();
        editor.remove("sp_mbr_id");
        editor.commit();
    }

    public static String getMbrId() {
        String user = get().getString("sp_mbr_id", null);
        return user;
    }

    public static void setAuthFlag(String flag) {
        SharedPreferences.Editor editor = get().edit();
        editor.putString("sp_auth_flag", flag);
        editor.commit();
    }

    public static void clearAuthFlag() {
        SharedPreferences.Editor editor = get().edit();
        editor.remove("sp_auth_flag");
        editor.commit();
    }

    public static String getAuthFlag() {
        String user = get().getString("sp_auth_flag", null);
        return user;
    }

}
