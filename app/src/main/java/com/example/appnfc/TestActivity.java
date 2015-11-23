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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnfc.businessobject.boTest;
import com.example.appnfc.info.OpcionesMultiples;
import com.example.appnfc.info.RegistroTest;
import com.example.appnfc.info.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;

public class TestActivity extends Activity {

	TableLayout tlTest;
	Test[] vResult;
	boTest objTest = null;
	LinearLayout trPreguntas;
	TableRow trRespuestas;
	TableRow trSubmit;
	TextView tvPre;
	RadioGroup rgOpcB;
	public int layout;


	ProgressDialog pDialog;

	TareaTest te;
	TareaRegTest ttp;
	TareaCalificarTest tct;


	int idestudianteG;
	int idmateriaG;
	int tiempoG;
	String titulotestG;
	int idtestG;

	TextView tiempo;
	TextView titulo;

	boolean testfinxtiempo = false;

	NfcAdapter adapter;
	PendingIntent pendingIntent;
	IntentFilter writeTagFilters[];
	boolean writeMode;
	Tag mytag;
	Context ctx;

	CountDownTimer cT;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_test);

		tlTest = (TableLayout) findViewById(R.id.tlTest);

		idestudianteG = getIntent().getExtras().getInt("idestudiante");
		idmateriaG = getIntent().getExtras().getInt("idmateria");
		tiempoG = getIntent().getExtras().getInt("tiempo");
		titulotestG = getIntent().getExtras().getString("titulo");
		idtestG = getIntent().getExtras().getInt("idtest");

		pDialog = new ProgressDialog(this);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(true);
		pDialog.setMax(100);
		pDialog.show();


		te = new TareaTest();
		te.execute();

		ctx=this;

		Button btnSaveTestOnTag = (Button)findViewById(R.id.btnSaveTestOnTag);

		btnSaveTestOnTag.setVisibility(View.GONE);

		btnSaveTestOnTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {


				try {
					if(mytag==null){
						Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
					}else{
						write(Global.TestJson,mytag);
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



			}
		});

		adapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
		writeTagFilters = new IntentFilter[] { tagDetected };


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
			Toast.makeText(this, "tag menor a texto... tagsize:" + ndef.getMaxSize() + " - textosize:"+size, Toast.LENGTH_LONG).show();

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
			Toast.makeText(this, this.getString(R.string.ok_detection) + mytag.toString(), Toast.LENGTH_LONG ).show();
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
		adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
	}

	private void WriteModeOff(){
		writeMode = false;
		adapter.disableForegroundDispatch(this);
	}




	//Set the questions and answers (set the test)
	private void SetPreguntasRespuestas()
	{

		titulo = (TextView) findViewById(R.id.tvTitulo);
		titulo.setText(titulotestG);


		cT =  new CountDownTimer(tiempoG*60*1000, 1000) {

			public void onTick(long millisUntilFinished) {
				tiempo = (TextView) findViewById(R.id.txTiempo);
				String v = String.format("%02d", millisUntilFinished/60000);
				int va = (int)( (millisUntilFinished%60000)/1000);
				tiempo.setText("Remaining time: " +v+":"+String.format("%02d",va));
			}

			public void onFinish() {
				tiempo.setText("Time over");

				testfinxtiempo = true;

				ttp = new TareaRegTest();
				ttp.execute();


			}
		};
		cT.start();



		if (this == null)
			Log.i("Actividad", "Nula");
		trSubmit = new TableRow(this);
		trSubmit.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		trSubmit.setGravity(Gravity.CENTER_HORIZONTAL);
		for (int i=0;i<vResult.length;i++)
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
			tvPre.setText(String.valueOf(i+1) + ". " + vResult[i].getPregunta());
			//float dimension1 = getResources().getDimension(R.dimen.tamano);
			//tvPre.setTextSize(dimension1);


			Log.i("TamanioTextoPreguntas", String.valueOf(tvPre.getTextSize()));
			tvPre.setId((1*10)+ vResult[i].getIdPregunta());


			trPreguntas.addView(tvPre,params);

			trRespuestas.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			trRespuestas.setOrientation(TableLayout.HORIZONTAL);
			trRespuestas.setGravity(Gravity.CENTER);



			final float scale = this.getResources().getDisplayMetrics().density;
			Log.i("Densidad de pantalla", String.valueOf(scale));

			//Opciones de respuesta tipo multiple cerradas, una a la vez

			trRespuestas.setOrientation(TableLayout.HORIZONTAL);
			TableRow.LayoutParams p2 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			p2.gravity = Gravity.CENTER;
			p2.setMargins(0, margintop/2, 0, margintop);


			List<OpcionesMultiples> tmp = vResult[i].getOpcMultiples();

			rgOpcB = new RadioGroup(this);
			rgOpcB.setGravity(Gravity.CENTER_HORIZONTAL);
			rgOpcB.setOrientation(TableLayout.HORIZONTAL);



			for (OpcionesMultiples opcionesMultiples : tmp) {

				RadioButton a = new RadioButton(this);
				a.setText(opcionesMultiples.getRespuesta());
				a.setTextColor(Color.WHITE);
				a.setButtonDrawable(R.drawable.radiobuttonselector);
				a.setId((((5*10)+vResult[i].getIdPregunta())*10) + opcionesMultiples.getIdRespuesta());

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





		float dimensionp = getResources().getDimension(R.dimen.tamano);

		Button btnGuardar = new Button(this);
		btnGuardar.setText("Save");

		btnGuardar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ttp = new TareaRegTest();
				ttp.execute();

			}
		});


		TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		int margintop = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
		params.setMargins(0, 2*margintop, 0, margintop);


		btnGuardar.setGravity(Gravity.CENTER_HORIZONTAL);
		btnGuardar.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		trSubmit.addView(btnGuardar,params);
		tlTest.addView(trSubmit);





	}

	private class TareaTest extends AsyncTask<String, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {

			if (!isCancelled())
			{
				objTest = new boTest();
				vResult = objTest.GetPlantillaTest(idestudianteG,idmateriaG, idtestG);
				if (vResult != null)
					return true;
				else
					return false;

			}
			else
				return false;
		}

		@Override
		protected void onPreExecute() {



		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled())
			{
				if (result)
				{
					//tlTest.removeAllViews();
					pDialog.dismiss();

					if (vResult.length > 1)
						SetPreguntasRespuestas();
					else{
						//SetBusquedaTest();
						Toast.makeText(getApplicationContext(), "No test to complete", Toast.LENGTH_LONG).show();

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

	/**
	 * Save a test
	 */
	private class TareaRegTest extends AsyncTask<String, Integer, Boolean>{


		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
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

			pDialog = new ProgressDialog(TestActivity.this);
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
				tct = new TareaCalificarTest();
				tct.execute();

			}

			else {

				Toast toast = Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT);
				toast.show();
			}
			ttp = null;
		}

	}


	private String TareaRegistroTest()
	{
		String vResult = "";
		String tmp = "";
		int i=0;
		int cPregIncompletas = 0;
		boTest objTest = new boTest();
		ArrayList<RegistroTest> Temporal = GetListaRegistroTest();


		for (RegistroTest registroTest : Temporal) {
			Log.i("Opc. Contestadas", String.valueOf(registroTest.getOpcion()));
			if (registroTest.getOpcion() == 0 && !testfinxtiempo)
				cPregIncompletas++;
		}

		Log.i("Pregunta incompletas", String.valueOf(cPregIncompletas));
		if (cPregIncompletas > 0)
			vResult = "";

		else { //If all questions were answered

			for (RegistroTest registroTest : Temporal) {


				if (i==0)
				{

					vResult = objTest.setRegistrarTest(registroTest, 'C');
					if (vResult.equals("ok"))
					{
						vResult = objTest.setRegistrarTest(registroTest, 'D');
					}
				}
				else{
					vResult = objTest.setRegistrarTest(registroTest, 'D');
				}


				i=i+1;

			}



		}



		return vResult;
	}



	private ArrayList<RegistroTest> GetListaRegistroTest()
	{
		ArrayList<RegistroTest> Resul = new ArrayList<RegistroTest>();
		boolean flag=false;
		for(int j=0; j<vResult.length;j++)
		{
			Log.i("test1",String.valueOf(vResult[j].getIdMateria()));
			Log.i("test2",String.valueOf(idestudianteG));
			Log.i("test3",String.valueOf(idmateriaG));
			Log.i("test4",String.valueOf(vResult[j].getIdTest()));

			RegistroTest item = new RegistroTest();
			item.setIdEstudiante(idestudianteG);
			item.setIdMateria(vResult[j].getIdMateria());
			item.setIdTest(vResult[j].getIdTest());
			item.setIdPregunta(vResult[j].getIdPregunta());



			flag=true;
			for (OpcionesMultiples registroopc : vResult[j].getOpcMultiples()) {
				RadioButton r = (RadioButton) tlTest.findViewById((((5*10)+vResult[j].getIdPregunta())*10) + registroopc.getIdRespuesta());
				if (r.isChecked())
					item.setOpcion(r.getId() - (((5*10)+vResult[j].getIdPregunta())*10));
			}




			if (flag)
				Resul.add(item);


		}

		for (RegistroTest registroEncuesta : Resul) {
			Log.i("Opciones", String.valueOf(registroEncuesta.getOpcion()));
			Log.i("Pregunta", String.valueOf(registroEncuesta.getIdPregunta()));
		}

		return Resul;
	}




	private void SetBusquedaTest()
	{
		if (this == null)
			Log.i("Actividad", "Nula");
		trSubmit = new TableRow(this);
		trSubmit.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		trSubmit.setGravity(Gravity.CENTER_HORIZONTAL);


		Button btnBuscar = new Button(this);
		btnBuscar.setText("Update");

		btnBuscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				te = new TareaTest();
				te.execute();

			}
		});


		TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		int margintop = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
		params.setMargins(0, 2*margintop, 0, margintop);


		btnBuscar.setGravity(Gravity.CENTER_HORIZONTAL);
		btnBuscar.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		trSubmit.addView(btnBuscar,params);
		tlTest.addView(trSubmit);


	}


	private class TareaCalificarTest extends AsyncTask<String, Integer, Boolean>{

		ProgressDialog pDialog;
		boolean respuesta;
		@Override
		protected Boolean doInBackground(String... params) {
			if (!isCancelled())
			{
				objTest = new boTest();
				respuesta = objTest.CalificarTest(idmateriaG,idtestG,idestudianteG);
				if (respuesta)
					return true;
				else
					return false;


			}
			else return false;
		}


		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(TestActivity.this);
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
				Toast.makeText(getApplicationContext(), "Test sent", Toast.LENGTH_SHORT).show();

				Intent i = new Intent(TestActivity.this,StudentTestListActivity.class);
				i.putExtra("idestudiante", idestudianteG);
				i.putExtra("idmateria", idmateriaG);
				startActivity(i);

				TestActivity.this.finish();
			}

			else {

				Toast toast = Toast.makeText(getApplicationContext(), "Marking test failed, please inform to the teacher", Toast.LENGTH_SHORT);
				toast.show();
			}
			tct = null;
		}

	}


	public void onBackPressed() {

		AlertDialog.Builder builder1 = new AlertDialog.Builder(TestActivity.this);
		builder1.setMessage("Do you wish to finish the test?");
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

						testfinxtiempo = true;

						ttp = new TareaRegTest();
						ttp.execute();


						dialog.cancel();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();


	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (cT !=null)
			cT.cancel();
		if (pDialog != null)
			pDialog.dismiss();
		if (te != null)
			te.cancel(true);
		if (ttp != null)
			ttp.cancel(true);
		if (tct != null)
			tct.cancel(true);


	}


}
