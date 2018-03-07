package de.fhws.mobcom.adminapp;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kanga on 07.03.2018.
 */

public class MainApplication extends Application {

    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTimerTask;
    private boolean mWasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

    @Override
    public void onCreate( ){
        super.onCreate();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged( Configuration config ){
        super.onConfigurationChanged( config );
    }

    public void startActivityTransitionTimer(){
        mActivityTransitionTimer = new Timer();
        mActivityTimerTask = new TimerTask(){
            @Override
            public void run(){
                MainApplication.this.wasInBackground( true );
            }
        };

        mActivityTransitionTimer.schedule( mActivityTimerTask, MAX_ACTIVITY_TRANSITION_TIME_MS );
    }

    public void stopActivityTransitionTimer(){
        if( mActivityTimerTask != null ){
            mActivityTimerTask.cancel();
        }
        if( mActivityTransitionTimer != null ){
            mActivityTransitionTimer.cancel();
        }
        wasInBackground( false );
    }

    public void wasInBackground( boolean bool ){
        mWasInBackground = bool;
    }

    public boolean wasInBackground(){
        return mWasInBackground;
    }
}
