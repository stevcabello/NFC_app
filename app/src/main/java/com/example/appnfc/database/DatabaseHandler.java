package com.example.appnfc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "AppNFCDB";

	// Contacts table name

	private static final String TABLE_DETAIL_TESTS = "TestRegistroDetalle";

	// Contacts Table Columns names
	private static final String KEY_TEST = "idtest";
	private static final String KEY_MATERIA = "idmateria";
	private static final String KEY_PREGUNTA = "idpregunta";
	private static final String KEY_OPCION = "opcion";
	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_TESTS_DETAIL_TABLE = "CREATE TABLE " + TABLE_DETAIL_TESTS + "("
				+ KEY_TEST + " INTEGER,"
				+ KEY_MATERIA + " INTEGER,"
				+ KEY_PREGUNTA + " INTEGER,"
				+ KEY_OPCION + " INTEGER" + ")";
		db.execSQL(CREATE_TESTS_DETAIL_TABLE);
		
		
	}

	

	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL_TESTS);


		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addTestsDetails(int idtest, int idmateria, int idpregunta, int opcion) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TEST, String.valueOf(idtest)); 
		values.put(KEY_MATERIA, String.valueOf(idmateria)); 
		values.put(KEY_PREGUNTA, String.valueOf(idpregunta)); 
		values.put(KEY_OPCION, String.valueOf(opcion)); 

		// Inserting Row
		db.insert(TABLE_DETAIL_TESTS, null, values);
		db.close(); // Closing database connection
	}


	


		public ArrayList<String> getTestDetail(int idtest, int idmateria) {

			SQLiteDatabase db = this.getReadableDatabase();
			
			 Cursor cur = db.rawQuery("Select * from TestRegistroDetalle where idtest = ? and idmateria = ?",
						new String[] { String.valueOf(idtest) , String.valueOf(idmateria) });
			    ArrayList<String> array = new ArrayList<String>();
			    while (cur.moveToNext()) {
			        String idtestS = cur.getString(cur.getColumnIndex("idtest"));
			        array.add(idtestS);
			        String idmateriaS = cur.getString(cur.getColumnIndex("idmateria"));
			        array.add(idmateriaS);
			        String idpreguntaS = cur.getString(cur.getColumnIndex("idpregunta"));
			        array.add(idpreguntaS);
			        String opcionS = cur.getString(cur.getColumnIndex("opcion"));
			        array.add(opcionS);

			    }
			    return array;

		}

		public ArrayList<String> getTestHeader() {

			SQLiteDatabase db = this.getReadableDatabase();
			
			 Cursor cur = db.rawQuery("Select DISTINCT idtest, idmateria from TestRegistroDetalle",
						new String[] {});
			    ArrayList<String> array = new ArrayList<String>();
			    while (cur.moveToNext()) {
			        String idtestS = cur.getString(cur.getColumnIndex("idtest"));
			        array.add(idtestS);
			        String idmateriaS = cur.getString(cur.getColumnIndex("idmateria"));
			        array.add(idmateriaS);
			    }
			    return array;

		}
		
		// Deleting single contact
	public void deleteTest(int idtest, int idmateria) {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_DETAIL_TESTS, KEY_TEST + " = ? AND " + KEY_MATERIA + " = ?",
					new String[] { String.valueOf(idtest) , String.valueOf(idmateria) });
			db.close();
		}


}

