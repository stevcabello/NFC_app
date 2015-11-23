package com.example.appnfc.list;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.appnfc.R;
import com.example.appnfc.TestListActivity;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class LazyAdapterStudentTest extends BaseAdapter {
    
    private Activity activity;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    
    public LazyAdapterStudentTest(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    



    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_student_test_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // descripcion

       
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
       title.setText(song.get(TestListActivity.KEY_TITLE));
       artist.setText(song.get(TestListActivity.KEY_ARTIST));
     
    	   
       
        
        return vi;
    }
}