package com.example.appnfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boEstudiante;
import com.example.appnfc.businessobject.boTest;
import com.example.appnfc.database.DatabaseHandler;
import com.example.appnfc.info.Materia;
import com.example.appnfc.info.RegistroTest;
import com.example.appnfc.list.LazyAdapterStudent;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentSubjectActivity extends Activity {

	Materia[] vResult =null;
	static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";


	ListView list;
	LazyAdapterStudent adapter;

	private ProgressDialog pDialog;

	private  TareaGetMaterias mTask = null;
	private  TareaGetSavedTests mTaskSavedTests = null;
	TareaRegTest ttp;
	TareaCalificarTest tct;

	private int ROWS;
	private String idmateria[] = {};
	private String nombremateria[] = {};


	private int IdEstudianteG;

	DatabaseHandler db;

	ArrayList<String> TestGuardados;
	ArrayList<String> TestGuardadosCabeceras;

	private String idtestSaved[] = {};
	private String idmateriaSaved[] = {};
	private String idpreguntaSaved[] = {};
	private String opcionSaved[] = {};

	boTest objTest = null;

	private String idtestHeader[] = {};
	private String idmateriaHeader[] = {};

	private int IdTestG;
	private int IdMateriaG;
	private int cont = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_student_subject);

		IdEstudianteG = this.getIntent().getExtras().getInt("idestudiante");
		Log.i("idestudiante",String.valueOf(IdEstudianteG));

		db = new DatabaseHandler(StudentSubjectActivity.this);

		mTask = new TareaGetMaterias();
		mTask.execute();
	}

	public void CargarListaMaterias(){

		ArrayList<HashMap<String, String>> Listmaterias = new ArrayList<HashMap<String, String>>();


		for (int i = 0; i < ROWS; i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_ID, idmateria[i]);
			map.put(KEY_TITLE, nombremateria[i]);

			Listmaterias.add(map);
		}



		list=(ListView)findViewById(R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter=new LazyAdapterStudent(this, Listmaterias);


		list.setAdapter(adapter);


		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Intent i = new Intent(StudentSubjectActivity.this, StudentTestListActivity.class);
				i.putExtra("idestudiante", IdEstudianteG);
				i.putExtra("idmateria", Integer.parseInt(idmateria[(int) id]));
				startActivity(i);

			}
		});



		TestGuardadosCabeceras = db.getTestHeader();

		idtestHeader = new String[TestGuardadosCabeceras.size()/2];
		idmateriaHeader = new String[TestGuardadosCabeceras.size()/2];

		for (int i=0 ; i<TestGuardadosCabeceras.size();i=i+2){
			idtestHeader[i/2] = TestGuardadosCabeceras.get(i);
			idmateriaHeader[i/2] = TestGuardadosCabeceras.get(i+1);
		}




		idtestSaved = new String[]{};
		idmateriaSaved= new String[]{};
		idpreguntaSaved= new String[]{};
		opcionSaved= new String[]{};

		if (idtestHeader.length > 0) {
			IdTestG = Integer.parseInt(idtestHeader[0]);
			IdMateriaG = Integer.parseInt(idmateriaHeader[0]);


			mTaskSavedTests = new TareaGetSavedTests();
			mTaskSavedTests.execute();
		}






	}


	/**
	 * Get the student's subjects
	 */
	private class TareaGetMaterias extends AsyncTask<Integer, Boolean , Boolean >{

		protected Boolean doInBackground(Integer... params) {

			try
			{


				Log.i("idestudiante2",String.valueOf(IdEstudianteG));
				boEstudiante objMaterias = new boEstudiante();

				vResult = objMaterias.GetMateriasXEstudiante(IdEstudianteG);


				if ( vResult != null) {

					ROWS = vResult.length;

					idmateria = new String[ROWS];
					nombremateria = new String[ROWS];

					for (int i = 0; i < ROWS; i++) {
						idmateria[i] = String.valueOf(vResult[i].getId());
						nombremateria[i] = vResult[i].getNombre();
					}

					return true;
				}

				else return false;


			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(StudentSubjectActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Cargando Materias...");
			pDialog.setCancelable(true);
			pDialog.show();

		}



		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();

			if (result)
			{

				CargarListaMaterias();

			}
			else{
				Toast.makeText(StudentSubjectActivity.this, "No available sujects", Toast.LENGTH_LONG).show();

			}
		}



	}

	/**
	 * Get the tests to complete from the selected subject
	 */
	private class TareaGetSavedTests extends AsyncTask<Integer, Boolean , Boolean >{


		protected Boolean doInBackground(Integer... params) {

			try
			{

				Log.i("idtestBB", String.valueOf(IdTestG));
				Log.i("idmateriaBB", String.valueOf(IdMateriaG));

				TestGuardados = db.getTestDetail(IdTestG, IdMateriaG);

				idtestSaved = new String[TestGuardados.size()/4];
				idmateriaSaved = new String[TestGuardados.size()/4];
				idpreguntaSaved = new String[TestGuardados.size()/4];
				opcionSaved = new String[TestGuardados.size()/4];

				for (int i = 0; i <TestGuardados.size(); i=i+4) {
					idtestSaved[i/4] = TestGuardados.get(i);
					idmateriaSaved[i/4] = TestGuardados.get(i+1);
					idpreguntaSaved[i/4] = TestGuardados.get(i+2);
					opcionSaved[i/4] = TestGuardados.get(i+3);
				}



				return true;

			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(StudentSubjectActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Sending the tests...");
			pDialog.setCancelable(true);
			pDialog.show();

		}



		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();

			if (result)
			{

				ttp = new TareaRegTest();
				ttp.execute();

			}
			else{
				//Toast.makeText(StudentSubjectActivity.this, "No hay materias disponibles", Toast.LENGTH_LONG).show();

			}
		}



	}


	/**
	 * Save a student's test
	 */
	private class TareaRegTest extends AsyncTask<String, Integer, Boolean>{

		ProgressDialog pDialog;

		@Override
		protected Boolean doInBackground(String... params) {

			if (!isCancelled())
			{
				String msg = TareaRegistroTest();
				if (msg.equals("ok"))
					return true;
				else
					return false;

			}
			else return false;
		}


		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(StudentSubjectActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Saving your tests...");
			pDialog.setCancelable(false);
			pDialog.setMax(100);
			pDialog.show();

		}


		@Override
		protected void onPostExecute(Boolean result) {

			pDialog.dismiss();

			if (result)
			{
				tct = new TareaCalificarTest();
				tct.execute();


			}

			else {

				db.deleteTest(IdTestG, IdMateriaG);

				Toast toast = Toast.makeText(getApplicationContext(), "Sending you saved tests failed, possible duplicated test.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			ttp = null;
		}

	}

	/**
	 *
	 * @return ok if test successfully saved, otherwise return ""
	 */
	private String TareaRegistroTest()
	{
		String vResult = "";
		String tmp = "";
		int i=0;
		boTest objTest = new boTest();
		ArrayList<RegistroTest> Temporal = GetListaRegistroTest();


		for (RegistroTest registroTest : Temporal) {

			Log.i("registrotest","1");

			if (i==0)
			{
				Log.i("registrotest","2");
				vResult = objTest.setRegistrarTest(registroTest, 'C');
				if (vResult.equals("ok"))
				{
					vResult = objTest.setRegistrarTest(registroTest, 'D');
				}
				else break;
			}
			else{
				Log.i("registrotest","3");
				vResult = objTest.setRegistrarTest(registroTest, 'D');
			}


			i=i+1;

		}

		return vResult;
	}


	/**
	 *
	 * @return the set of questions and answers from a test
	 */
	private ArrayList<RegistroTest> GetListaRegistroTest()
	{
		ArrayList<RegistroTest> Resul = new ArrayList<RegistroTest>();
		for(int j=0; j<idmateriaSaved.length;j++)
		{


			RegistroTest item = new RegistroTest();
			item.setIdEstudiante(IdEstudianteG);
			item.setIdMateria(Integer.parseInt(idmateriaSaved[j]));
			item.setIdTest(Integer.parseInt(idtestSaved[j]));
			item.setIdPregunta(Integer.parseInt(idpreguntaSaved[j]));
			item.setOpcion(Integer.parseInt(opcionSaved[j]));

			Resul.add(item);


		}

		for (RegistroTest registroEncuesta : Resul) {
			Log.i("Opciones", String.valueOf(registroEncuesta.getOpcion()));
			Log.i("Pregunta", String.valueOf(registroEncuesta.getIdPregunta()));
		}

		return Resul;
	}

	/**
	 * Mark the test
	 */
	private class TareaCalificarTest extends AsyncTask<String, Integer, Boolean>{

		ProgressDialog pDialog;
		boolean respuesta;
		@Override
		protected Boolean doInBackground(String... params) {
			if (!isCancelled())
			{
				objTest = new boTest();
				respuesta = objTest.CalificarTest(Integer.parseInt(idmateriaSaved[0]),Integer.parseInt(idtestSaved[0]),IdEstudianteG);
				if (respuesta)
					return true;
				else
					return false;


			}
			else return false;
		}


		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(StudentSubjectActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Saving the test...");
			pDialog.setCancelable(false);
			pDialog.setMax(100);
			pDialog.show();

		}


		@Override
		protected void onPostExecute(Boolean result) {

			pDialog.dismiss();

			if (result)
			{

				idtestSaved = new String[]{};
				idmateriaSaved= new String[]{};
				idpreguntaSaved= new String[]{};
				opcionSaved= new String[]{};



				cont = cont + 1;

				if (cont < idtestHeader.length){

					IdTestG = Integer.parseInt(idtestHeader[cont]);
					IdMateriaG = Integer.parseInt(idmateriaHeader[cont]);

					mTaskSavedTests = new TareaGetSavedTests();
					mTaskSavedTests.execute();
				} else {

					for (int i=0 ; i<idtestHeader.length;i++){
						db.deleteTest(Integer.parseInt(idtestHeader[i]), Integer.parseInt(idmateriaHeader[i]));
					}

					Toast.makeText(getApplicationContext(), "Saved tests successfully sent", Toast.LENGTH_SHORT).show();
				}




			}

			else {

				Toast toast = Toast.makeText(getApplicationContext(), "Sending your saved tests failed", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			tct = null;
		}

	}


	public void onBackPressed() {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentSubjectActivity.this);
		builder1.setMessage("Log out?");
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

						Intent i = new Intent(StudentSubjectActivity.this, LoginActivity.class);
						startActivity(i);
						StudentSubjectActivity.this.finish();


						dialog.cancel();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


		if (ttp != null)
			ttp.cancel(true);
		if (tct != null)
			tct.cancel(true);
		if (mTask != null)
			mTask.cancel(true);
		if (mTaskSavedTests != null)
			mTaskSavedTests.cancel(true);


	}

}
