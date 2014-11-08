package com.example.tx.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.layout;
import com.example.tx.R.menu;
import com.example.tx.activity.MarketActivity.MyHandler;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ModifyPasswordActivity extends BaseActivity {
	
	private ImageButton ib_back;
	
	private Button b_modify;
	private EditText et_oldpassword;
	private EditText et_newPassword1;
	private EditText et_newPassword2;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		
		//绑定控件
		ib_back = (ImageButton) findViewById(R.id.ib_back_mpassword);
		b_modify = (Button) findViewById(R.id.b_modify);
		et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
		et_newPassword1 = (EditText) findViewById(R.id.et_newpassword1);
		et_newPassword2 = (EditText) findViewById(R.id.et_newpassword2);
		
		sp=getSharedPreferences("TaoxuePref", MODE_PRIVATE);
		
		ib_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		b_modify.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				HashMap map = new HashMap();
				map.put("userId", C.userId);
				map.put("oldPassword", et_oldpassword.getText().toString());
				
				if(et_newPassword1.getText().toString().length() < 6 || et_newPassword1.getText().toString().length() > 20){
					makeToast("密码长度应在6-20之间");
					return;
				}
				
				if(et_newPassword1.getText().toString().equals(et_newPassword2.getText().toString())){
					map.put("newPassword", et_newPassword1.getText().toString());
					new ModifyPasswordThread(map).start();
				}
				else{
					makeToast("两次输入密码不一致");
				}
			}
			
		});
		
		mHandler = new MyHandler();
		
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
	}
	
	private class ModifyPasswordThread extends Thread{
		HashMap map;
		public ModifyPasswordThread(HashMap m){
			this.map = m;
		}
		
		
		
		@Override
		public void run() {
			JSONObject ret = C.asyncPost(C.URLmodify_password, map);
			try {
				if(!(ret.getInt("status") == 0)){
					mHandler.sendToast(ret.getString("description"));
					return;
				}
				
				mHandler.sendToast(ret.getString("description"));
				Editor editor = sp.edit();
				editor.putString("password", et_newPassword1.getText().toString());
				editor.commit();
				
				
				finish();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_password, menu);
		return true;
	}

}
