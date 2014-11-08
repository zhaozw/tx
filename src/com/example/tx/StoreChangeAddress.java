package com.example.tx;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoreChangeAddress extends Activity {
	
	private ImageButton ib_back_changeaddress;
	private TextView tv_submit;
	private EditText address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_change_address);
		
		Intent intent = getIntent();
		String a = intent.getExtras().getString("address");
		
		//
		ib_back_changeaddress = (ImageButton) findViewById(R.id.ib_back_changeaddress);
		tv_submit = (TextView) findViewById(R.id.tv_ok);
		address = (EditText) findViewById(R.id.et_myaddress);
		
		address.setText(a);
		
		ib_back_changeaddress.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		tv_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO 把修改的地址内容传过去
				Intent intent = getIntent();
				intent.putExtra("result", address.getText().toString());
				setResult(RESULT_OK,intent);
				finish();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store_change_address, menu);
		return true;
	}

}
