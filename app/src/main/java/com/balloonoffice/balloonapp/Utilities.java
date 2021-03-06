package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.balloonoffice.balloonapp.Model.CodeObj;
import com.balloonoffice.balloonapp.Model.Csv_item_model;
import com.google.gson.Gson;
import com.opencsv.CSVReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bluenightz on 6/9/15 AD.
 */
public class Utilities {

    public static CodeObj<CodeObj.Code> CodeObj;

    public static boolean CHECK_INTERNET_CONN(Context C){
        ConnectivityManager connMgr = (ConnectivityManager) C.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());

    }

    public static void UPDATE_VIA_LINK(Context C){
        if ( Utilities.CHECK_INTERNET_CONN(C) ) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APPCONFIG.INSTALL_PATH));
            C.startActivity(browserIntent);
        } else {
            ToastAlert(C, ToastAlert.TYPE_INTERNET_CHECK);
        }
    }

    public static void ToastAlert(Context C, String type){
        int str;

        switch(type){
            case ToastAlert.TYPE_INTERNET_CHECK:
                str = R.string.checkNetwork;
                break;
            case ToastAlert.TYPE_DATA_FROM_SERVER_ERROR:
                str = R.string.productlist_notload;
                break;
            case ToastAlert.TYPE_JSON_ERROR:
                str = R.string.json_error;
                break;
            case ToastAlert.LOGIN_ERROR_USER_NOT_MATCH:
                str = R.string.login_error_user_not_match;
                break;
            case ToastAlert.LOGIN_BEFORE_USE:
                str = R.string.login_before_use;
                break;
            case ToastAlert.TYPE_DATA_COMPLETE:
                str = R.string.type_data_complete;
                break;
            default:
                str = R.string.toast_error;
                break;
        }


        Toast.makeText(C, str, Toast.LENGTH_SHORT ).show();
    }


    public static ArrayList<CodeObj.Code> ReadCSVFile( String path ){
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"

        ArrayList<CodeObj.Code> Csv_list = new ArrayList<CodeObj.Code>();
        CodeObj<CodeObj.Code> CodeObj = new CodeObj<CodeObj.Code>();

        Boolean keepData = false;

        String[] a = path.split("/");
        String dir = "";
        String filename = "";
        for( int i = 0 ; i < a.length  ; ++i ){
            if( i == a.length - 1 ) {
                filename = a[i];
                break;
            }
            dir += a[i];
            dir += "/";
        }

        // File sdcard = Environment.getExternalStorageDirectory();
        File sdcard = new File(dir);

        //Get the text file
        File file = new File(sdcard, filename);

        //Read text from file
        //StringBuilder text = new StringBuilder();

        String text = "";
        int i = 0;

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader( file ));
            String [] nextLine;
            try {
                while ((nextLine = reader.readNext()) != null) {
                    // nextLine[] is an array of values from the line
                    System.out.println(nextLine[0] + nextLine[1] + "etc...");
                    if( keepData ){
//                        Csv_item_model csv_item_model = new Csv_item_model();
//
//                        csv_item_model.setStoreCode(nextLine[1]);
//                        csv_item_model.setProductCode(nextLine[2]);
//                        csv_item_model.setStockBefore(nextLine[7]);
//                        csv_item_model.setStockIn(nextLine[10]);
//                        csv_item_model.setStockOut( nextLine[13] );
//                        csv_item_model.setStockAfter(nextLine[16]);

//                        Csv_list.add( csv_item_model );

                        CodeObj.Code csv_item_model = new CodeObj().new Code();
                        csv_item_model.code = nextLine[2];
                        csv_item_model.quantity = nextLine[16];
                        csv_item_model.src = "http://dev.balloonoffice.com/images/Materials/1419490398.gif";
                        csv_item_model.title = "30F Multi-Panel Heart";

                        CodeObj.Time time = new CodeObj().new Time();
                        time.date = "07/02/2016";
                        time.time = "12:06";
                        time.quantity = "-1";

                        csv_item_model.checkschedule = new CodeObj.Time[1];
                        csv_item_model.checkschedule[0] = time;

                        Csv_list.add( csv_item_model );



                        ++i;
                    }
                    if( nextLine[0].toString().equalsIgnoreCase("no") ){
                        //
                        keepData = true;
                    }
                }

                CodeObj codeObj = new CodeObj();
                codeObj.codes = new CodeObj.Code[Csv_list.size()];

                for(int z = 0 ; z < Csv_list.size(); ++z){
                    CodeObj.Code c = new CodeObj().new Code();
                    c.code = Csv_list.get(z).code;
                    c.quantity = Csv_list.get(z).quantity;
                    c.src = "http://dev.balloonoffice.com/images/Materials/1419490398.gif";
                    c.title = "30F Multi-Panel Heart";

                    CodeObj.Time time = new CodeObj().new Time();
                    time.date = "07/02/2016";
                    time.time = "12:06";
                    time.quantity = "-1";

                    codeObj.codes[z] = c;

                    c.checkschedule = new CodeObj.Time[1];
                    c.checkschedule[0] = time;
                }

                Utilities.CodeObj = codeObj;
                codeObj.addtofirst();
                Csv_list.get(0).code = "8858667045108";

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            Log.e("ReadFile", e.getMessage());
        }
        */


        return Csv_list;
    }


    public static String ReadFile( String path ){
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"

                String[] a = path.split("/");
                String dir = "";
                String filename = "";
                for( int i = 0 ; i < a.length  ; ++i ){
                    if( i == a.length - 1 ) {
                        filename = a[i];
                        break;
                    }
                    dir += a[i];
                    dir += "/";
                }

                // File sdcard = Environment.getExternalStorageDirectory();
                File sdcard = new File(dir);

        //Get the text file
                File file = new File(sdcard, filename);

        //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                }
                catch (IOException e) {
                    //You'll need to add proper error handling here
                    Log.e("ReadFile", e.getMessage());
                }



        return text.toString();
    }

    public static <Code> void saveCountlist(Activity c, Code obj){
        Gson gson = new Gson();
        String savestr = gson.toJson(obj);

        SharedPreferences sharedPreferences = c.getSharedPreferences(APPCONFIG.PREFERENCE_FILENAME, c.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("content", savestr);
        editor.commit();
    }

    public static String getJSONString(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpURLConnection httpURLConnection;
        OutputStreamWriter request = null;
        String _url = null;
        String _response = null;
        _url = url;
        String parameters = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(_url);

            try {
                // Add your data
                httppost.setEntity(new UrlEncodedFormEntity(params));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader_buffer = new BufferedReader
                        (new InputStreamReader(content));

                String line;
                while ((line = reader_buffer.readLine()) != null) {
                    str.append(line);
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }catch(Exception e){

        }
//
//        try {
//            URL _urlconnection = new URL(_url);
//            httpURLConnection = (HttpURLConnection) _urlconnection.openConnection();
//            httpURLConnection.setDoOutput( true );
//            httpURLConnection.setRequestProperty("Content-Type", "application/json");
//            httpURLConnection.setRequestMethod("GET");
//
//            request = new OutputStreamWriter(httpURLConnection.getOutputStream());
//            request.write(parameters);
//            request.flush();
//            request.close();
//            String line = "";
//            InputStreamReader isr = new InputStreamReader(httpURLConnection.getInputStream());
//            BufferedReader reader = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            while ((line = reader.readLine()) != null)
//            {
//                sb.append(line + "\n");
//            }
//            // Response from server after login process will be stored in response variable.
//            _response = sb.toString();
//            // You can perform UI operations here
//            // Toast.makeText(this,"Message from Server: \n"+ response, 0).show();
//            isr.close();
//            reader.close();
//
//        }catch(Exception e){
//            Log.e("Error", e.getMessage());
//        }
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//            HttpResponse response = client.execute(httpPost);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) {
//                HttpEntity entity = response.getEntity();
//                InputStream content = entity.getContent();
//                BufferedReader reader_buffer = new BufferedReader
//                        (new InputStreamReader(content));
//
//                String line;
//                while ((line = reader_buffer.readLine()) != null) {
//                    str.append(line);
//                }
//            } else {
//                Log.e("Log", "Failed to download file..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return str.toString();
    }

    public static class UpdateApp extends AsyncTask<String,Void,Void> {
        private Context context;
        private ProgressBar PG;

        public void setContext(Context contextf){
            context = contextf;
        }
        public void setPG(ProgressBar pg){ PG = pg; }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            PG.setVisibility(View.GONE);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String PATH = "/mnt/sdcard/Download/";
                File file = new File(PATH);
                file.mkdirs();
                String filename = "BalloonAppUpdate.apk";
                File outputFile = new File(file, filename);
                if(outputFile.exists()){
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/"+filename)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
    }
}
