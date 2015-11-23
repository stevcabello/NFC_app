package com.example.appnfc;

import android.app.Application;

public class Global extends Application {
	
	private static Global singleton;

	public static String DireccionIPWebServices = "stevcabello-001-site1.atempurl.com";
	public static String TestJson;
	public static String Test;
	
  	
	public static Global getInstance() {
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        
    }

}
