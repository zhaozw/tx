package com.example.tx.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.activity.AddActivity.MyHandler;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TreeholeWriteActivity extends BaseActivity {
	
	private EditText et_thwrite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_treehole_write);
		
		mHandler = new MyHandler();
		
		et_thwrite = (EditText) findViewById(R.id.et_thwrite);
		
		((Button)findViewById(R.id.b_back_thwrite)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		((Button)findViewById(R.id.b_send_thwrite)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO-发送内容
				if(!C.logged){
					makeToast("亲，您还未登陆，请登陆后再发布哦！");
					return;
				}
				String content = et_thwrite.getText().toString();
				if(content.equals("")){
					makeToast("您还未填写内容");
					return ;
				}
				HashMap p = new HashMap();
				p.put("userId", C.userId);
				p.put("content", content);
				
				new sendThread(p).start();
				finish();
			}
			
		});
		
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				
			case 99:
				String toast = (String) msg.obj;
				makeToast(toast);
				break;

			default:
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
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treehole_write, menu);
		return true;
	}
	
	private class sendThread extends Thread{
		HashMap p;
		public sendThread(HashMap p){
			this.p = p;
		}
		@Override
		public void run() {
			try {
				
				JSONObject ret = C.asyncPost(C.URLadd_talk, p);
				if(ret.getInt("status") != 0){
					mHandler.sendToast("发送失败");
					return;
				}
				mHandler.sendToast("发送成功，快去看看吧。");
				TreeholeActivity.mHandler.sendEmptyMessage(1);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
