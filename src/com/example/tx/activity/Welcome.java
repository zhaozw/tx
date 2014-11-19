package com.example.tx.activity;

import cn.jpush.android.api.JPushInterface;

import com.example.tx.R;
import com.example.tx.util.C;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;

public class Welcome extends Activity {
	
	public static Welcome that;

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
		super.onResume();
		JPushInterface.onResume(this);
	}
}
