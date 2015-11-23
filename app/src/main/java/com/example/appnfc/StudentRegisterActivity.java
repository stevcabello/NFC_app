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
import android.widget.Toast;

import com.example.appnfc.businessobject.boEstudiante;
import com.example.appnfc.info.Estudiante;

public class StudentRegisterActivity extends Activity {

	private String mNombre;
	private String mApellido;
	private String mUsuario;
	private String mContrasenia;
	private String mConfContrasenia;
	private String mEmail;
	private String mNumeroMatricula;



	// UI references.
	private EditText mNombreView;
	private EditText mApellidoView;
	private EditText mUsuarioView;
	private EditText mContraseniaView;
	private EditText mConfContraseniaView;
	private EditText mEmailView;
	private EditText mNumeroMatriculaView;

	ProgressDialog pDialog;
	private TareaRegistraEstudiante mAuthTask = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_student_register);

		mNombreView = (EditText) findViewById(R.id.etNombre);
		mApellidoView = (EditText) findViewById(R.id.etApellido);
		mUsuarioView = (EditText) findViewById(R.id.etusuario);
		mContraseniaView = (EditText) findViewById(R.id.etcontrasenia);
		mConfContraseniaView = (EditText) findViewById(R.id.etreptcontrasenia);
		mEmailView = (EditText) findViewById(R.id.etemail);
		mNumeroMatriculaView = (EditText) findViewById(R.id.etNumeroMatricula);



		Button btnRegistrar = (Button) findViewById(R.id.btnRegistrar);


		btnRegistrar.setOnClickListener(new View.OnClickListener() {

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
		mNombreView.setError(null);
		mApellidoView.setError(null);
		mContraseniaView.setError(null);
		mConfContraseniaView.setError(null);
		mUsuarioView.setError(null);
		mEmailView.setError(null);
		mNumeroMatriculaView.setError(null);



		// Store values at the time of the login attempt.
		mNombre = mNombreView.getText().toString();
		mApellido = mApellidoView.getText().toString();
		mUsuario = mUsuarioView.getText().toString();
		mContrasenia= mContraseniaView.getText().toString();
		mConfContrasenia = mConfContraseniaView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mNumeroMatricula = mNumeroMatriculaView.getText().toString();



		boolean cancel = false;
		View focusView = null;




		if (TextUtils.isEmpty(mNombre)) {
			mNombreView.setError(getString(R.string.error_field_required));
			focusView = mNombreView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mApellido)) {
			mApellidoView.setError(getString(R.string.error_field_required));
			focusView = mApellidoView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mUsuario)) {
			mUsuarioView.setError(getString(R.string.error_field_required));
			focusView = mUsuarioView;
			cancel = true;
		}


		if (TextUtils.isEmpty(mContrasenia)) {
			mContraseniaView.setError(getString(R.string.error_field_required));
			focusView = mContraseniaView;
			cancel = true;
		} else if (mContrasenia.length() < 6 || mContrasenia.length()>10) {
			mContraseniaView.setError(getString(R.string.error_short_password));
			focusView = mContraseniaView;
			cancel = true;
		}


		if (TextUtils.isEmpty(mConfContrasenia)) {
			mConfContraseniaView.setError(getString(R.string.error_field_required));
			focusView = mConfContraseniaView;
			cancel = true;
		} else if (!mConfContrasenia.equals(mContrasenia)) {
			mConfContraseniaView.setError(getString(R.string.error_confirm_password));
			focusView = mConfContraseniaView;
			cancel = true;
		}


		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mNumeroMatricula)) {
			mNumeroMatriculaView.setError(getString(R.string.error_field_required));
			focusView = mNumeroMatriculaView;
			cancel = true;
		}


		if (cancel) {
			focusView.requestFocus();

		} else {

			mAuthTask = new TareaRegistraEstudiante();
			mAuthTask.execute();

		}


	}


	/**
	 * Asyntask to Save a student data (registration)
	 */
	private class TareaRegistraEstudiante extends AsyncTask<String, Integer, Boolean>{
		int idestu;
		Estudiante objEstudiante = new Estudiante();
		@Override
		protected Boolean doInBackground(String... params) {

			try {

				boEstudiante BOestudiante =new boEstudiante();
				objEstudiante.setId(0);
				objEstudiante.setNombre(mNombre);
				objEstudiante.setApellido(mApellido);
				objEstudiante.setUsuario(mUsuario);
				objEstudiante.setContrasenia(mContrasenia);
				objEstudiante.setEmail(mEmail);
				objEstudiante.setNumeroMatricula(mNumeroMatricula);




				int Result = BOestudiante.Registrar(objEstudiante);
				if (Result > 0){

					idestu = Result;
					return true;
				}

				else
					return false;

			}catch (Exception e){
				e.printStackTrace();
				return false;

			}
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(StudentRegisterActivity.this);
			pDialog.setMessage("Saving...." );
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			pDialog.dismiss();

			if (result)
			{

				Intent i = new Intent(StudentRegisterActivity.this, StudentSubjectActivity.class);
				i.putExtra("idestudiante", idestu);
				i.putExtra("nombreestudiante",objEstudiante.getNombre());


				startActivity(i);
				StudentRegisterActivity.this.finish();

			} else {

				Toast.makeText(StudentRegisterActivity.this, "Saving failed, please try again", Toast.LENGTH_LONG).show();

			}
		}

	}






}
