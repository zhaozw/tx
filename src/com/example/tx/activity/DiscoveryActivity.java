package com.example.tx.activity;

import com.example.tx.activity.EventActivity;
import com.example.tx.R;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.EventLog.Event;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscoveryActivity extends BaseActivity {
	
	//private static String d_address = "http://112.126.67.182:8080/Discovery/Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discovery);
		
		((LinearLayout) findViewById(R.id.ll_treehole)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DiscoveryActivity.this, TreeholeActivity.class);
				startActivity(intent);
			}
			
		});
		
		((LinearLayout) findViewById(R.id.ll_activity)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.putExtra("url", C.WebView_event);
				intent.putExtra("title", "活动");
				intent.setClass(DiscoveryActivity.this, EventActivity.class);
				startActivity(intent);
			}
			
		});
		
//		((LinearLayout) findViewById(R.id.ll_storeinscholl)).setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(DiscoveryActivity.this, StoresActivity.class);
////				intent.putExtra("type", 1);
//				Bundle b=new Bundle();
//				b.putInt("type", 1);
//				intent.putExtra("store", b);
//				startActivity(intent);
//			}
//			
//		});
//		
//		((LinearLayout) findViewById(R.id.ll_studentstore)).setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(DiscoveryActivity.this, StoresActivity.class);
////				intent.putExtra("type", 0);
//				Bundle b=new Bundle();
//				b.putInt("type", 0);
//				intent.putExtra("store", b);
//				startActivity(intent);
//			}
//			
//		});
//		
//		((LinearLayout) findViewById(R.id.ll_storeoutschool)).setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(DiscoveryActivity.this, StoresActivity.class);
////				intent.putExtra("type", 2);
//				Bundle b=new Bundle();
//				b.putInt("type", 2);
//				intent.putExtra("store", b);
//				startActivity(intent);
//			}
//			
//		});
	}
}
