package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balloonoffice.balloonapp.Model.Csv_item_model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.koushikdutta.ion.Ion;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductList extends ActionBarActivity implements PostTask.PostTaskInterface {
    private Activity c = this;
    private AlertDialog dialog;
    public static final int REQ_INNER_SCANNER = 50;
    private ArrayList<Csv_item_model> csv_list;

    @Override
    public <T> void onJSONComplete(T object) {
        Utilities.ToastAlert(c, ToastAlert.TYPE_DATA_COMPLETE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Boolean isMac5 = this.getIntent().getBooleanExtra("isMac5", false);
        String csvPath = this.getIntent().getStringExtra("csvPath");


        if( isMac5 ){
            csv_list = Utilities.ReadCSVFile( csvPath );
        }else {

            String stringUrl = APPCONFIG.PRODUCTLIST_JSON + "?branch_id=" + UserInfo.branch_id;
            if (Utilities.CHECK_INTERNET_CONN(c)) {
                new DownloadWebpageTask().execute(stringUrl);
            } else {
                Utilities.ToastAlert(c, ToastAlert.TYPE_INTERNET_CHECK);
            }
        }

        createAlertDialog();

    }

    private void createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder( c );
        builder.setMessage(R.string.dialog_code_notmatch)
                .setTitle(R.string.dialog_code_notmatch_head)
                .setNeutralButton(R.string.text_scan_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if ( requestCode == REQ_INNER_SCANNER){
//            Log.e("Check", "Intent");
//        }

        if ( requestCode == REQ_INNER_SCANNER && intent != null ) {
//            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//            if (scanResult.getContents() != null) {
                String strcode = intent.getStringExtra("CONTENT");
                // handle scan result
//              String strcode = scanResult.getContents().toString();
                ChangedList.Result result = (ChangedList.Result) ChangedList.check(strcode);

                if (SCANMODE.TYPE == SCANMODE.SCANMODE_LIST) {
                    if (ActiveProduct.CODE.equals(strcode)) {
                        ActiveProduct.setValue(result.codeobj);
                        Intent i = new Intent(c, InputData.class);
                        startActivity(i);
                    } else {
                        dialog.show();
                    }
                } else if (SCANMODE.TYPE == SCANMODE.SCANMODE_AUTO) {
                    if (result.b == true) {
                        ActiveProduct.setValue(result.codeobj);
                        Intent i = new Intent(c, InputData.class);
                        startActivity(i);

                    } else {
                        dialog.show();
                    }
                } else if (SCANMODE.TYPE == SCANMODE.SCANMODE_VIEW) {
                    Intent i = new Intent(c, ViewProductDetail.class);
                    i.putExtra("code", strcode );
                    startActivity(i);
                }


        }
        // else continue with any other code you need in the method
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_product_list, menu);
        MenuItem menuItem = menu.add(Menu.NONE,
                99,
                1,
                R.string.btn_sendData);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setIcon(R.drawable.ic_check_circle_white_24dp);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }else if(id == 99){
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(c);
            alertDialog.setTitle(R.string.sendData_title)
                    .setMessage(R.string.sendData_desc)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(Utilities.CHECK_INTERNET_CONN(c))

                            {
                                PostTask<SendComplete> task = new PostTask<SendComplete>(c);
                                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                                String jsonStr = new Gson().toJson(ChangedList.codeObj);
                                params.add(new BasicNameValuePair("jsonStr", jsonStr));
                                params.add(new BasicNameValuePair("branch_id",UserInfo.branch_id));
                                task.setType( new TypeToken<SendComplete>(){}.getType() );
                                task.setParams(params);
                                task.execute(APPCONFIG.SAVE_DATA);
                            }

                            else

                            {
                                Utilities.ToastAlert(c, ToastAlert.TYPE_INTERNET_CHECK);
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alertDialog.create().show();
            return true;
        }else if(id == android.R.id.home){

            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SendComplete<T>{
        public boolean success;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(c, MainActivity.class);
        startActivity(i);
    }

    public class CustomArrayAdapter extends ArrayAdapter<CodeObj.Code>{
        private Context context;
        private ArrayList<CodeObj.Code> values;

        public CustomArrayAdapter(Context context,int Resource, ArrayList<CodeObj.Code> values){
            super(context, Resource, values);
            this.context = context;
            this.values = values;

        }

        public class Viewholder{
            public TextView tcode;
            public ImageView imgview;
            public TextView history;
            public TextView tcount;
            public TextView tstock;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutinflater;
            Viewholder holder;
            View row;
            CodeObj.Code code = values.get(position);
//            int max = code.checkschedule.length;
            CodeObj.Time[] time = code.checkschedule;

            if( convertView == null ){
                layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutinflater.inflate(R.layout.eachproduct, null);
                holder = new Viewholder();
                holder.tcode = (TextView) convertView.findViewById(R.id.text_code);
                holder.imgview = (ImageView) convertView.findViewById(R.id.each_img);
                holder.history = (TextView) convertView.findViewById(R.id.updatehistory);
                holder.tcount = (TextView) convertView.findViewById(R.id.number_count);
                holder.tstock = (TextView) convertView.findViewById(R.id.number_stock);

                convertView.setTag(holder);
            }else{
                holder = (Viewholder) convertView.getTag();
            }


            Ion.with(holder.imgview)
                    .load(code.src);

            holder.tcode.setText(code.code);

            try {
                CodeObj.Time lastestTime = time[time.length - 1];
                String userCountNum = (lastestTime.quantity.equals("-1"))? "-" : lastestTime.quantity ;
//                holder.history.setText( lastestTime.date + ":" + lastestTime.time + " เหลือ " + userCountNum + "/" + code.quantity + " ชิ้น");
                holder.history.setText( lastestTime.date + ":" + lastestTime.time );
                holder.tcount.setText(userCountNum);
                holder.tstock.setText(code.quantity);

                if( lastestTime.isCorrect ){
                    holder.history.setTextColor(getResources().getColor(R.color.Green));
                }else{
                    holder.history.setTextColor(getResources().getColor(R.color.Red));
                }
//                holder.history.setText(lastestTime.time + " " + lastestTime.date + " : คงเหลือ " + lastestTime.quantity + " ชิ้น");
            }catch(ArrayIndexOutOfBoundsException e){
                Log.e("NullPointerException", "Can't find time from checkschedule");
            }catch(Exception e){

            }

//            Ion.with(imgview)
//                    .load(code.src);

            return convertView;


        }
    }



    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                //List<NameValuePair> params = new ArrayList<NameValuePair>();

                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair("office", UserInfo.branch_id));

                return Utilities.getJSONString(urls[0], params);
            }catch(Exception e){
                return "Unable to retrieve web page. URL my be invalid.";
            }
        }


        @Override
        protected void onPostExecute(String s) {



            ProgressBar progressBar_product = (ProgressBar) findViewById(R.id.progressBar_product);
            Button btn_reload = (Button) findViewById(R.id.btn_reload);
            String str = "{\"codes\":" + s.trim() + "}";
            str = str.replaceAll("(\r\n|\n)", "");


            Type t = new TypeToken<CodeObj<CodeObj.Code>>(){}.getType();
            JsonReader reader =new JsonReader(new StringReader( str ));
            reader.setLenient(true);

            CodeObj<CodeObj.Code> code_json_object = null;
            CodeObj<CodeObj.Code> countlist_json_object = null;

            try {
                code_json_object = new Gson().fromJson(str, t);
                code_json_object.addtofirst();

            } catch (Exception e) {
                Utilities.ToastAlert(c, ToastAlert.TYPE_DATA_FROM_SERVER_ERROR);

                progressBar_product.setVisibility(View.GONE);
                btn_reload.setVisibility(View.VISIBLE);
            }

            if( code_json_object != null ) {
                ArrayList<CodeObj.Code> codelist = code_json_object.getArrayList();

                String _date = codelist.get(0).checkschedule[0].date.replace("/", "_");
                APPCONFIG.PREFERENCE_FILENAME = APPCONFIG.PACKAGENAME + ".count_" + _date;
                String filename = APPCONFIG.PREFERENCE_FILENAME;
                SharedPreferences prefObj = getSharedPreferences(filename, MODE_PRIVATE);
                String content = prefObj.getString("content", "false");
                if( content.equals("false") ){

                }else{
                    Gson g = new Gson();
                    try {
                        countlist_json_object = g.fromJson(content, t);
                        codelist = countlist_json_object.getArrayList();

                        updateCodelist(code_json_object, countlist_json_object);
                        codelist = code_json_object.getArrayList();

                    }catch(Exception e){

                    }
                }


                final ArrayList<CodeObj.Code> codelist2 = codelist;


//                ChangedList.products = codelist;
                ChangedList.codeObj = code_json_object;

                CustomArrayAdapter cAdapter = new CustomArrayAdapter(getApplicationContext(), R.layout.eachproduct, codelist2);


                ListView listview = (ListView) findViewById(R.id.product_LV);
                listview.setAdapter(cAdapter);


                TextView textview = (TextView) findViewById(R.id.productlist_intro);
                String str2 = String.format("ของวันที่ %s | ทั้งหมด %s รายการ", codelist.get(0).checkschedule[0].date, codelist2.size());
                textview.setText( str2 );
//                textview.setText( getResources().getString( R.string.intro_product_list ) + " " + codelist.get(0).checkschedule[0].date + " เวลา " + codelist.get(0).checkschedule[0].time );

                listview.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                SCANMODE.TYPE = SCANMODE.SCANMODE_LIST;
                                ActiveProduct.setValue(codelist2.get(position));

                                Intent intent = new Intent(c, CaptureActivity.class);
                                startActivityForResult(intent, REQ_INNER_SCANNER);

//                                IntentIntegrator integrator = new IntentIntegrator(c);
//                                integrator.initiateScan();
                            }
                        }
                );

                progressBar_product.setVisibility(View.GONE);
            }

        }

    }

    private void updateCodelist(CodeObj<CodeObj.Code> codelist, CodeObj<CodeObj.Code> countlist){
        int max = codelist.codes.length;
        for(int i = 0; i < max; ++i){
            for(int x = 0; x < max; ++x) {
                if (codelist.codes[i].code.equals(countlist.codes[x].code)) {
                    codelist.codes[i].checkschedule[0].quantity = countlist.codes[x].checkschedule[0].quantity;
                    codelist.codes[i].checkschedule[0].time = countlist.codes[x].checkschedule[0].time;
                    codelist.codes[i].checkschedule[0].date = countlist.codes[x].checkschedule[0].date;
                    codelist.codes[i].checkschedule[0].isCorrect = countlist.codes[x].checkschedule[0].isCorrect;
                }
            }
        }
    }

    public void callbarcode(View view){
        int id = view.getId();
        switch(id){
            case R.id.btn_autoscan:
                SCANMODE.TYPE = SCANMODE.SCANMODE_AUTO;
                break;
            case R.id.btn_viewproduct:
                SCANMODE.TYPE = SCANMODE.SCANMODE_VIEW;
                break;
        }
//        IntentIntegrator integrator = new IntentIntegrator(c);
//        integrator.initiateScan();
        Intent intent = new Intent(c, CaptureActivity.class);
        startActivityForResult(intent, REQ_INNER_SCANNER);
    }



    public void reloadlist(View view){
        Intent i = new Intent(c, ProductList.class);
        startActivity(i);
    }




    public class CodeObj<Code>{
        Code[] codes;
        public ArrayList<CodeObj.Code> pl = null;

        public void addtofirst(){
            CodeObj.Code c = (CodeObj.Code) codes[0];
            c.code = "8858667045108";
        }

        public ArrayList<CodeObj.Code> getArrayList(){
            ArrayList<CodeObj.Code> list = new ArrayList<CodeObj.Code>();


            for(int i = 0; i < codes.length; ++i){
                CodeObj.Code _c = (CodeObj.Code) codes[i];
                list.add(_c);
            }


            return list;
        }

        public class Time{
            String time;
            String date;
            String quantity; // qunatity from admin count at that time
            boolean isCorrect;
        }

        public class Code{
            String code;
            String title;
            String quantity; // quantity from yesterday
            String src;

            Time[] checkschedule;




            public boolean checkStock(String num, String date, String time){
                Time t = (CodeObj.Time) checkschedule[0];
                t.quantity = num;
                t.time = time;
                t.date = date;

                if( quantity.equals(t.quantity) ){
                    t.isCorrect = true;
                }else{
                    t.isCorrect = false;
                }

                return t.isCorrect;
            }

        }

    }


}
