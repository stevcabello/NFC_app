
package com.example.appnfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.example.appnfc.database.DatabaseHandler;
import com.example.appnfc.info.OpcionesMultiples;
import com.example.appnfc.info.RegistroTest;
import com.example.appnfc.info.Test;
import com.example.appnfc.nfc_reader.NdefMessageParser;
import com.example.appnfc.nfc_reader.record.ParsedNdefRecord;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device just discovered.
 */
public class TagViewer extends Activity {

    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
    private LinearLayout mTagContent;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private AlertDialog mDialog;
    
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
	
	TextView tiempo;
	TextView titulo;

	
	boolean testfinxtiempo = false;
	
	CountDownTimer cT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tag_viewer);
        
        AlertDialog.Builder builder1 = new AlertDialog.Builder(TagViewer.this);
        builder1.setMessage("Do you wish to start the test saved in the Tag?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
            	
            	TagViewer.this.finish();
            	
                dialog.cancel();
            }
        });
        builder1.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id2) {
            	
            	mTagContent = (LinearLayout) findViewById(R.id.list);
                resolveIntent(getIntent());

                mDialog = new AlertDialog.Builder(TagViewer.this).setNeutralButton("Ok", null).create();

                tlTest = (TableLayout) findViewById(R.id.tlTest);
                
                
                mAdapter = NfcAdapter.getDefaultAdapter(TagViewer.this);
                if (mAdapter == null) {
                    showMessage(R.string.error, R.string.no_nfc);
                    finish();
                    return;
                }

                       	
                 mPendingIntent = PendingIntent.getActivity(TagViewer.this, 0,
                      new Intent(TagViewer.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                 mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                      "Message from NFC Reader :-)", Locale.ENGLISH, true) });


        		
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        
        
            	
        		
        		
        
                
        
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }
    
   
    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
       
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            
        	mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
             
            
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
            }
            // Setup the views
            buildTagViews(msgs);
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                case MifareClassic.TYPE_CLASSIC:
                    type = "Classic";
                    break;
                case MifareClassic.TYPE_PLUS:
                    type = "Plus";
                    break;
                case MifareClassic.TYPE_PRO:
                    type = "Pro";
                    break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                case MifareUltralight.TYPE_ULTRALIGHT:
                    type = "Ultralight";
                    break;
                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    type = "Ultralight C";
                    break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;

        // Parse the first message in the list
        // Build views for all of the sub records
        Date now = new Date();
        msgs[0].getRecords()[0].getPayload();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            /*TextView timeView = new TextView(this);
            timeView.setText(TIME_FORMAT.format(now));
            content.addView(timeView, 0);
            ParsedNdefRecord record = records.get(i);
            
            content.addView(record.getView(this, inflater, content, i), 1 + i);
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);*/
            
        }
        
        
        
        te = new TareaTest();
		te.execute();
		
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    
    
    private void SetPreguntasRespuestas()
	{
    	 titulo = (TextView) findViewById(R.id.tvTitulo);
		 titulo.setText(Global.Test.split("%")[1]);
		 
		
		 cT =  new CountDownTimer(Integer.parseInt(Global.Test.split("%")[2])*60*1000, 1000) {

	         public void onTick(long millisUntilFinished) {
	        	 tiempo = (TextView) findViewById(R.id.txTiempo);
	             String v = String.format("%02d", millisUntilFinished/60000);
	             int va = (int)( (millisUntilFinished%60000)/1000);
	             tiempo.setText("Remaining time: " +v+":"+String.format("%02d",va));
	         } 

	         public void onFinish() {
	             tiempo.setText("Timeout");
	             
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
				vResult = objTest.GetPlantillaTest2(Global.Test.split("%")[0]);
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
			
			pDialog = new ProgressDialog(TagViewer.this);
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
					//tlTest.removeAllViews();
					pDialog.dismiss();

					if (vResult.length > 1) 
						SetPreguntasRespuestas();
					else{
					  //  SetBusquedaTest();	
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
    
    
    private class TareaRegTest extends AsyncTask<String, Integer, Boolean>{
		
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
			
			pDialog = new ProgressDialog(TagViewer.this);
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

				Toast.makeText(getApplicationContext(), "Test successfully saved", Toast.LENGTH_SHORT).show();
				
				TagViewer.this.finish();

			}
			
			else {
				
				Toast toast = Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT);
				toast.show();
			}
			ttp = null;
		}
		
	}


    /**
     * Save test
     * @return ok if successfully saved
     */
	private String TareaRegistroTest()
	{
		String vResult = "";
		String tmp = "";
		int i=0;
		int cPregIncompletas = 0;
		boTest objTest = new boTest();
		DatabaseHandler db;
		ArrayList<RegistroTest> Temporal = GetListaRegistroTest();
		
		db = new DatabaseHandler(TagViewer.this);
		
		
		for (RegistroTest registroTest : Temporal) {
			Log.i("Opc. Contestadas", String.valueOf(registroTest.getOpcion()));
			if (registroTest.getOpcion() == 0 && !testfinxtiempo)
				cPregIncompletas++;
		}
		
		Log.i("Pregunta incompletas", String.valueOf(cPregIncompletas));
		if (cPregIncompletas > 0)
			vResult = "";
		
		else { //if all the questions were answered
		    
			for (RegistroTest registroTest : Temporal) {
             
				 Log.i("idtestAAA", String.valueOf(registroTest.getIdTest()));
				 Log.i("idmateriaAAA", String.valueOf(registroTest.getIdMateria()));
				 db.addTestsDetails(registroTest.getIdTest(), registroTest.getIdMateria(), registroTest.getIdPregunta(), registroTest.getOpcion());
				
			}
			
			vResult = "ok";		
		}

	
		return vResult;
	}
	
	
	
	private ArrayList<RegistroTest> GetListaRegistroTest()
	{
		ArrayList<RegistroTest> Resul = new ArrayList<RegistroTest>();
		boolean flag=false;
		for(int j=0; j<vResult.length;j++)
		{

			RegistroTest item = new RegistroTest();
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

	
	
	public void onBackPressed() {
        
		AlertDialog.Builder builder1 = new AlertDialog.Builder(TagViewer.this);
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
		
		if (cT != null)
			cT.cancel();
		if (pDialog != null)
			 pDialog.dismiss();
		if (te != null)
			te.cancel(true);
		if (ttp != null)
			ttp.cancel(true);

				
	}
    
}
