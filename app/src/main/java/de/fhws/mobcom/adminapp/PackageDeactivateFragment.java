package de.fhws.mobcom.adminapp;

import android.app.ListFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.fhws.mobcom.adminapp.adapter.PackageAdapter;
import de.fhws.mobcom.adminapp.helper.PackageHelper;
import de.fhws.mobcom.adminapp.model.Package;

/**
 * Created by kanga on 06.03.2018.
 */

public class PackageDeactivateFragment extends ListFragment {

    private static final String TAG = PackageDeactivateFragment.class.getSimpleName();

    @Override
    public void onCreate( Bundle savedInstance ){
        super.onCreate( savedInstance );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        renewListAdapter();

        return super.onCreateView( inflater, container, savedInstanceState );
    }

    @Override
    public void onActivityCreated( Bundle savedInstance ){
        super.onActivityCreated( savedInstance );
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onListItemClick( ListView listview, View view, int position, long id ){

        CheckBox appEnabled = ( CheckBox ) view.findViewById( R.id.appChecked );
        appEnabled.setChecked( !appEnabled.isChecked() );

        Package cur = ( Package ) getListAdapter().getItem( position );

        PackageManager pm = getContext().getPackageManager();
        if (cur.mIsHidden) {
            pm.setApplicationEnabledSetting( cur.mName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP );
        } else {
            pm.setApplicationEnabledSetting( cur.mName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP );
        }
        cur.mIsHidden = !cur.mIsHidden;
    }

    public void renewListAdapter(){
        ArrayList<Package> installed = PackageHelper.LAUNCHABLE( getContext() );

        // check all applications if they are activated
        PackageManager pm = getContext().getPackageManager();
        for( int i = 0 ; i < installed.size() ; i++ ){
            Package cur = installed.get( i );

            try {
                ApplicationInfo appInfo = pm.getApplicationInfo( cur.mName, 0 );
                cur.mIsHidden = !appInfo.enabled;
            } catch( PackageManager.NameNotFoundException e ) {
                installed.remove( i );
                continue;
            }
        }

        PackageAdapter adapter = new PackageAdapter( getContext(), installed );
        setListAdapter( adapter );
    }
}
