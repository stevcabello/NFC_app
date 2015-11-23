package com.example.appnfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);


		TextView txtEstudiante = (TextView) findViewById(R.id.txtEstudiante);

		txtEstudiante.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this, StudentRegisterActivity.class);
				startActivity(i);
			}

		});

		TextView txtDocente = (TextView) findViewById(R.id.txtDocente);

		txtDocente.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this, TeacherRegisterActivity.class);
				startActivity(i);
			}

		});


	}


	public void ingresoEst(View v) {
		Intent i = new Intent(this, StudentRegisterActivity.class);
		startActivity(i);
	}

	public void ingresoDoc(View v) {
		Intent i = new Intent(this, TeacherRegisterActivity.class);
		startActivity(i);
	}




}
