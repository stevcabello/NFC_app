package com.example.appnfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class TeacherMenuActivity extends Activity {
	
	int iddocente;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_menu);
		
		iddocente = getIntent().getExtras().getInt("iddocente");
		
		 Button btntestlist = (Button) findViewById(R.id.btntestlist);
	       

	        btntestlist.setOnClickListener(new View.OnClickListener() {
	        	
	        	public void onClick(View v) {
	        		
	        		Intent i = new Intent(TeacherMenuActivity.this,TeacherSubjectActivity.class);
	        		i.putExtra("iddocente", iddocente);
	        		i.putExtra("flag",1);
	        		startActivity(i);
	        		

	        	}
	         
	    	});
	        
	        
	        
	        Button btnscoretest = (Button) findViewById(R.id.btnscoretest);
		       

	        btnscoretest.setOnClickListener(new View.OnClickListener() {
	        	
	        	public void onClick(View v) {
	        		
	        		Intent i = new Intent(TeacherMenuActivity.this,TeacherSubjectActivity.class);
	        		i.putExtra("iddocente", iddocente);
	        		i.putExtra("flag",2);
	        		startActivity(i);
	        		

	        	}
	         
	    	});
		
		
	}


	
	public void onBackPressed() {
        
		AlertDialog.Builder builder1 = new AlertDialog.Builder(TeacherMenuActivity.this);
        builder1.setMessage("Logout?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
            	
                dialog.cancel();
            }
        });
        builder1.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
            	
            	Intent i = new Intent(TeacherMenuActivity.this, LoginActivity.class);
				startActivity(i);
				TeacherMenuActivity.this.finish();
        		

                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
		
		
	}
	
	

}
