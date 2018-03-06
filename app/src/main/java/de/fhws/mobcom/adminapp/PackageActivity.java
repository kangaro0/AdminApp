package de.fhws.mobcom.adminapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PackageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        FragmentManager fm = getFragmentManager();

        if( intent.getStringExtra( "Fragment").equals( "Hide" ) ){
            PackageHideFragment fragment = new PackageHideFragment();
            fm.beginTransaction().replace( R.id.activity_package, fragment ).commit();
        } else {
            PackageDeactivateFragment fragment = new PackageDeactivateFragment();
            fm.beginTransaction().replace( R.id.activity_package, fragment ).commit();
        }
    }


}
