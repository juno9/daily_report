package com.example.myapplication;


import static android.content.Context.MODE_PRIVATE;
import static android.media.MediaCodec.MetricsConstants.MODE;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper
{

    private SharedPreferences app_prefs;
    private Context context;

    public PreferenceHelper(Context context)

    {
        this.context = context;
        app_prefs = context.getSharedPreferences("shared", 0);

    }

    public void setUser_email(String user_email) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("user_email", user_email);
        edit.apply();

    }

    public String getUser_email() {
        String user_email=app_prefs.getString("user_email","");
        return user_email;
    }

    public void setLogin(boolean login) {

        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean("login",true);
        edit.apply();

    }

    public boolean getLogin() {
        boolean login=app_prefs.getBoolean("login",true);
        return login;
    }
}


