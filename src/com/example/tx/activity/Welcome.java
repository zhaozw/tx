package com.example.tx.activity;

import cn.jpush.android.api.JPushInterface;

import com.example.tx.R;
import com.example.tx.util.C;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;

public class Welcome extends Activity {
	
	public static Welcome that;
	public static boolean firstInstall;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		that=this;
		
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
        
		

		SharedPreferences sp=getSharedPreferences("TaoxuePref", MODE_PRIVATE);
		if(sp!=null)
		{
			C.logged=sp.getBoolean("check_autolog", false);
		}
		if(C.logged)
		{
			C.account=sp.getString("account", "");
			C.userId=sp.getString("userId", "");
			C.location=sp.getString("location", "");
		}
		
		//查看是否重新安装其他版本了
		if(!getVersion().equals(sp.getString("versionname", "0"))){
			firstInstall = true;
			Editor editor=sp.edit();
			editor.putString("versionname", getVersion());
			editor.commit();
		}
		else{
			firstInstall = false;
		}
		
		
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				startActivity(new Intent(getApplication(),MainActivity.class));
				Welcome.this.finish();
			}			
		}, 1000);
		
	}
	
	@Override
	protected void onPause()
	{
		JPushInterface.onPause(this);
        super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		JPushInterface.onResume(this);
		super.onResume();
		
	}
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "0";
	    }
	}
}
