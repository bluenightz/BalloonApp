package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import android.app.Fragment;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LoginDialog.LoginDialogInterface, PostTask.PostTaskInterface {
    private Activity c = this;
    private int framelayout;
    private Fragment f = null;
    private LoginDialog loginDialog = null;
    private AppControl AC = null;
    private PostTask<User> postTask;
    private Menu _menu;
    private ProgressBar progressBar;


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TextView t_username = (TextView) dialog.getDialog().findViewById(R.id.input_username);
        TextView t_password = (TextView) dialog.getDialog().findViewById(R.id.input_password);
        String username = t_username.getText().toString();
        String pass = t_password.getText().toString();
        UserInfo.username = username;
        UserInfo.password = pass;
        String str = String.format(APPCONFIG.LOGINPROCESS, username, pass);
//         String str = String.format(APPCONFIG.LOGINPROCESS + "?username=admin&password=2892", username, pass);
        if(Utilities.CHECK_INTERNET_CONN(c))

        {

        }

        else

        {
            Utilities.ToastAlert(c, ToastAlert.TYPE_INTERNET_CHECK);
        }

        postTask = new PostTask<User>(c);
        postTask.setType(new TypeToken<User>() {
        }.getType());

        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("username", UserInfo.username));
        params.add(new BasicNameValuePair("password", UserInfo.password));

        postTask.setParams(params);
        postTask.execute(str);
        postTask = null;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    public class User {

        /*
        {
            "status": "Has Password but Login False",
                    "name": "",
                    "branch": "",
                    "username": "",
                    "branch_id": "",
                    "type": "",
                    "success": "False"
        }
        */

        public String username; // admin_username
        public String branch; // admin_branch
        public String name; // admin_name
        public String branch_id; // admin_branch_id
        public boolean success; // admin_success
        public String type;
        public String status;
    }


    @Override
    public void onDismissDialog(DialogFragment dialog) {
        destroyLogin();
    }


    @Override
    public <User> void onJSONComplete(User object) {
        if (object == null) {
            Utilities.ToastAlert(c, ToastAlert.LOGIN_ERROR_USER_NOT_MATCH);
        } else {
            MainActivity.User usernameobj = (MainActivity.User) object;
            AlertDialog.Builder abox = new AlertDialog.Builder(c);
            abox.setTitle(R.string.login_title_en)
                    .setNeutralButton(R.string.natural_button_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            if (usernameobj.success) {
                abox.setMessage(R.string.login_success);
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(c);
                SharedPreferences.Editor edit = sh.edit();
                edit.putString("admin_username", usernameobj.username);
                edit.putString("admin_branch", usernameobj.branch);
                edit.putString("admin_name", usernameobj.name);
                edit.putString("admin_branch_id", usernameobj.branch_id);
                edit.putBoolean("admin_success", usernameobj.success);
                edit.putString("admin_type", usernameobj.type);
                edit.putString("admin_status", usernameobj.status);

                UserInfo.replaceInfo(usernameobj);
                edit.commit();

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.list_view_container);


                if( viewGroup.getChildCount() > 0 ){
                    toggleUserInfo();
                    toggleUserInfo();
                }else{
                    toggleUserInfo();
                }

                _menu.findItem(R.id.login).setTitle("Log out");
                Button btn = (Button) findViewById(R.id.btn_login);
                btn.setText(R.string.logout);
            } else {
                abox.setMessage(R.string.login_fail);
            }
            abox.create().show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);


        setContentView(R.layout.activity_main);
        // setTitle("");

        progressBar = (ProgressBar) findViewById(R.id.progressBar_update);

        if( this.getIntent().getBooleanExtra("showProgess", false) ){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
            NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
            manager.cancel(0);
        }

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(c);

        if (sh.getBoolean("admin_success", false)) {
            User latestuser = new User();
            latestuser.success = sh.getBoolean("admin_success", true);
            latestuser.branch = sh.getString("admin_branch", "");
            latestuser.branch_id = sh.getString("admin_branch_id", "");
            latestuser.name = sh.getString("admin_name", "");
            latestuser.username = sh.getString("admin_username", "");
            latestuser.status = sh.getString("admin_status", "");
            latestuser.type = sh.getString("admin_type", "");
            UserInfo.replaceInfo(latestuser);
            Button btn = (Button) findViewById(R.id.btn_login);
            btn.setText(R.string.logout);
        }else{
            Button btn = (Button) findViewById(R.id.btn_login);
            btn.setText(R.string.login);
        }
//
//
        SharedPreferences.Editor edit = sh.edit();
        edit.putString("app_version", APPCONFIG.APPVERSION);
        edit.commit();
//
        AC = new AppControl();
        AC.setActivity(c);
        AC.setOnCheckComplete(new AppControl.AppControlInterface() {
            @Override
            public void onCheckComplete(AppControl.AppDetail jsonobject, boolean skip) {
                String ServerVersion = jsonobject.version.replaceAll("(!.)\\D", "");
                String CurrentVersion = APPCONFIG.APPVERSION.replaceAll("(!.)\\D", "");
                NumberFormat nf = new DecimalFormat("990.0");
                double ServerVersion_d;
                double CurrentVersion_d;
                try {
                    ServerVersion_d = (double) nf.parse(ServerVersion).doubleValue();
                    CurrentVersion_d = (double) nf.parse(CurrentVersion).doubleValue();


                    if (CurrentVersion_d < ServerVersion_d) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                        alertDialog.setTitle(R.string.app_name)
                                .setMessage(R.string.update_info)
                                .setPositiveButton("อัพเดท", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateApp();
                                    }
                                })
                                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                        alertDialog.create().show();
                    } else {
                        if (!skip) { // เลือก skip = true จะข้ามตรงนี้
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                            alertDialog.setTitle(R.string.app_name)
                                    .setMessage(R.string.update_already)
                                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            alertDialog.create().show();
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();


                }

            }
        });
        AC.checkUpdate(true);


    }


    private void destroyLogin() {
        loginDialog = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        _menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (UserInfo.success) {
            menu.findItem(R.id.login).setTitle(R.string.logout);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //R.style.Widget_AppCompat_ActionBar


        //noinspection SimplifiableIfStatement
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if (id == R.id.action_settings) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                Intent i = new Intent(c, SettingsActivity.class);
                startActivity(i);
            } else {

                toggleUserInfo();

            }


            return true;
        } else if (id == R.id.update) {
            // Utilities.UPDATE_VIA_LINK(c);
            AC.checkUpdate(false);

            return true;
        } else if (id == R.id.login) {
            logincheck();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleUserInfo() {
        if (f == null) {
            f = new SettingsFragment();

            framelayout = findViewById(R.id.list_view_container).getId();

            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(framelayout, f)
                    .addToBackStack(null)
                    .commit();
        } else {
            closeFragment();
        }
    }

    private void logincheck(){
        if(Utilities.CHECK_INTERNET_CONN(c))

        {
            if (UserInfo.success) {
                UserInfo.destroy(c, _menu);
                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.list_view_container);
                if( viewGroup.getChildCount() > 0 ){
                    closeFragment();
                }
            } else {
                openlogin();
            }
        }

        else

        {
            Utilities.ToastAlert(c, ToastAlert.TYPE_INTERNET_CHECK);
        }

    }

    private void openlogin(){
        if( loginDialog == null ) {
            loginDialog = new LoginDialog();
            loginDialog.show(getFragmentManager(), "Login");
        }
    }

    private void updateApp(){
        ProgressBar PGupdate = (ProgressBar) findViewById(R.id.progressBar_update);

        Utilities.UpdateApp a = new Utilities.UpdateApp();
        a.setContext(getApplicationContext());
        a.setPG(PGupdate);
        a.execute(APPCONFIG.INSTALL_PATH);

        PGupdate.setVisibility(View.VISIBLE);



        final NotificationManager manager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
        builder.setSmallIcon(R.drawable.ic_get_app_white_24dp);
        builder.setContentTitle(getResources().getText(R.string.downloading));
        builder.setContentText(getResources().getText(R.string.downloading_desc));
        final Intent intent = new Intent(c, MainActivity.class);
        intent.putExtra("showProgess", true);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(c);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent resultPending = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPending);

        manager.notify(0, builder.build());


