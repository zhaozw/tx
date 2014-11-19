package com.example.tx;

import com.example.tx.activity.DiscoveryActivity;
import com.example.tx.activity.EventActivity;
import com.example.tx.activity.MessageActivity;
import com.example.tx.activity.TreeholeActivity;
import com.example.tx.util.C;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NewDiscoveryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_discovery);
		
		//跳转activity
		((RelativeLayout) findViewById(R.id.rl_talk)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NewDiscoveryActivity.this, TreeholeActivity.class);
				startActivity(intent);
			}
			
		});
		
		//活动
		((RelativeLayout) findViewById(R.id.rl_activity)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();        
				intent.putExtra("url", C.WebView_event);
				intent.putExtra("title", "活动");
				intent.setClass(NewDiscoveryActivity.this, EventActivity.class);
				startActivity(intent);
			}
			
		});
		
		((RelativeLayout) findViewById(R.id.rl_message)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NewDiscoveryActivity.this, MessageActivity.class);
				startActivity(intent);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_discovery, menu);
		return true;
	}

}
