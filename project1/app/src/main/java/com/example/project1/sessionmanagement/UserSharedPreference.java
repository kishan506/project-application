package com.example.project1.sessionmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.project1.activites.LoginActivity;
import com.example.project1.activites.PhoneValidationActivity;
import com.example.project1.activites.ResetPasswordActivity;

public class UserSharedPreference {
    Context context;
    String IP;
    public UserSharedPreference(LoginActivity context)
    {
        this.context = context;
    }
    public UserSharedPreference(ResetPasswordActivity context)
    {
        this.context = context;
    }
    public UserSharedPreference(PhoneValidationActivity context)
    {
        this.context = context;
    }

    public void addUserDetails(int user_id) {
        Log.d("from pref",user_id+"");
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putInt("userId",user_id);
        myEdit.commit();
    }
    public void addIP(String IP) {
        Log.d("from pref",IP+"");
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("useIP",IP);
        myEdit.commit();
    }

    public int getUserDetails() {
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        return sh.getInt("userId",0);
    }public String getIP() {
        SharedPreferences sh = context.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        Log.d("dd", "getIP: "+sh.getAll());
        Log.d("gg", "getIP: "+sh.getString("useIP",""));
//        Map<String, ?> map = new HashMap<String,String>();
//        map = sh.getAll();
        String z =sh.getString("useIP","");
//        Log.d("ff", "addIP: "+map.get("userIP"));

        return z;

    }
}
