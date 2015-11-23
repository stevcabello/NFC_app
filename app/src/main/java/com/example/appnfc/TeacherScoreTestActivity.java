package com.example.appnfc;



import java.util.ArrayList;
import java.util.HashMap;
import com.example.appnfc.businessobject.boTest;
import com.example.appnfc.info.TestList;
import com.example.appnfc.list.LazyAdapterScoreTest;
import com.example.appnfc.list.LazyAdapterTest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TeacherScoreTestActivity extends Activity {
	
	TestList[] vResult =null; 
	static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_SCORE = "score";

	
	ListView list;
    LazyAdapterScoreTest adapter;

	private ProgressDialog pDialog;
	
	private  TareaGetScoreTest mTask = null;
	
    private int ROWS;
    private String idmateria[] = {};
    private String idtest[] = {};
	private String titulo[] = {};
	private String descripcion[] = {};
	private String promedio[] = {};
	
	
	private int IdDocenteG;
	private int idmateriaG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_teacher_score_test);
		
		idmateriaG = getIntent().getExtras().getInt("idmateria");
		
		mTask = new TareaGetScoreTest();
		mTask.execute();
		
	}

	
public void CargarListaTest(){
		
		ArrayList<HashMap<String, String>> ListaTest = new ArrayList<HashMap<String, String>>();


		for (int i = 0; i < ROWS; i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_ID, idtest[i]);
			map.put(KEY_TITLE, titulo[i] );
			map.put(KEY_ARTIST,descripcion[i]);
			map.put(KEY_SCORE,promedio[i]);
			
			ListaTest.add(map);
		}
		

		
		list=(ListView)findViewById(R.id.list);
		
		// Getting adapter by passing xml data ArrayList
	    adapter=new LazyAdapterScoreTest(this, ListaTest);  

	    
	    list.setAdapter(adapter);


	}
	
	
	
	private class TareaGetScoreTest extends AsyncTask<Integer, Boolean , Boolean >{
	    

		
		protected Boolean doInBackground(Integer... params) {
	
			try
			{
				
				
			    boTest objTestList = new boTest();
			    
			    vResult = objTestList.GetTestxMateria(idmateriaG,"C",0);
		

			    if ( vResult != null) {
			    	
			    	ROWS = vResult.length;
			    	
			    	if (ROWS > 0) {
			    		idmateria = new String[ROWS];
				    	idtest = new String[ROWS];
				    	titulo = new String[ROWS];
				    	descripcion = new String[ROWS];
				    	promedio = new String[ROWS];
				    	
				    	for (int i = 0; i < ROWS; i++) {
				    		idmateria[i] = String.valueOf(vResult[i].getIdMateria());
				    		idtest[i] = String.valueOf(vResult[i].getIdTest());
				    		titulo[i] = vResult[i].getTitulo();
				    		descripcion[i] = vResult[i].getDescripcion();
				    		promedio[i] = String.valueOf(vResult[i].getPromedio());
				    		
				    	}
				    	
				    	  return true;
			    		
			    	} else return false;
			    	
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
		
			pDialog = new ProgressDialog(TeacherScoreTestActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Cargando sus Tests...");
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
			
				Toast toast = Toast.makeText(TeacherScoreTestActivity.this, "No hay tests disponibles", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
		
	
	
	}
	


}
