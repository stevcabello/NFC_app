
package com.example.appnfc.nfc_reader.record;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import android.app.Activity;
import android.nfc.NdefRecord;
import android.util.Base64;
//import org.apache.axis.encoding.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appnfc.Global;
import com.example.appnfc.R;
import com.google.common.base.Preconditions;

/**
 * An NFC Text Record
 */
public class TextRecord implements ParsedNdefRecord {

    /** ISO/IANA language code */
    private final String mLanguageCode;

    private final String mText;

    private TextRecord(String languageCode, String text) {
   
        mLanguageCode = Preconditions.checkNotNull(languageCode);
        mText = Preconditions.checkNotNull(text);
    }

    public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
        TextView text = (TextView) inflater.inflate(R.layout.tag_text, parent, false);
        text.setText(mText);
        return text;
    }

    public String getText() {
        return mText;
    }

    /**
     * Returns the ISO/IANA language code associated with this text element.
     */
    public String getLanguageCode() {
        return mLanguageCode;
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


    // TODO: deal with text fields which span multiple NdefRecords
    public static TextRecord parse(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        try {
        	
            byte[] payload = decompressByteArray(record.getPayload());
            Log.i("pp2", new String(payload));
        	//byte[] payload = record.getPayload();
        	
        	
            /*
             * payload[0] contains the "Status Byte Encodings" field, per the
             * NFC Forum "Text Record Type Definition" section 3.2.1.
             *
             * bit7 is the Text Encoding Field.
             *
             * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
             * The text is encoded in UTF16
             *
             * Bit_6 is reserved for future use and must be set to zero.
             *
             * Bits 5 to 0 are the length of the IANA language code.
             */
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            String text =
                new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            
            Global.Test = text;

            
            return new TextRecord(languageCode, text);
        } catch (UnsupportedEncodingException e) {
            // should never happen unless we get a malformed tag.
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
