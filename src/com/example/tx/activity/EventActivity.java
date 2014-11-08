package com.example.tx.activity;

import org.apache.http.util.EncodingUtils;

import com.example.tx.R;
import com.example.tx.util.BaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;


public class EventActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		String url=getIntent().getStringExtra("url");
		String title=getIntent().getStringExtra("title");
		((TextView)findViewById(R.id.event_title)).setText(title);
		
		WebView wv = (WebView)findViewById(R.id.webView_event);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.requestFocus();
		wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.postUrl(url, EncodingUtils.getBytes("", "base64"));
		
		((ImageButton) findViewById(R.id.ib_eventback)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.event, menu);
		return true;
	}

}
