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

public class AddRemarkActivity extends Activity {
	
	private ImageButton ib_back_addremark;
	private TextView tv_submit;
	private EditText remark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_remark);
		
		//
		String s = getIntent().getExtras().getString("remark");
		if(s.equals("点此添加备注信息"))
			s="";
		ib_back_addremark = (ImageButton) findViewById(R.id.ib_back_addremark);
		tv_submit = (TextView) findViewById(R.id.ib_yes);
		remark = (EditText) findViewById(R.id.et_doremark);
		remark.setText(s);
		
		ib_back_addremark.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		tv_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO 添加备注信息到订单activity
				Intent intent = getIntent();
				intent.putExtra("result", remark.getText().toString());
				setResult(RESULT_OK,intent);
				finish();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_remark, menu);
		return true;
	}

}
