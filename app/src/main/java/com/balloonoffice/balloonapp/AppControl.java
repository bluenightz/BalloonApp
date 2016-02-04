package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluenightz on 6/19/15 AD.
 */

public class AppControl {
    private Activity a;
    private AppControlInterface mListener;
    private boolean skip;

    public interface AppControlInterface{
        public void onCheckComplete(AppDetail code_json_object, boolean skip);
    }

    public void setOnCheckComplete(AppControlInterface listener){
        mListener = listener;
    }

    public void setActivity(Activity _a){
        a = _a;
    }

    public void checkUpdate(boolean _skip){
        skip = _skip;
        String stringUrl = APPCONFIG.CHECKVERSION_PATH;
        if ( Utilities.CHECK_INTERNET_CONN(a) ) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Utilities.ToastAlert(a, ToastAlert.TYPE_INTERNET_CHECK);
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                return Utilities.getJSONString(urls[0], params);
            }catch(Exception e){
                return "Unable to retrieve web page. URL my be invalid.";
            }
        }


        @Override
        protected void onPostExecute(String s) {

            String str = s.trim();
            str = str.replaceAll("(\r\n|\n|\t)", "");

            Type t = new TypeToken<AppDetail>(){}.getType();
            JsonReader reader =new JsonReader(new StringReader( str ));
            reader.setLenient(true);

            AppDetail code_json_object = null;

            try {
                code_json_object = new Gson().fromJson(str, t);
                mListener.onCheckComplete(code_json_object, skip);
            } catch (Exception e) {

                //Toast.makeText(c,(CharSequence) getString(R.string.productlist_notload), Toast.LENGTH_SHORT ).show();
            }


        }

    }

    public class AppDetail{
        public String version;
        public String releaseDate;
    }
}
