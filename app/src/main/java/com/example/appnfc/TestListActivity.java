package com.example.appnfc;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boTest;
import com.example.appnfc.info.OpcionesMultiples;
import com.example.appnfc.info.Test;
import com.example.appnfc.info.TestList;
import com.example.appnfc.list.LazyAdapterTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Deflater;

public class TestListActivity extends Activity {

	TestList[] vResult =null;
	static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_ESTADO = "estado";
	public static final String KEY_TIME = "tiempo";


	ListView list;
	LazyAdapterTest adapter;

	private ProgressDialog pDialog;

	private  TareaGetTestList mTask = null;
	private  TareaUpdateTest mUTask = null;


	private int ROWS;
	private String idmateria[] = {};
	private String idtest[] = {};
	private String titulo[] = {};
	private String descripcion[] = {};
	private String estado[] = {};
	private String tiempo[] = {};

	private int idmateriaG;

	boTest objTest = null;
	TareaTest te;
	Test[] vResult2 =null;

	TextView titulo2;

	TableLayout tlTest;

	LinearLayout trPreguntas;
	TableRow trRespuestas;
	TableRow trSubmit;
	TextView tvPre;
	RadioGroup rgOpcB;


	View layout_nfc_test;

	String titulotestG;
	String tiempotestG;
	String idtestG;

