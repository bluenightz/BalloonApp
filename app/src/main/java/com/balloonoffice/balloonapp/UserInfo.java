package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.Button;

/**
 * Created by bluenightz on 6/26/15 AD.
 */
public class UserInfo {
    public static String password;
    public static String username;
    public static String name;
    public static String branch;
    public static String branch_id;
    public static boolean success;
    public static String type;
    public static String status;


    public static void replaceInfo(MainActivity.User user){
        username = user.username;
        name = user.name;
        branch = user.branch;
        branch_id = user.branch_id;
        success = user.success;
        type = user.type;
        status = user.status;
    }

    public static void destroy(Activity c, Menu menu){
        final Activity activity = c;
        username = "";
        name = "";
        branch = "";
        branch_id = "";
        success = false;
        type = "";
        status = "";


        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences( activity );


        SharedPreferences.Editor editor = sh.edit();
        editor.putString("admin_username", "");
        editor.putString("admin_branch", "");
        editor.putString("admin_name", "");
        editor.putString("admin_branch_id", "");
        editor.putBoolean("admin_success", false);
        editor.putString("admin_type", "");
        editor.putString("admin_status", "");
        editor.commit();


        AlertDialog.Builder alertDialog = new AlertDialog.Builder( activity );
        alertDialog.setTitle("Log out")
                .setMessage("ออกจากระบบเรียบร้อยแล้ว")
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog.create().show();

        menu.findItem(R.id.login).setTitle(R.string.login);
        Button btn = (Button)c.findViewById(R.id.btn_login);
        btn.setText(R.string.login);

    }


}
