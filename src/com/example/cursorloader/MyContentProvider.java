package com.example.cursorloader;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider{

	   public static final String PROVIDER_NAME = "com.example.cursorloader";
	   public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/login");
	   public static final String _ID = "_id";
	   public static final String USERNAME = "username";
	   public static final String PASSWORD = "password";
	   private static final int LOGIN = 1;
	   private static final UriMatcher uriMatcher;
	   static{
	      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	      uriMatcher.addURI(PROVIDER_NAME, "login", LOGIN);
	   }   
	   private SQLiteDatabase loginDB;
	   private static final String DATABASE_NAME = "mydb.db";
	   private static final String DATABASE_TABLE = "login";
	   private static final int DATABASE_VERSION = 1;
	   private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
	         + "username text not null, password text not null);";
	   
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int uriMatches = uriMatcher.match(uri);
		int count = 0;
		
		switch (uriMatches) {
		case LOGIN:
			count = loginDB.delete(DATABASE_TABLE, selection, selectionArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);   
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		int rowID = (int) loginDB.insert(DATABASE_TABLE, "", values);
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		DBHelper dbHelper = new DBHelper(getContext());
	    loginDB = dbHelper.getWritableDatabase();
	    return (loginDB == null)? false:true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		Log.e("query", uri.toString());
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(DATABASE_TABLE);
		
		Cursor cursor = builder.query(loginDB, projection, selection, selectionArgs, null, null, sortOrder);
		
		//---register to watch a content URI for changes---
	      cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int uriMatches = uriMatcher.match(uri);
		int count = 0;
		
		switch (uriMatches) {
		case LOGIN:
			count = loginDB.update(DATABASE_TABLE, values, selection, selectionArgs);
			break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);   
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	static class DBHelper extends SQLiteOpenHelper
	{
		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
