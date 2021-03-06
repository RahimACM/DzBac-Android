package com.squalala.dzbac.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Auteur : Fayçal Kaddouri
 * Nom du fichier : GCMUtils.java
 * Date : 26 nov. 2014
 * 
 */
public class GCMUtils {
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCMUtils";
	
	/** 
	 * Gets the current registration ID for application on GCM service. 
	 * <p> 
	 * If result is empty, the app needs to register. 
	 * 
	 * @return registration ID, or empty string if there is no existing 
	 *         registration ID. 
	 */ 
    public static String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return ""; 
	    } 
	    // Check if app was updated; if so, it must clear the registration ID 
	    // since the existing regID is not guaranteed to work with the new 
	    // app version. 
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return ""; 
	    } 
	    return registrationId;
	} 
	
	/** 
	 * @return Application's {@code SharedPreferences}. 
	 */ 
	public static SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but 
	    // how you store the regID in your app is up to you. 
	    return context.getSharedPreferences("winrakom",
	            Context.MODE_PRIVATE);
	} 
	
	
	/** 
	 * @return Application's version code from the {@code PackageManager}. 
	 */ 
	public static int getAppVersion(Context context) {
	    try { 
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen 
	        throw new RuntimeException("Could not get package name: " + e);
	    } 
	}
	
	/** 
	 * Stores the registration ID and app versionCode in the application's 
	 * {@code SharedPreferences}. 
	 * 
	 * @param context application's context. 
	 * @param regId registration ID 
	 */ 
	public static void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}


    public static String getEmail(Context context) {

        // Supérieur à 2.1
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR) {

            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;
                    return possibleEmail;
                }
            }
        }
        // Égale à 2.1
        else {
            Account[] accounts = AccountManager.get(context).getAccounts();
            if (accounts.length > 0) {
                String possibleEmail = accounts[0].name;
                return possibleEmail;
            }
        }

        return "";
    }

}
