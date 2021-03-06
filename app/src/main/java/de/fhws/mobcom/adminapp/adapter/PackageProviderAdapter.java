package de.fhws.mobcom.adminapp.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import de.fhws.mobcom.adminapp.model.Package;

/**
 * Created by kanga on 01.03.2018.
 */

public class PackageProviderAdapter {

    private static final String TAG = PackageProviderAdapter.class.getSimpleName();
    private static final Uri CONTENT_URL = Uri.parse( "content://de.fhws.mobcom.adminapp.PackageProvider/apps" );

    private Context mContext;
    private ContentResolver mContentResolver;
    private ArrayList<Package> mPackages;

    public PackageProviderAdapter( Context context ){
        mContext = context;
        mContentResolver = context.getContentResolver();

        init();
    }

    private void init(){
        // reset current packages
        mPackages = new ArrayList<Package>();

        String[] projection = { "id", "name", "label" };
        Cursor cursor = mContentResolver.query( CONTENT_URL, projection, null, null, null );

        if( !cursor.moveToFirst() )
            return;

        do {

            String id = cursor.getString( cursor.getColumnIndex( "id" ) );
            String name = cursor.getString( cursor.getColumnIndex( "name" ) );
            String label = cursor.getString( cursor.getColumnIndex( "label" ) );

            Package pack = new Package( id, name, label, null );
            mPackages.add( pack );

        } while( cursor.moveToNext() );

        //printCurrentPackages();
    }

    private void printCurrentPackages(){
        int max = mPackages.size();
        for( int i = 0 ; i < max ; i++ ){
            Package cur = mPackages.get( i );
            Log.d( TAG, "Name: " + cur.mName + ", Label: " + cur.mLabel );
        }
    }

    public boolean has( Package pack ){
        return has( pack.mName );
    }

    public boolean has( String name ){
        int max = mPackages.size();
        for( int i = 0 ; i < max ; i++ ){
            Package cur = mPackages.get( i );
            if( cur.mName.equals( name ) )
                return true;
        }
        return false;
    }

    public ArrayList<Package> getAll(){
        return mPackages;
    }

    public Package getByName( String name ){
        int max = mPackages.size();
        for( int i = 0 ; i < max ; i++ ){
            Package cur = mPackages.get( i );
            if( cur.mName.equals( name ) )
                return cur;
        }
        return null;
    }

    public void insert( Package pack ){
        if( has( pack ) )
            return;

        Log.d( TAG, "Inserting..." );

        ContentValues values = new ContentValues();
        values.put( "name", pack.mName );
        values.put( "label", pack.mLabel );

        mContentResolver.insert( CONTENT_URL, values );

        init();
    }

    public void delete( String name ){
        if( !has( name ) )
            return;

        Package toDelete = getByName( name );

        String selection = "id = ?";
        String[] selectionArgs = { toDelete.mId };

        mContentResolver.delete( CONTENT_URL, selection, selectionArgs );

        init();
    }
}
