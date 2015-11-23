package com.example.appnfc;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boTest;
import com.example.appnfc.info.TestList;
import com.example.appnfc.list.LazyAdapterStudentTest;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentTestListActivity extends Activity {

	TestList[] vResult =null;
	static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_TIME = "time";


	ListView list;
	LazyAdapterStudentTest adapter;

	private ProgressDialog pDialog;

	private  TareaGetTestList mTask = null;


	private int ROWS;
	private String idmateria[] = {};
	private String idtest[] = {};
	private String titulo[] = {};
	private String descripcion[] = {};
	private String tiempo[] = {};

	private int idestudianteG;
	private int idmateriaG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_student_test_list);

		idmateriaG = getIntent().getExtras().getInt("idmateria");
		idestudianteG = getIntent().getExtras().getInt("idestudiante");

		mTask = new TareaGetTestList();
		mTask.execute();
	}



	public void CargarListaTest(){

		ArrayList<HashMap<String, String>> ListaTest = new ArrayList<HashMap<String, String>>();


		for (int i = 0; i < ROWS; i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_ID, idtest[i]);
			map.put(KEY_TITLE, titulo[i] );
			map.put(KEY_ARTIST,descripcion[i]);
			map.put(KEY_TIME,tiempo[i]);


			ListaTest.add(map);
		}



		list=(ListView)findViewById(R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter=new LazyAdapterStudentTest(this, ListaTest);


		list.setAdapter(adapter);

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, final long id) {


				AlertDialog.Builder builder1 = new AlertDialog.Builder(StudentTestListActivity.this);
				builder1.setTitle("Do you wish to start the Test?");
				builder1.setMessage("This test have a duration of " + tiempo[(int) id] + " minutes");
				builder1.setCancelable(true);
				builder1.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id2) {

								dialog.cancel();
							}
						});
				builder1.setNegativeButton("Start",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id2) {

								Intent i = new Intent(StudentTestListActivity.this, TestActivity.class);
								i.putExtra("idestudiante", idestudianteG);
								i.putExtra("idmateria", Integer.parseInt(idmateria[(int) id]));
								i.putExtra("tiempo",Integer.parseInt(tiempo[(int) id]));
								i.putExtra("titulo",titulo[(int)id]);
								i.putExtra("idtest",Integer.parseInt(idtest[(int) id]));
								startActivity(i);
								StudentTestListActivity.this.finish();


								dialog.cancel();
							}
						});

				AlertDialog alert11 = builder1.create();
				alert11.show();




			}
		});





	}



	private class TareaGetTestList extends AsyncTask<Integer, Boolean , Boolean >{



		protected Boolean doInBackground(Integer... params) {

			try
			{


				boTest objTestList = new boTest();

				vResult = objTestList.GetTestxMateria(idmateriaG,"A",idestudianteG);


				if ( vResult != null) {

					ROWS = vResult.length;

					if (ROWS > 0){

						idmateria = new String[ROWS];
						idtest = new String[ROWS];
						titulo = new String[ROWS];
						descripcion = new String[ROWS];
						tiempo = new String[ROWS];


						for (int i = 0; i < ROWS; i++) {
							idmateria[i] = String.valueOf(vResult[i].getIdMateria());
							idtest[i] = String.valueOf(vResult[i].getIdTest());
							titulo[i] = vResult[i].getTitulo();
							descripcion[i] = vResult[i].getDescripcion();
							tiempo[i] = String.valueOf(vResult[i].getTiempo());
						}

						return true;

					} else  //if rows = 0
						return false;

				}

				else{

					return false;
				}


			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(StudentTestListActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Loading your tests...");
			pDialog.setCancelable(true);
			pDialog.show();

		}



		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();

			if (result)
			{

				CargarListaTest();

			}
			else{

				Toast toast = Toast.makeText(StudentTestListActivity.this, "No tests to complete", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}



	}




}
