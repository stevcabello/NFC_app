package com.example.appnfc;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boDocente;
import com.example.appnfc.info.Materia;
import com.example.appnfc.list.LazyAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherSubjectActivity extends Activity {

	Materia[] vResult =null;
	static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";


	ListView list;
	LazyAdapter adapter;

	private ProgressDialog pDialog;

	private  TareaGetMaterias mTask = null;

	private int ROWS;
	private String idmateria[] = {};
	private String nombremateria[] = {};


	private static int TAKE_PICTURE = 1;

	private String name = "";

	private int IdDocenteG;
	private int flagG;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_subject);

		IdDocenteG = this.getIntent().getExtras().getInt("iddocente");
		flagG = this.getIntent().getExtras().getInt("flag");

		mTask = new TareaGetMaterias();
		mTask.execute();
	}


	/**
	 * Get the list of subjects of a teacher
	 */
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
		adapter=new LazyAdapter(this, Listmaterias);


		list.setAdapter(adapter);


		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				Intent i = null;

				if (flagG == 1 )
					i = new Intent(TeacherSubjectActivity.this, TestListActivity.class);
				else if (flagG == 2)
					i = new Intent(TeacherSubjectActivity.this, TeacherScoreTestActivity.class);

				i.putExtra("iddocente", IdDocenteG);
				i.putExtra("idmateria", Integer.parseInt(idmateria[(int) id]));
				startActivity(i);

			}
		});


	}


	/**
	 * Get the subjects of a teacher
	 */
	private class TareaGetMaterias extends AsyncTask<Integer, Boolean , Boolean >{



		protected Boolean doInBackground(Integer... params) {

			try
			{


				boDocente objMaterias = new boDocente();

				vResult = objMaterias.GetMateriasxDocente(IdDocenteG);


				if ( vResult != null) {

					ROWS = vResult.length;

					if(ROWS > 0) {

						idmateria = new String[ROWS];
						nombremateria = new String[ROWS];

						for (int i = 0; i < ROWS; i++) {
							idmateria[i] = String.valueOf(vResult[i].getId());
							nombremateria[i] = vResult[i].getNombre();
						}

						return true;

					}else return false;

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

			pDialog = new ProgressDialog(TeacherSubjectActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Loading subjects...");
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
				Toast.makeText(TeacherSubjectActivity.this, "No available subjects", Toast.LENGTH_LONG).show();

			}
		}



	}



}
