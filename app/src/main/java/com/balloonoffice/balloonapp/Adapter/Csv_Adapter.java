package com.balloonoffice.balloonapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.balloonoffice.balloonapp.R;

import java.util.List;

/**
 * Created by bluenightz on 2/7/16 AD.
 */
public class Csv_Adapter extends ArrayAdapter{
    public Csv_Adapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder;
        LayoutInflater layoutInflater;
        Context context = getContext();


        if( convertView == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.eachproduct, null);
            viewholder = new Viewholder();
            viewholder.tcode = (TextView) convertView.findViewById(R.id.text_code);
            viewholder.imgview = (ImageView) convertView.findViewById(R.id.each_img);
            viewholder.history = (TextView) convertView.findViewById(R.id.updatehistory);
            viewholder.tcount = (TextView) convertView.findViewById(R.id.number_count);
            viewholder.tstock = (TextView) convertView.findViewById(R.id.number_stock);

            convertView.setTag( viewholder );

        }else{
            viewholder = (Viewholder) convertView.getTag();
        }

        viewholder.tcode.setText("Test");

        return convertView;
    }

    public class Viewholder{
        public TextView tcode;
        public ImageView imgview;
        public TextView history;
        public TextView tcount;
        public TextView tstock;
    }
}
