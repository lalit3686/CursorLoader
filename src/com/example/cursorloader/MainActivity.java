package com.example.cursorloader;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>{

	private static final int LOADER_ALL_ID = 100;
	private static final int LOADER_BY_ID = 200;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //insertUsingContentProvider("lalit", "poptani");
        //updateUsingContentProvider("lalit", "poptani", "3");
        //deleteUsingContentProvider("3");
        
        // select all values
        getSupportLoaderManager().initLoader(LOADER_ALL_ID, null, this);
        
        // select from _ID
        getSupportLoaderManager().initLoader(LOADER_BY_ID, null, this);
    }
    
    /**
    *
    * Update by _ID
    * 
    **/
    void updateUsingContentProvider(String username, String password, String _id) {
    	ContentValues values = new ContentValues();
        values.put(MyContentProvider.USERNAME, username);
        values.put(MyContentProvider.PASSWORD, username);
        getContentResolver().update(MyContentProvider.CONTENT_URI, values, MyContentProvider._ID+" = ?", new String[]{_id});
    }
    
    /**
     *
     * Delete by _ID
     * 
     **/
    void deleteUsingContentProvider(String _id) {
    	getContentResolver().delete(MyContentProvider.CONTENT_URI, MyContentProvider._ID+" = ?", new String[]{_id});
	}
    
    void insertUsingContentProvider(String username, String password) {
    	ContentValues values = new ContentValues();
        values.put(MyContentProvider.USERNAME, username);
        values.put(MyContentProvider.PASSWORD, password);
        getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
    }
    

	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		if(id == LOADER_ALL_ID)
			return new CursorLoader(this, MyContentProvider.CONTENT_URI, null, null, null, MyContentProvider.USERNAME+" ASC");
		else if(id == LOADER_BY_ID)
			return new CursorLoader(this, MyContentProvider.CONTENT_URI, null, MyContentProvider._ID+" = ?", new String[]{"3"}, null);
		return null;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(cursor.moveToFirst()){
    		do{
    			Log.d("data", cursor.getString(cursor.getColumnIndex(MyContentProvider._ID))+" "+
    					cursor.getString(cursor.getColumnIndex(MyContentProvider.USERNAME))+" "+
    					cursor.getString(cursor.getColumnIndex(MyContentProvider.PASSWORD)));
    		}while(cursor.moveToNext());
    	}
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
	}
}