	NfcAdapter adapter2;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test_list);

		idmateriaG = getIntent().getExtras().getInt("idmateria");



		mTask = new TareaGetTestList();
		mTask.execute();

		adapter2 = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };

	}



	public void CargarListaTest(){

		ArrayList<HashMap<String, String>> ListaTest = new ArrayList<HashMap<String, String>>();


		for (int i = 0; i < ROWS; i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_ID, idtest[i]);
			map.put(KEY_TITLE, titulo[i] );
			map.put(KEY_ARTIST,descripcion[i]);
			map.put(KEY_ESTADO,estado[i]);
			map.put(KEY_TIME,tiempo[i]);

			ListaTest.add(map);
		}



		list=(ListView)findViewById(R.id.list);

		adapter=new LazyAdapterTest(this, ListaTest);


		list.setAdapter(adapter);


		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, final long id) {

				if (estado[(int)id].equals("A")) {

					AlertDialog.Builder builder1 = new AlertDialog.Builder(TestListActivity.this);
					builder1.setMessage("Do you wish to deactive this test?");
					builder1.setCancelable(true);
					builder1.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id2) {

									dialog.cancel();
								}
							});
					builder1.setNegativeButton("Deactivate",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id2) {

									mUTask = new TareaUpdateTest();
									mUTask.execute(Integer.parseInt(idmateria[(int) id]), Integer.parseInt(idtest[(int)id]),0,0,0);


									dialog.cancel();
								}
							});

					AlertDialog alert11 = builder1.create();
					alert11.show();

				} else {

					AlertDialog.Builder builder1 = new AlertDialog.Builder(TestListActivity.this);
					builder1.setMessage("Do you wish to activate this test?");
					final EditText input = new EditText(TestListActivity.this);
					input.setHint("Test duration time in minutes");
					builder1.setView(input);

					builder1.setCancelable(true);
					builder1.setPositiveButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id2) {

									dialog.cancel();
								}
							});
					builder1.setNegativeButton("Activate",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id2) {

									if  (input.getText().toString().equals("") || input.getText().toString().equals("0") ){

										Toast.makeText(getApplicationContext(), "Please provide a test duration (in minutes)", Toast.LENGTH_SHORT).show();

									}else{

										try {
											mUTask = new TareaUpdateTest();
											mUTask.execute(Integer.parseInt(idmateria[(int) id]), Integer.parseInt(idtest[(int)id]),0,1,Integer.valueOf(input.getText().toString()));
											dialog.cancel();
										} catch(Exception ex) {
											Toast.makeText(getApplicationContext(), "Duration time format is incorrect", Toast.LENGTH_SHORT).show();
										}


									}

								}
							});

					AlertDialog alert11 = builder1.create();
					alert11.show();


				}



			}
		});

		//To send a test to a NFC tag
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, final long id) {

				if (estado[(int)id].equals("A")) {

					titulotestG = titulo[(int)id];
					tiempotestG = tiempo[(int)id];
					idtestG = idtest[(int)id];

					te = new TareaTest();
					te.execute();


				}

				return true;
			}
		});

	}



	private class TareaGetTestList extends AsyncTask<Integer, Boolean , Boolean >{



		protected Boolean doInBackground(Integer... params) {

			try
			{


				boTest objTestList = new boTest();

				vResult = objTestList.GetTestxMateria(idmateriaG,"*",0);


				if ( vResult != null) {

					ROWS = vResult.length;

					if (ROWS > 0) {

						idmateria = new String[ROWS];
						idtest = new String[ROWS];
						titulo = new String[ROWS];
						descripcion = new String[ROWS];
						estado = new String[ROWS];
						tiempo = new String[ROWS];

						for (int i = 0; i < ROWS; i++) {
							idmateria[i] = String.valueOf(vResult[i].getIdMateria());
							idtest[i] = String.valueOf(vResult[i].getIdTest());
							titulo[i] = vResult[i].getTitulo();
							descripcion[i] = vResult[i].getDescripcion();
							estado[i] = vResult[i].getEstado();
							tiempo[i] = String.valueOf(vResult[i].getTiempo());

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

			pDialog = new ProgressDialog(TestListActivity.this);
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
				Toast toast = Toast.makeText(TestListActivity.this, "No test available", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}



	}


	private class TareaUpdateTest extends AsyncTask<Integer, Boolean , Boolean >{

		boolean vR;
		String estado;

		protected Boolean doInBackground(Integer... params) {

			try
			{

				if (params[3].equals(1))
					estado = "A"; // active state ...means visible for the students
				else
					estado = "I"; // inactive state

				boTest objTest = new boTest();
				vR = objTest.ActualizarTest(params[0],params[1],params[2],estado,params[4]);

				return vR;


			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(TestListActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Updating test state...Please wait");
			pDialog.setCancelable(true);
			pDialog.show();

		}



		@Override
		protected void onPostExecute(Boolean result) {
			pDialog.dismiss();

			if (result)
			{

				mTask = new TareaGetTestList();
				mTask.execute();
				Toast.makeText(TestListActivity.this, "Test state updated", Toast.LENGTH_SHORT).show();

			}
			else{
				Toast.makeText(TestListActivity.this, "Test state couldn't be updated", Toast.LENGTH_LONG).show();

			}
		}



	}



	private class TareaTest extends AsyncTask<String, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {

			if (!isCancelled())
			{
				objTest = new boTest();
				vResult2 = objTest.GetPlantillaTest(0,idmateriaG,Integer.parseInt(idtestG)); //the first parameter is the student id, but I'm not using it that's why I place 0
				if (vResult2 != null)
					return true;
				else
					return false;

			}
			else
				return false;
		}

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(TestListActivity.this);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(true);
			pDialog.setMax(100);
			pDialog.show();


		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled())
			{
				if (result)
				{

					pDialog.dismiss();

					if (vResult2.length > 1)
						SetPreguntasRespuestas();
					else{

						Toast.makeText(getApplicationContext(), "No tests to complete", Toast.LENGTH_LONG).show();

					}

				}
				else
				{
					pDialog.dismiss();
					Toast.makeText(getApplicationContext(), "Error " + objTest.ErrorMsg, Toast.LENGTH_LONG).show();
				}
			}
			te = null;
		}

		@Override
		protected void onCancelled() {

			pDialog.dismiss();
			super.onCancelled();
		}

	}


	private void SetPreguntasRespuestas()
	{

		layout_nfc_test = getLayoutInflater().inflate(R.layout.nfc_test_layout, null);
		tlTest = (TableLayout) layout_nfc_test.findViewById(R.id.tlTest);

		ctx=layout_nfc_test.getContext();

		AlertDialog.Builder builder1 = new AlertDialog.Builder(TestListActivity.this);

		builder1.setTitle("");
		builder1.setCancelable(true);
		builder1.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id2) {

						dialog.cancel();
					}
				});
		builder1.setNegativeButton("Transfer test",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id2) {

						try {
							if(mytag==null){
								Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
							}else{
								write(Global.TestJson +"%"+ titulotestG + "%" + tiempotestG,mytag);
								Toast.makeText(ctx, ctx.getString(R.string.ok_writing), Toast.LENGTH_LONG ).show();
							}
						} catch (IOException e) {
							Log.i("Error Formato NFC",Global.TestJson);
							Toast.makeText(ctx, ctx.getString(R.string.error_writing), Toast.LENGTH_LONG ).show();
							e.printStackTrace();
						} catch (FormatException e) {
							Toast.makeText(ctx, ctx.getString(R.string.error_writing) , Toast.LENGTH_LONG ).show();
							e.printStackTrace();
						}


						dialog.cancel();
					}
				});



		titulo2 = (TextView) layout_nfc_test.findViewById(R.id.tvTitulo);
		titulo2.setText(titulotestG);


		if (this == null)
			Log.i("Actividad", "Nula");
		trSubmit = new TableRow(this);
		trSubmit.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		trSubmit.setGravity(Gravity.CENTER_HORIZONTAL);
		for (int i=0;i<vResult2.length;i++)
		{
			trPreguntas = new LinearLayout(this);
			trRespuestas = new TableRow(this);

			trPreguntas.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			trPreguntas.setGravity(Gravity.CENTER_HORIZONTAL);
			tvPre = new TextView(this);
			tvPre.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			tvPre.setGravity(Gravity.CENTER_HORIZONTAL);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margintop = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
			params.setMargins(0, margintop, 0, 0);

			TableRow.LayoutParams params1 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.setMargins(0, margintop/2, 0, margintop);


			tvPre.setHorizontallyScrolling(false);
			tvPre.setSingleLine(false);
			tvPre.setTextColor(Color.WHITE);


			Log.i("Indice", String.valueOf(i));
			tvPre.setText(String.valueOf(i+1) + ". " + vResult2[i].getPregunta());


			Log.i("TamanioTextoPreguntas", String.valueOf(tvPre.getTextSize()));
			tvPre.setId((1*10)+ vResult2[i].getIdPregunta());


			trPreguntas.addView(tvPre,params);

			trRespuestas.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			trRespuestas.setOrientation(TableLayout.HORIZONTAL);
			trRespuestas.setGravity(Gravity.CENTER);



			final float scale = this.getResources().getDisplayMetrics().density;
			Log.i("Densidad de pantalla", String.valueOf(scale));


			trRespuestas.setOrientation(TableLayout.HORIZONTAL);
			TableRow.LayoutParams p2 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			p2.gravity = Gravity.CENTER;
			p2.setMargins(0, margintop/2, 0, margintop);


			List<OpcionesMultiples> tmp = vResult2[i].getOpcMultiples();

			rgOpcB = new RadioGroup(this);
			rgOpcB.setGravity(Gravity.CENTER_HORIZONTAL);
			rgOpcB.setOrientation(TableLayout.HORIZONTAL);


			for (OpcionesMultiples opcionesMultiples : tmp) {

				RadioButton a = new RadioButton(this);
				a.setText(opcionesMultiples.getRespuesta());
				a.setTextColor(Color.WHITE);
				a.setButtonDrawable(R.drawable.radiobuttonselector);
				a.setId((((5*10)+vResult2[i].getIdPregunta())*10) + opcionesMultiples.getIdRespuesta());

				if (scale <= 1.0)
					a.setPadding(a.getPaddingLeft() + (int) (15.0f * scale + 0.5f), 0, a.getPaddingRight() + (int) (18.0f * scale + 0.5f), 0);
				else if (scale > 1.0 && scale <= 1.5)
					a.setPadding(a.getPaddingLeft() + (int) (4.0f * scale + 0.5f), 0, a.getPaddingRight() + (int) (14.0f * scale + 0.5f) , 0);
				else if (scale > 1.5f)
					a.setPadding(a.getPaddingLeft() + (int) (7.0f * scale + 0.5f), 0, a.getPaddingRight() + (int) (10.0f * scale + 0.5f) , 0);

				rgOpcB.addView(a);

			}



			HorizontalScrollView hsc = new HorizontalScrollView(this);
			hsc.setLayoutParams(p2);
			hsc.setScrollbarFadingEnabled(false);
			hsc.addView(rgOpcB);
			trRespuestas.addView(hsc);



			tlTest.addView(trPreguntas);
			tlTest.addView(trRespuestas);




		}


		builder1.setView(layout_nfc_test);

		AlertDialog alert11 = builder1.create();
		alert11.show();




	}


	private void write(String text, Tag tag) throws IOException, FormatException {


		NdefRecord[] records = { createRecord(text) };
		NdefMessage  message = new NdefMessage(records);
		// Get an instance of Ndef for the tag.
		Ndef ndef = Ndef.get(tag);
		// Enable I/O
		ndef.connect();


		int size = message.toByteArray().length;
		if(ndef.getMaxSize() < size) {

			Toast.makeText(this, "Test data too large for the Tag", Toast.LENGTH_SHORT).show();

		}

		ndef.writeNdefMessage(message);
		// Close the connection
		ndef.close();
	}


	public byte[] compressByteArray(byte[] bytes){

		ByteArrayOutputStream baos = null;
		Deflater dfl = new Deflater();
		dfl.setLevel(Deflater.BEST_COMPRESSION);
		dfl.setInput(bytes);
		dfl.finish();
		baos = new ByteArrayOutputStream();
		byte[] tmp = new byte[4*1024];
		try{
			while(!dfl.finished()){
				int size = dfl.deflate(tmp);
				baos.write(tmp, 0, size);
			}
		} catch (Exception ex){

		} finally {
			try{
				if(baos != null) baos.close();
			} catch(Exception ex){}
		}

		return baos.toByteArray();
	}

	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {

		String lang       = "en";
		byte[] textBytes  = text.getBytes();
		byte[] langBytes  = lang.getBytes("US-ASCII");
		int    langLength = langBytes.length;
		int    textLength = textBytes.length;
		byte[] payload    = new byte[1 + langLength + textLength];

		// set status byte (see NDEF spec for actual bits)
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1,              langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);


		payload = compressByteArray(payload);


		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

		return recordNFC;
	}


	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
			mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Toast.makeText(this, this.getString(R.string.ok_detection), Toast.LENGTH_LONG ).show();
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		WriteModeOff();
	}

	@Override
	public void onResume(){
		super.onResume();
		WriteModeOn();
	}

	private void WriteModeOn(){
		writeMode = true;
		adapter2.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
	}

	private void WriteModeOff(){
		writeMode = false;
		adapter2.disableForegroundDispatch(this);
	}



}
