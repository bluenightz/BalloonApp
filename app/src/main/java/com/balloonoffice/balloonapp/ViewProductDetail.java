package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.koushikdutta.ion.Ion;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class ViewProductDetail extends ActionBarActivity implements PostTask.PostTaskInterface {
    private Activity c = this;
    private String lv = "";
    private String code = "";

    private TextView textcode;
    private TextView texttitle;
    private TextView quantity;
    private TextView price;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_detail);

        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            lv = ( bundle.getString("lv") != null )? bundle.getString("lv") : "" ;
//            code = bundle.getString("code");
            code = ProductCategory.code;
        }

        if( lv.equals("4") ){
            Button b = (Button) findViewById(R.id.button2);
            b.setVisibility(View.GONE);
        }

        textcode = (TextView) findViewById(R.id.input_code);
        texttitle = (TextView) findViewById(R.id.input_title);
        quantity = (TextView) findViewById(R.id.input_quantity);
        price = (TextView) findViewById(R.id.input_price);
        imageview = (ImageView) findViewById(R.id.produc_img);

        PostTask<ProductDetail> task = new PostTask<ProductDetail>(c);
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("code", code));
        list.add(new BasicNameValuePair("office", UserInfo.branch_id));

        task.setParams(list);
        task.setType(new TypeToken<ProductDetail>() {
        }.getType());

        task.execute(APPCONFIG.PRODUCT_VIEW_DETAIL);





    }

    @Override
    public <ProductDetail> void onJSONComplete(ProductDetail object) {
        ViewProductDetail.ProductDetail detailProduct = (ViewProductDetail.ProductDetail) object;
        textcode.setText( detailProduct.code );
        texttitle.setText( detailProduct.title );
        quantity.setText( String.valueOf(detailProduct.quantity) );
        price.setText( String.valueOf(detailProduct.price) );

        Ion.with(imageview)
                .load(detailProduct.img);
    }

    public class ProductDetail<E> {
        public String code;
        public String title;
        public int quantity;
        public int price;
        public String img;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent i = new Intent(c, SyncPage.class);
//        startActivity(i);
    }

    public void scanother(View view){
//        IntentIntegrator integrator = new IntentIntegrator( c );
//        integrator.initiateScan();

        Intent intent = new Intent(c, CaptureActivity.class);
        startActivityForResult(intent, ProductList.REQ_INNER_SCANNER);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//        if (scanResult.getContents() != null) {

        if ( requestCode == ProductList.REQ_INNER_SCANNER && intent != null ) {
            // handle scan result

            String strcode = intent.getStringExtra("CONTENT");
            Intent i = new Intent(c, ViewProductDetail.class);
            i.putExtra("code", strcode );
            startActivity(i);
        }
        // else continue with any other code you need in the method
    }
}
