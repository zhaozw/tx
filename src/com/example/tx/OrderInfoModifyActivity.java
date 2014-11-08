package com.example.tx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class OrderInfoModifyActivity extends Activity {
	
	TextView tv_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_info_modify);
		
		Intent it=getIntent();
		String title=it.getStringExtra("head");
		
		TextView tv_title=(TextView)findViewById(R.id.tv_store_name);
		tv_title.setText(title);
		
		ImageButton ib_return=(ImageButton)findViewById(R.id.ib_backstoredetail);
		TextView tv_done=(TextView)findViewById(R.id.ib_buy_storedetail);
		
		tv_main=(TextView)findViewById(R.id.tv_main);
		
		ib_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		tv_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String resStr=tv_main.getText().toString().trim();
				Intent it=new Intent();
				it.putExtra("result", resStr);
				setResult(RESULT_OK, it);
			}
		});
	}
}
