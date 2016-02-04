package com.balloonoffice.balloonapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;


public class InputData extends ActionBarActivity {
    private Bundle b = null;
    private TextView t_code;
    private TextView t_title;
    private ImageView imageview;
    private TextView t_quantity;
    private Activity c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);


        t_quantity = (TextView) findViewById(R.id.quantity);
        t_code = (TextView) findViewById(R.id.input_inputdata_code);
        t_title = (TextView) findViewById(R.id.input_inputdata_title);
        imageview = (ImageView) findViewById(R.id.produc_img);



        t_code.setText(ActiveProduct.CODE);
        t_title.setText(ActiveProduct.TITLE);

        Ion.with(imageview)
                .load(ActiveProduct.SRC);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_data, menu);
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

    public void update(View view){
        Time now = new Time();
        now.setToNow();
        int Y = Integer.valueOf( now.format("%Y") );
        Y = Y + 543;

        // String timestr = now.format("%d/%m/") + String.valueOf( Y ) + " " + now.format("%T");


        String str = t_quantity.getText().toString();

        if( ActiveProduct.CODEOBJ.checkStock(str, now.format("%d/%m/") + String.valueOf( Y ), now.format("%T")) ){
            // ตรง -> บันทึก -> เปลี่ยน intent
//            ActiveProduct.CODEOBJ.checkschedule[0].time = now.format("%T");
//            ActiveProduct.CODEOBJ.checkschedule[0].date = now.format("%d/%m/") + String.valueOf( Y );

            Utilities.saveCountlist(c, ChangedList.codeObj);

            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle(R.string.updateDataComplete)
//                    .setMessage(R.string.AlertDialogTitle_Number_not_Match_desc)
                    .setPositiveButton(R.string.natural_button_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(getApplicationContext(), ProductList.class);
                            startActivity(intent);
                        }
                    });
            builder.create().show();

        }else{
            // ไม่ตรง -> alertdialog.show()

            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle(R.string.AlertDialogTitle_Number_not_Match)
                    .setMessage(R.string.AlertDialogTitle_Number_not_Match_desc)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utilities.saveCountlist(c, ChangedList.codeObj);

                            Intent intent = new Intent(getApplicationContext(), ProductList.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            t_quantity.setText("");
                        }
                    });
            builder.create().show();

        }

    }
}
