package com.example.tx.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.example.tx.NewDiscoveryActivity;
import com.example.tx.R;
import com.example.tx.util.C;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnCheckedChangeListener{
	
	private long exitTime;
	
	public TabHost tabhost;
	public RadioButton radio0;
	public RadioButton radio4;
	public RadioButton radio3;
	public RadioButton radio2;
	public RadioButton radio1;
	public int currenttag;
	public static MainActivity that;
	
	private TextView tv_messagenum;
	private int num;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		that=this;
		
		tv_messagenum = (TextView) findViewById(R.id.tv_messagenum);
		
		mHandler = new MyHandler();
		
		currenttag = 0;
		
		tabhost=getTabHost();

		TabSpec tab1=tabhost.newTabSpec("tab0");
		String string_market=getString(R.string.market);
		tab1.setIndicator(string_market);
		Intent it1=new Intent(this,MarketActivity.class);
		//tab里面显示的是各个activity的主要内容
		//将一个intent对象作为这个tab选项卡的内容，这样就可以实现activity之间跳转
		tab1.setContent(it1);
		tabhost.addTab(tab1);
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator(getString(R.string.secondhand)).setContent(new Intent(this,SecondHandActivity.class)));
		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator(getString(R.string.add)).setContent(new Intent(this,AddActivity.class)));
		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator(getString(R.string.message)).setContent(new Intent(this,NewDiscoveryActivity.class)));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator(getString(R.string.mine)).setContent(new Intent(this,MineActivity.class)));
		
		//最下面五个选择
		radio0=((RadioButton) findViewById(R.id.radio0));
		radio4=((RadioButton) findViewById(R.id.radio4));
		radio3=((RadioButton) findViewById(R.id.radio3));
		radio2=((RadioButton) findViewById(R.id.radio2));
		radio1=((RadioButton) findViewById(R.id.radio1));
		((RadioButton) findViewById(R.id.radio0)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio1)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio2)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio3)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio4)).setOnCheckedChangeListener(this);
		
		//进入即获取消息
		if(C.logged && C.userId != null){
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		if(isChecked)
		{
			switch(buttonView.getId())
			{
			case R.id.radio0:
				currenttag = 0;
				tabhost.setCurrentTabByTag("tab0");
				break;
			case R.id.radio1:
				currenttag = 1;
				tabhost.setCurrentTabByTag("tab1");
				break;
			case R.id.radio2:      
				//根据是否登录决定是否跳转
				if(C.logged == false){
					AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
					alert.setMessage("您还没有登录");
					alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							tabhost.setCurrentTabByTag("tab0");
							radio0.setChecked(true);
							return;
						}
					});
					alert.setButton(DialogInterface.BUTTON_POSITIVE, "登录", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
//							tabhost.setCurrentTabByTag("tab4");
							radio4.setChecked(true);
							return;
						}});
					alert.show();
				}
				else{
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, AddActivity.class);
					startActivity(intent);
				}
				//tabhost.setCurrentTabByTag("tab2");
				break;
			case R.id.radio3:
				currenttag = 3;
				tabhost.setCurrentTabByTag("tab3");
//				if(C.logged)
//					MessageActivity.that.mHandler.sendEmptyMessage(1);
//				Log.d("mainref","ref");
				break;
			case R.id.radio4:
				currenttag = 4;
				tabhost.setCurrentTabByTag("tab4");
				break;
			default:
				break;
			}
		}
	}
	
	public static MyHandler mHandler;
	public class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if(num > 0){  //显示消息提醒的个数
					tv_messagenum.setVisibility(View.VISIBLE);
					tv_messagenum.setText(String.valueOf(num));
					
				}else if(num == 0){
					tv_messagenum.setVisibility(View.INVISIBLE);
					tv_messagenum.setText(String.valueOf(num));
					
				}
				break;
			case 1:
				new getMsgsnumThread().start();
				break;
			}
		}
	}
	
	private class getMsgsnumThread extends Thread {

		@Override
		public void run() {
			HashMap p = new HashMap();
			p.put("userId", C.userId);
			JSONObject res = C.asyncPost(C.URLget_messages, p);
			try {
				if(res.getInt("status") != 0){
					//mHandler.sendToast("获取消息失败");
					return;
				}
				num = res.getJSONArray("messages").length();
				mHandler.sendEmptyMessage(0);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){  
		    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {   
		    	Log.d("back",exitTime+"");
				if(System.currentTimeMillis() - exitTime > 2000){
					Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
					return false;
				}
				else{
					finish();
				}
		    }  
		    return true;  
		}  
		return super.dispatchKeyEvent(event);
	}
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
//        JPushInterface.onResume(this);
        super.onResume();
    }
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(this);
    }

}
