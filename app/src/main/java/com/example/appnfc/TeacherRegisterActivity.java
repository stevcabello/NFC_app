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

import com.example.appnfc.businessobject.boDocente;
import com.example.appnfc.info.Docente;

public class TeacherRegisterActivity extends Activity {

	private String mNombre;
	private String mApellido;
	private String mUsuario;
	private String mContrasenia;
	private String mConfContrasenia;
	private String mEmail;
	private String mCedula;



	// UI references.
	private EditText mNombreView;
	private EditText mApellidoView;
	private EditText mUsuarioView;
	private EditText mContraseniaView;
	private EditText mConfContraseniaView;
	private EditText mEmailView;
	private EditText mCedulaView;

	ProgressDialog pDialog;
	private TareaRegistraDocente mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_register);

		mNombreView = (EditText) findViewById(R.id.etNombre);
		mApellidoView = (EditText) findViewById(R.id.etApellido);
		mUsuarioView = (EditText) findViewById(R.id.etusuario);
		mContraseniaView = (EditText) findViewById(R.id.etcontrasenia);
		mConfContraseniaView = (EditText) findViewById(R.id.etreptcontrasenia);
		mEmailView = (EditText) findViewById(R.id.etemail);
		mCedulaView = (EditText) findViewById(R.id.etCedula);
		

		Button btnRegistrar = (Button) findViewById(R.id.btnRegistrar);


		btnRegistrar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				ChequeaDatos();

			}

		});

	}


	public void ChequeaDatos() {


		// Reset errors.
		mNombreView.setError(null);
		mApellidoView.setError(null);
		mContraseniaView.setError(null);
		mConfContraseniaView.setError(null);
		mUsuarioView.setError(null);
		mEmailView.setError(null);
		mCedulaView.setError(null);



		// Store values at the time of the login attempt.
		mNombre = mNombreView.getText().toString();
		mApellido = mApellidoView.getText().toString();
		mUsuario = mUsuarioView.getText().toString();
		mContrasenia= mContraseniaView.getText().toString();
		mConfContrasenia = mConfContraseniaView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mCedula = mCedulaView.getText().toString();



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


		if (TextUtils.isEmpty(mCedula)) {
			mCedulaView.setError(getString(R.string.error_field_required));
			focusView = mCedulaView;
			cancel = true;
		}



		if (cancel) {
			focusView.requestFocus();

		} else {

			mAuthTask = new TareaRegistraDocente();
			mAuthTask.execute();

		}


	}

	/**
	 * Save the Teacher's data
	 */
	private class TareaRegistraDocente extends AsyncTask<String, Integer, Boolean>{
		int iddoc;
		Docente objDocente = new Docente();
		@Override
		protected Boolean doInBackground(String... params) {

			try {

				boDocente BODocente =new boDocente();
				objDocente.setId(0);
				objDocente.setNombre(mNombre);
				objDocente.setApellido(mApellido);
				objDocente.setUsuario(mUsuario);
				objDocente.setContrasenia(mContrasenia);
				objDocente.setEmail(mEmail);
				objDocente.setCedula(mCedula);

				int Result = BODocente.Registrar(objDocente);
				if (Result > 0){

					iddoc = Result;

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
			pDialog = new ProgressDialog(TeacherRegisterActivity.this);
			pDialog.setMessage("Saving data......" );
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {

			pDialog.dismiss();

			if (result)
			{

				Intent i = new Intent (TeacherRegisterActivity.this, TeacherMenuActivity.class);
				i.putExtra("iddocente", iddoc);
				i.putExtra("nombredocente",objDocente.getNombre());
				startActivity(i);
				TeacherRegisterActivity.this.finish();

			} else {

				Toast.makeText(TeacherRegisterActivity.this, "Registration failed, please try again", Toast.LENGTH_LONG).show();

			}
		}

	}




}
