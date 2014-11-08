package com.example.tx.activity;

import com.example.tx.R;
import com.example.tx.R.layout;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//TODO 注销后remove别名
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("设置");
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		((TextView)findViewById(R.id.setting_logout)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				C.logged=false;
				MineActivity.mHandler.sendEmptyMessage(1);
				finish();
			}
		});
		
		((TextView)findViewById(R.id.setting_ModifyPassword)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, ModifyPasswordActivity.class);
				startActivity(intent);
			}
		});
		
		((TextView)findViewById(R.id.setting_help)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.putExtra("url", C.WebView_help);
				intent.putExtra("title", "帮助");
				intent.setClass(SettingActivity.this, EventActivity.class);
				startActivity(intent);
			}
		});
		
		((TextView)findViewById(R.id.setting_opinion)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.putExtra("url", C.WebView_advice);
				intent.putExtra("title", "建议");
				intent.setClass(SettingActivity.this, EventActivity.class);
				startActivity(intent);
			}
		});
		
		((TextView)findViewById(R.id.setting_about)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.putExtra("url", C.WebView_about);
				intent.putExtra("title", "关于");
				intent.setClass(SettingActivity.this, EventActivity.class);
				startActivity(intent);				
			}
		});
	}
}
