
package com.example.appnfc.nfc_reader;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.example.appnfc.R;
import com.example.appnfc.nfc_reader.record.ParsedNdefRecord;
import com.example.appnfc.nfc_reader.record.SmartPoster;
import com.example.appnfc.nfc_reader.record.TextRecord;
import com.example.appnfc.nfc_reader.record.UriRecord;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
            	elements.add(new ParsedNdefRecord() {
					@Override
					public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
				        TextView text = (TextView) inflater.inflate(R.layout.tag_text, parent, false);
				        try {
							text.setText(new String( decompressByteArray(record.getPayload())));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        return text;
					}
            		
            	});
            }
        }
        return elements;
    }
    

    public static byte[] decompressByteArray(byte[] bytes) throws UnsupportedEncodingException{
        
        
        // Decode the bytes into a String        
        ByteArrayOutputStream baos = null;
        Inflater iflr = new Inflater();
        iflr.setInput(bytes);
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!iflr.finished()){
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){
        	ex.printStackTrace();
             
        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){
            	ex.printStackTrace();
            	
            }
        }
         
        
        
        return baos.toByteArray();
    }
    
}
