package com.example.appnfc;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boDocente;
import com.example.appnfc.businessobject.boEstudiante;
import com.example.appnfc.info.Docente;
import com.example.appnfc.info.Estudiante;

public class LoginActivity extends Activity {

	private TareaInicioSesion mAuthTask = null;
	ProgressDialog pDialog;

	private String mUsuario;
	private String mContrasenia;

	private EditText mUsuarioView;
	private EditText mContraseniaView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		mUsuarioView = (EditText) findViewById(R.id.etusuario);
		mContraseniaView = (EditText) findViewById(R.id.etcontrasenia);

		TextView tvRegistrese = (TextView) findViewById(R.id.txRegistrese);
		tvRegistrese.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(i);
			}

		});



		Button btnlogin = (Button) findViewById(R.id.btnInicioSesion);
		btnlogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				ChequeaDatos();

			}

		});
	}

	/**
	 * Check if the fields were correctly entered
	 */
	public void ChequeaDatos() {

		// Reset errors.
		mContraseniaView.setError(null);
		mUsuarioView.setError(null);

		mUsuario = mUsuarioView.getText().toString();
		mContrasenia= mContraseniaView.getText().toString();


		boolean cancel = false;
		View focusView = null;



		if (TextUtils.isEmpty(mUsuario)) {
			mUsuarioView.setError(getString(R.string.error_field_required));
			focusView = mUsuarioView;
			cancel = true;
		}


		if (TextUtils.isEmpty(mContrasenia)) {
			mContraseniaView.setError(getString(R.string.error_field_required));
			focusView = mContraseniaView;
			cancel = true;
		}


		if (cancel) { //if there is a problem, focus the field
			focusView.requestFocus();

		} else { //if fields are ok then Login

			mAuthTask = new TareaInicioSesion();
			mAuthTask.execute();

		}


	}



	/**
	 * AsynTask to handle the Login of Student or Teacher
	 */
	private class TareaInicioSesion extends AsyncTask<String, Integer, Boolean> {

		private Docente resultWebServicesD;
		private Estudiante resultWebServicesE;
		private int indicador = 0;

		protected Boolean doInBackground(String... params)
		{
			//First try to login as Teacher
			boDocente objDoc = new boDocente();
			resultWebServicesD = objDoc.LoginDocente(mUsuario,mContrasenia);

			if (resultWebServicesD == null) { //If null then login as Student

				boEstudiante objEst = new boEstudiante();
				resultWebServicesE = objEst.LoginEstudiante(mUsuario,mContrasenia);

				if (resultWebServicesE == null)
					return false;

				else{
					indicador = 2; //If the user is a Student
					return true;
				}

			}

			else{
				indicador = 1;  //If the user is a Teacher
				return true;
			}

		}



		protected void onPreExecute() {

			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Authenticating...Please wait" );
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}


		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();
			if (result)
			{

				if (indicador == 1) { //if case the user is a Teacher

					Intent i = new Intent(LoginActivity.this, TeacherMenuActivity.class);
					i.putExtra("iddocente", resultWebServicesD.getId());
					i.putExtra("nombredocente", resultWebServicesD.getNombre());
					startActivity(i);
					LoginActivity.this.finish();

				} else { //in case the user is a Student

					Intent i = new Intent(LoginActivity.this, StudentSubjectActivity.class);
					i.putExtra("idestudiante", resultWebServicesE.getId());
					i.putExtra("nombreestudiante", resultWebServicesE.getNombre());
					startActivity(i);
					LoginActivity.this.finish();
				}


			}
			else{

				Toast.makeText(LoginActivity.this, "Invalid username or password ", Toast.LENGTH_SHORT).show();
			}

		}




	}



}
