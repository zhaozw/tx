package com.example.tx;

import com.example.tx.adapter.CartListAdapter;
import com.example.tx.util.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OrderManageActivity extends BaseActivity {
	
	private TextView b_edit;
	private ListView lv_orders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_manage);
		
		//
		b_edit = (TextView) findViewById(R.id.ib_edit);         //TODO 点击编辑后，删除按钮显示，右侧小图片隐藏
		lv_orders = (ListView) findViewById(R.id.lv_orders);  //TODO OrderListAdapter
		
		//
		//返回
		((ImageButton)findViewById(R.id.ib_back_orders)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		lv_orders.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mHandler = new MyHandler();
		
	}
	
	
	static public MyHandler mHandler;
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				break;
			case 99:
				makeToast((String)msg.obj);
				break;
			}
			
		}
		
		public void sendToast(String toast) {
			Message toastMessage = mHandler.obtainMessage();
			toastMessage.obj = toast;
			toastMessage.what = 99;
			mHandler.sendMessage(toastMessage);
			return ;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_manage, menu);
		return true;
	}

}
