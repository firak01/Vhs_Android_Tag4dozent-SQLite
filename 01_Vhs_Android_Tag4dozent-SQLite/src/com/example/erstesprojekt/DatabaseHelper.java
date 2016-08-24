package com.example.erstesprojekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context) {
		super(context, "erstesProjekt.db", null, 1);
	}

	private static final String SQL_CREATE = "CREATE TABLE person " +
			"(_id INTEGER PRIMARY KEY," +
			"vorname TEXT," +
			"nachname TEXT," +
			"age INTEGER)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Our app is already perfect.		
	}
	
	public void add(Person p) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("vorname", p.getVorname());
		values.put("nachname", p.getNachname());
		values.put("age", p.getAlter());
		
		db.insert("person", null, values);
	}
	
	public void remove(long id) {
		SQLiteDatabase db = getWritableDatabase();
		
		db.delete("person", "_id=?", new String[] {id + ""});
	}
}
