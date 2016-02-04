package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ProductCategory extends ActionBarActivity {
    private AlertDialog.Builder searchbox;
    private Activity c = this;
    private String lv = "";
    private String ref = "";
    private List<NameValuePair> params;
    private EditText text_filter;
    private Category<Category.OneCategory> code_json_object;
    private refreshArrayAdapter rf;
    public static String mgroup = null;
    public static String sgroup = null;
    public static String code = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        Bundle extras = c.getIntent().getExtras();
        if(extras == null){

        }else{
            lv = extras.getString("lv");
            ref = extras.getString("ref");
        }
        text_filter = (EditText) findViewById(R.id.textview_filter);

        String stringUrl = "";
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("office", UserInfo.branch_id));

        if( lv.equals("") ){
            stringUrl = APPCONFIG.PRODUCT_CATEGORY;
            params.add(new BasicNameValuePair("ref", "0"));
        }else if( lv.equals("2") ){
            stringUrl = APPCONFIG.PRODUCT_CATEGORY2;
            params.add(new BasicNameValuePair("ref", ref));
        }else if( lv.equals("3") ){
            stringUrl = APPCONFIG.PRODUCT_CATEGORY3;
            params.add(new BasicNameValuePair("ref", ref));
            params.add(new BasicNameValuePair("mgroup", ProductCategory.mgroup));
            params.add(new BasicNameValuePair("sgroup", ProductCategory.sgroup));
            text_filter.setVisibility(View.VISIBLE);

            text_filter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // นำค่าไปค้นใน String[] แล้วคืนค่ามาเป็น String[] ชุดใหม่
                    // รอ 2 วิ
                    // นำค่าที่ได้ไป setnotify เพื่อเปลี่ยนค่าและ listview

                    ArrayList<String> l = code_json_object.contains( s.toString().toUpperCase() , true );
                    String[] strs = new String[l.size()];
                    strs = l.toArray(strs);
                    int size = strs.length;
                    code_json_object.strs.clear();
                    for(int i = 0 ; i < size ; ++i){
                        code_json_object.strs.add( strs[i] );
                    }
                    rf.notifyDataSetChanged();



//                    Log.e("Text now", String.valueOf( code_json_object.contains( s.toString() ) ));
                }
            });
        }

        if ( Utilities.CHECK_INTERNET_CONN(c) ) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Utilities.ToastAlert(c, ToastAlert.TYPE_INTERNET_CHECK);
        }
    }

    public void reloadlist(View view){
        Intent i = new Intent(c, ProductCategory.class);
        startActivity(i);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try{
                return Utilities.getJSONString(urls[0], params);
            }catch(Exception e){
                return "Unable to retrieve web page. URL my be invalid.";
            }
        }


        @Override
        protected void onPostExecute(String s) {

            ProgressBar progressBar_product = (ProgressBar) findViewById(R.id.progressBar_product);
            Button btn_reload = (Button) findViewById(R.id.btn_reload);
            String str = "{\"category\":" + s.trim() + "}";
            str = str.replaceAll("(\r\n|\n)", "");


            Type t = new TypeToken<Category<Category.OneCategory>>(){}.getType();
            JsonReader reader =new JsonReader(new StringReader( str ));
            reader.setLenient(true);

            code_json_object = null;


            try {
                code_json_object = new Gson().fromJson(str, t);

            } catch (Exception e) {
                Utilities.ToastAlert(c, ToastAlert.TYPE_DATA_FROM_SERVER_ERROR);

                progressBar_product.setVisibility(View.GONE);
                btn_reload.setVisibility(View.VISIBLE);
            }

            if( code_json_object != null ) {
                final ArrayList<Category.OneCategory> codelist = code_json_object.getArrayList();
                ListView listview = (ListView) findViewById(R.id.product_category);


//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, android.R.id.text1, code_json_object.strs);
                rf = new refreshArrayAdapter(c, android.R.layout.simple_list_item_1, code_json_object.strs);

                listview.setAdapter(rf);


                listview.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i ;
                                if( lv.equals("") ){
                                    i = new Intent(c, ProductCategory.class);
                                    i.putExtra("lv", "2");
                                    i.putExtra("ref", codelist.get(position).id );
                                    ProductCategory.mgroup = codelist.get(position).id;
                                    startActivity(i);
                                }else if( lv.equals("2") ){
                                    i = new Intent(c, ProductCategory.class);
                                    i.putExtra("lv", "3");
                                    i.putExtra("ref", codelist.get(position).id );
                                    ProductCategory.sgroup = codelist.get(position).id;
                                    startActivity(i);
                                }else if( lv.equals("3") ){
                                    i = new Intent(c, ViewProductDetail.class);
                                    i.putExtra("lv", "4");
                                    i.putExtra("ref", codelist.get(position).id );
                                    i.putExtra("code", codelist.get( position ).code );
                                    ProductCategory.code = codelist.get( position ).title;
                                    startActivity(i);
                                }
                            }
                        }
                );

                progressBar_product.setVisibility(View.GONE);
            }

        }

    }

    public class ViewHolder{
        public TextView textview1;
    }

    public class refreshArrayAdapter extends ArrayAdapter{
        private List<String> obj;

        public refreshArrayAdapter(Context context, int resource, List<String> strs) {
            super(context, resource, strs);

            obj = strs;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder v;
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
            if(convertView == null){
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
                v = new ViewHolder();
                v.textview1 = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(v);
            }else{
                v = (ViewHolder) convertView.getTag();
            }

            v.textview1.setText((CharSequence) obj.get(position));


            return convertView;
        }



        public void filter(){

        }


    }

    public class Category<OneCategory>{
        OneCategory[] category;
        public ArrayList<Category.OneCategory> pl = null;
        public List<String> strs;
        public List<String> back_strs;

        public class OneCategory{
            public String title;
            public String code;
            public String id;
        }

        public ArrayList<Category.OneCategory> getArrayList(){
            ArrayList<Category.OneCategory> list = new ArrayList<Category.OneCategory>();
            strs = new ArrayList<String>();
            back_strs = new ArrayList<String>();


            for(int i = 0; i < category.length; ++i){
                Category.OneCategory _c = (Category.OneCategory) category[i];
                list.add(_c);
                strs.add(_c.title);
                back_strs.add(_c.title);
            }


            return list;
        }



        public ArrayList<String> contains(Object o, boolean returnArray) {
            return indexOf(o, returnArray);
        }

        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        public ArrayList<String> indexOf(Object o, boolean returnArray) {
            int size = back_strs.size();
            ArrayList<String> str = new ArrayList<String>();
            if (o == null) {
                for (int i = 0; i < size ; i++) {
                    if (back_strs.get(i) == null) {
                        return str;
                    }
                }
            } else {
                for (int i = 0; i < size ; i++) {
                    if (back_strs.get(i).contains(String.valueOf(o))) {
                        try {
                            str.add(back_strs.get(i));
                        }catch(Exception e){

                        }
                    }
                }


//                strs.clear();
//                size = str.size();
//                for (int i = 0; i < size; ++i){
//                    strs.add( str.get(i) );
//                }
//                rf.notifyDataSetChanged();
            }
            return str;
        }

        public int indexOf(Object o) {
            int size = strs.size();
            if (o == null) {
                for (int i = 0; i < size ; i++) {
                    if (strs.get(i) == null) {
                        return i;
                    }
                }
            } else {
                for (int i = 0; i < size ; i++) {
                    if (strs.get(i).contains(String.valueOf(o))) {
                        return i;
                    }
                }
            }
            return -1;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_category, menu);
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
        }else if ( id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View view){
        searchbox = new AlertDialog.Builder(c);

        LayoutInflater layoutInflater = getLayoutInflater();
        final View v = layoutInflater.inflate(R.layout.dialogsearch, null);
        searchbox.setView(v);

        searchbox
                .setPositiveButton("ค้นหา", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText s = (EditText) ((AlertDialog) dialog).findViewById(R.id.input_search);
                        Intent intent = new Intent(c, ViewProductDetail.class);
                        intent.putExtra("code", s.toString());
                        intent.putExtra("lv", "4");
                        startActivity( intent );
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        searchbox.show();


    }
}
