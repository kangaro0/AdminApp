package de.fhws.mobcom.adminapp.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhws.mobcom.adminapp.model.Package;

/**
 * Created by kanga on 27.02.2018.
 */

public class PackageHelper {

    public static ArrayList<Package> INSTALLED( Context context ){
        ArrayList<Package> toReturn = new ArrayList<Package>();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages( PackageManager.GET_META_DATA );

        for( PackageInfo curInfo : packages ){
            if( pm.getLaunchIntentForPackage( curInfo.applicationInfo.name ) != null ) {
                Package curPackage = new Package(
                        null,
                        curInfo.packageName,
                        (String) pm.getApplicationLabel(curInfo.applicationInfo),
                        pm.getApplicationIcon(curInfo.applicationInfo)
                );
                toReturn.add(curPackage);
            }
        }

        return toReturn;
    }

    public static ArrayList<Package> LAUNCHABLE( Context context ){
        ArrayList<Package> toReturn = new ArrayList<Package>();

        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent( Intent.ACTION_MAIN, null );
        mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );

        List<ResolveInfo> list = pm.queryIntentActivities( mainIntent, 0 );
        Collections.sort( list, new ResolveInfo.DisplayNameComparator( pm ) );

        for( ResolveInfo rInfo : list ){
            Package curPackage = new Package(
                    null,
                    rInfo.resolvePackageName,
                    rInfo.loadLabel( pm ).toString(),
                    rInfo.loadIcon( pm )
            );
            toReturn.add( curPackage );
        }

        return toReturn;
    }
}
