package de.fhws.mobcom.adminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by kanga on 02.03.2018.
 */

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
            String action = intent.getAction();
            switch( action ){
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    if( preferences.getBoolean( getString( R.string.KEY_DISABLE_WIFI ), false ) ) {
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        if (wifiManager.isWifiEnabled())
                            wifiManager.setWifiEnabled(false);
                    }
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    if( preferences.getBoolean( getString( R.string.KEY_DISABLE_BLUETOOTH ), false ) ) {
                        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                        if (bluetoothManager.getAdapter().isEnabled())
                            bluetoothManager.getAdapter().disable();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );
        setContentView( R.layout.activity_main );

        initReceiver();
        initPasswordProtection();

        getFragmentManager().beginTransaction().replace( R.id.activity_main, new MainPreferenceFragment() ).commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        showPasswordDialog().show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver( mBroadcastReceiver );
    }

    private void initPasswordProtection(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        if( preferences.getBoolean( getString( R.string.KEY_PASSWORD_SET ), false ) ){
            return;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean( getString( R.string.KEY_PASSWORD_SET ), true );
            editor.putString( getString( R.string.KEY_PASSWORD ), "fhws" );
            editor.commit();
        }
    }

    private Dialog showPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Bitte Passwort eingeben:" );

        final EditText editText = new EditText( this );
        editText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );

        builder.setView( editText );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( isPasswordCorrect( editText.getText().toString() ) ){
                    return;
                } else {
                    showPasswordDenied();
                }
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });

        AlertDialog dialog = builder.create();
       return dialog;
    }

    private void showPasswordDenied(){
        AlertDialog.Builder dialog = new AlertDialog.Builder( this );
        dialog.setTitle( "Falsches Passwort" );

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        dialog.show();
    }

    private boolean isPasswordCorrect( String password ){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        if( preferences.getString( getString( R.string.KEY_PASSWORD ), "" ).equals( password ) )
            return true;
        return false;
    }

    private void initReceiver(){
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, wifiFilter);

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, bluetoothFilter);
    }

    private void changeNfcState(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        /*
        if( preferences.getBoolean( getString( R.string.KEY_DISABLE_NFC ), false  ) ) {
            PackageManager pm = getPackageManager();
            pm.setApplicationEnabledSetting( "com.android.nfc", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP );
        } else {
            PackageManager pm = getPackageManager();
            pm.setApplicationEnabledSetting( "com.android.nfc", PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP );
        }
        */
    }

    public static class MainPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        SharedPreferences mPreferences;

        private CheckBoxPreference mWifiDisabled;
        private CheckBoxPreference mBluetoothDisabled;
        private CheckBoxPreference mNfcDisabled;
        private Preference mHiddenApps;
        private EditTextPreference mChangePassword;

        @Override
        public void onCreate( Bundle savedInstance ){
            super.onCreate( savedInstance );

            addPreferencesFromResource( R.xml.preferences );

            // get sharedpreferences
            mPreferences = PreferenceManager.getDefaultSharedPreferences( getActivity().getApplicationContext() );

            mWifiDisabled = ( CheckBoxPreference ) findPreference( getString( R.string.KEY_DISABLE_WIFI ) );
            mWifiDisabled.setOnPreferenceChangeListener( this );
            mWifiDisabled.setOnPreferenceClickListener( this );
            mWifiDisabled.setChecked( mPreferences.getBoolean( getString( R.string.KEY_DISABLE_WIFI ), false ) );

            mBluetoothDisabled = ( CheckBoxPreference ) findPreference( getString( R.string.KEY_DISABLE_BLUETOOTH ) );
            mBluetoothDisabled.setOnPreferenceChangeListener( this );
            mBluetoothDisabled.setOnPreferenceClickListener( this );
            mBluetoothDisabled.setChecked( mPreferences.getBoolean( getString( R.string.KEY_DISABLE_BLUETOOTH ), false ) );

            mNfcDisabled = ( CheckBoxPreference ) findPreference( getString( R.string.KEY_DISABLE_NFC ) );
            mNfcDisabled.setOnPreferenceChangeListener( this );
            mNfcDisabled.setOnPreferenceClickListener( this );
            mNfcDisabled.setChecked( mPreferences.getBoolean( getString( R.string.KEY_DISABLE_NFC ), false ) );

            mHiddenApps = ( Preference ) findPreference( getString( R.string.KEY_HIDDEN_APPS ) );
            mHiddenApps.setOnPreferenceClickListener( this );

            mChangePassword = ( EditTextPreference ) findPreference( getString( R.string.KEY_CHANGE_PASSWORD ) );
            mChangePassword.setOnPreferenceChangeListener( this );
            mChangePassword.setOnPreferenceClickListener( this );
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getActivity().getApplicationContext() );
            SharedPreferences.Editor editor = preferences.edit();

            if( mChangePassword == preference ){
                String newPassword = ( String ) o;
                editor.putString( getString( R.string.KEY_PASSWORD ), newPassword );
            }

            editor.commit();
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            SharedPreferences.Editor editor = mPreferences.edit();

            if( mWifiDisabled == preference ){
                boolean oldValue = mPreferences.getBoolean( getString( R.string.KEY_DISABLE_WIFI ), false );
                mWifiDisabled.setChecked( !oldValue );
                // write update
                editor.putBoolean( getString( R.string.KEY_DISABLE_WIFI ), !oldValue );
                editor.commit();
            } else if( mBluetoothDisabled == preference ){
                boolean oldValue = mPreferences.getBoolean( getString( R.string.KEY_DISABLE_BLUETOOTH ), false );
                mBluetoothDisabled.setChecked( !oldValue );
                editor.putBoolean( getString( R.string.KEY_DISABLE_BLUETOOTH ), !oldValue );
                editor.commit();
            } else if( mNfcDisabled == preference ){
                boolean oldValue = mPreferences.getBoolean( getString( R.string.KEY_DISABLE_NFC ), false );
                mNfcDisabled.setChecked( !oldValue );
                editor.putBoolean( getString( R.string.KEY_DISABLE_NFC ), !oldValue );
                editor.commit();
            } else if( mHiddenApps == preference ){
                // start PackageActivity
                Intent intent = new Intent( getActivity(), PackageActivity.class );
                startActivity( intent );
            } else if( mChangePassword == preference ){

            }

            return true;
        }
    }
}
