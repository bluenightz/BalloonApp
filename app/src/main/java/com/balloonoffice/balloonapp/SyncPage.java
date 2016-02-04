package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SyncPage extends ActionBarActivity {
    private Activity c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_page);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sync_page, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void syncData(View view){
        // load json
        // เอามาแปลงค่าเป็น gson
        // สร้าง intent
        // ส่งค่าไปที่ intent
        // startactivity
        // Gets the URL from the UI's text field.

        if( Utilities.CHECK_INTERNET_CONN(this) ) {
            Intent intent = new Intent(this, ProductList.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, (CharSequence) getString(R.string.checkNetwork), Toast.LENGTH_LONG).show();
        }

    }



    public void skipSync(View view){

        boolean isfirst = true;
        //ActiveProduct.setValue(codelist.get(position), isfirst);
        IntentIntegrator integrator = new IntentIntegrator( c );
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult.getContents() != null) {
            // handle scan result
            String strcode = scanResult.getContents().toString();



            Intent i = new Intent(c, ViewProductDetail.class);
            startActivity(i);

        }
        // else continue with any other code you need in the method
    }

    public void quickscan(View view){

    }

}
