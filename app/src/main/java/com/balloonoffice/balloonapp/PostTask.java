package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by bluenightz on 6/26/15 AD.
 */
public class PostTask<E> extends AsyncTask<String, Void, String> {

    private Class classtemplate;
    private Activity c;
    private PostTaskInterface mListener;
    private Type t = null;
    private List<NameValuePair> params = null;
    private E jsonObj;
    // private Object code_json_object;

    public interface PostTaskInterface{
        public <T> void onJSONComplete(T object);
    }

    public PostTask(Activity activity){
        c = activity;
        mListener = (PostTaskInterface) c;
    }

    public void setType(Type _t){
        t = _t;
    }
    public void setParams(List<NameValuePair> _list){
        params = _list;
    }

    @Override
    protected String doInBackground(String... urls) {

        try{
//            List<NameValuePair> params = new ArrayList<NameValuePair>();

//            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//            params.add(new BasicNameValuePair("username", UserInfo.username));
//            params.add(new BasicNameValuePair("password", UserInfo.password));

            return Utilities.getJSONString(urls[0], params);
        }catch(Exception e){
            return "Unable to retrieve web page. URL my be invalid.";
        }
    }


    @Override
    protected void onPostExecute(String s) {
        if( t != null ) {
            String str = s.trim();
            str = str.replaceAll("(\r\n|\n|\t)", "");

            // Type t = new TypeToken<AppDetail>(){}.getType();
            JsonReader reader = new JsonReader(new StringReader(str));
            reader.setLenient(true);


            // code_json_object = null;

            try {
                jsonObj = new Gson().fromJson(str, t);
                mListener.onJSONComplete(jsonObj);
            } catch (JsonSyntaxException e){
                Utilities.ToastAlert(c, ToastAlert.TYPE_JSON_ERROR);
            } catch (Exception e) {
                Utilities.ToastAlert(c, ToastAlert.TYPE_DATA_FROM_SERVER_ERROR);
            }
        }else{
            mListener.onJSONComplete(null);
        }

    }
}