package de.fhws.mobcom.adminapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.fhws.mobcom.adminapp.adapter.PackageAdapter;
import de.fhws.mobcom.adminapp.adapter.PackageProviderAdapter;
import de.fhws.mobcom.adminapp.helper.PackageHelper;
import de.fhws.mobcom.adminapp.model.Package;

/**
 * Created by kanga on 27.02.2018.
 */

public class PackageHideFragment extends ListFragment {

    private static final String TAG = PackageHideFragment.class.getSimpleName();

    private PackageProviderAdapter mProviderAdapter;

    @Override
    public void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        mProviderAdapter = new PackageProviderAdapter( getContext() );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        renewListAdapter();

        return super.onCreateView( inflater, container, savedInstanceState );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ){
        super.onActivityCreated( savedInstanceState );
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id ){
        Log.d( TAG, "Item clicked.");

        // get package to view
        TextView appName = (TextView) view.findViewById( R.id.appName );
        TextView appLabel = ( TextView ) view.findViewById( R.id.appLabel );
        CheckBox appHidden = ( CheckBox ) view.findViewById( R.id.appChecked );

        // appname
        String name = appName.getText().toString();
        String label = appLabel.getText().toString();

        if( mProviderAdapter.has( name ) ){
            // uncheck
            appHidden.setChecked( false );
            mProviderAdapter.delete( name );
        } else {
            // check
            appHidden.setChecked( true );
            mProviderAdapter.insert( new Package( null, name, label, null ) );
        }
    }

    private void renewListAdapter(){
        ArrayList<Package> packages = PackageHelper.LAUNCHABLE( getContext() );
        ArrayList<Package> hidden = mProviderAdapter.getAll();

        Log.d( TAG, "Installed apps: " + packages.size() );
        Log.d( TAG, "Hidden apps: " + hidden.size() );

        for( Package pack : packages ){
            if( mProviderAdapter.has( pack.mName ) )
                pack.mIsHidden = true;
        }

        PackageAdapter adapter = new PackageAdapter( getContext(), packages );
        setListAdapter( adapter );
    }
}
