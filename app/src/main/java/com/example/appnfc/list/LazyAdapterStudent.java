package com.example.appnfc.list;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.appnfc.R;
import com.example.appnfc.StudentSubjectActivity;
import com.example.appnfc.TeacherSubjectActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class LazyAdapterStudent extends BaseAdapter {
    
    private Activity activity;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    
    public LazyAdapterStudent(Activity a, ArrayList<HashMap<String, String>> d) {
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
            vi = inflater.inflate(R.layout.list_student_subject_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
       
        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        
        // Setting all values in listview
       title.setText(song.get(StudentSubjectActivity.KEY_TITLE));
        
        return vi;
    }
}