//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        int incr;
//                        // Do the "lengthy" operation 20 times
//                        for (incr = 0; incr <= 100; incr+=5) {
//                            // Sets the progress indicator to a max value, the
//                            // current completion percentage, and "determinate"
//                            // state
//                            builder.setProgress(100, incr, false);
//                            // Displays the progress bar for the first time.
//                            manager.notify(0, builder.build());
//                            // Sleeps the thread, simulating an operation
//                            // that takes time
//                            try {
//                                // Sleep for 5 seconds
//                                Thread.sleep(1*1000);
//                            } catch (InterruptedException e) {
//
//                            }
//                        }
//                        // When the loop is finished, updates the notification
//
//                        intent.putExtra("showProgess", false);
//                        builder.setContentText("Download complete")
//                                // Removes the progress bar
//                                .setProgress(0,0,false);
//                        manager.notify(0, builder.build());
//                    }
//                }
//// Starts the thread by calling the run() method in its Runnable
//        ).start();


    }

    private void closeFragment(){
        FragmentTransaction FT = getFragmentManager().beginTransaction();
        if( f != null ){
            FT.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            FT.remove(f);
            FT.commit();
            FT = null;
            f = null;
        }
    }

    public void gotoPage(View view) {

        if( UserInfo.success ){
            closeFragment();
            Intent intent = new Intent(this, ProductList.class);
            startActivity(intent);
        }else{
            Utilities.ToastAlert(c, ToastAlert.LOGIN_BEFORE_USE);
        }
    }

    public void update(View view){
        AC.checkUpdate(false);
    }

    public void login(View view){
        logincheck();
    }

    public void gotoproductcategory(View view){
        Intent i = new Intent(c, ProductCategory.class);
        startActivity(i);
    }

}
