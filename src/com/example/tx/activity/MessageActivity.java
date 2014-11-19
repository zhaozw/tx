package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.StoreItemDetailActivity;
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.adapter.MessageListAdapter;
import com.example.tx.dto.Item;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MessageActivity extends BaseActivity {
	
	private ListView lv_message;
	private TextView tv_message_empty;
	
	private boolean flag = true;  //flag为true显示未读，为false，显示已读，刚开始时为true，显示未读
	
	
	public List<com.example.tx.dto.Message> msgs;
	public static boolean needRefresh = false;
	
	public static MessageActivity that = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		that = this;
		
		
		
		if(!C.logged){
			makeToast("亲，您还未登陆，这里没有您的消息！");
		}
		
		lv_message = (ListView) findViewById(R.id.lv_message);
		tv_message_empty = (TextView) findViewById(R.id.tv_message_empty);
		
		((Button) findViewById(R.id.b_read)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(flag){
					((Button)v).setText("未读");
					((TextView) findViewById(R.id.tv_m_unread)).setText("全部消息");
				}
				else{
					((Button)v).setText("全部");
					((TextView) findViewById(R.id.tv_m_unread)).setText("未读消息");
				}
				
				flag = !flag;
				
				new SetDataListThread().start();
			}
			
		});
		lv_message.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long id) {
				//TO-DO 首先检查是商品还是树洞
				//点击后将消息标记为已读并传给服务器
				com.example.tx.dto.Message msg = msgs.get(index);
				switch(msg.refType){
				case 0://商品
					Intent intent = new Intent(MessageActivity.this, ItemDetailActivity.class);
					intent.putExtra("from", 3);
					intent.putExtra("index", index);
					startActivity(intent);
					break;
				case 1://树洞
					Intent intent1 = new Intent(MessageActivity.this, TreeholeCommentActivity.class);
					intent1.putExtra("from", 3);
					intent1.putExtra("index", index);
					startActivity(intent1);
					break;
				case 2://商店的商品
					Intent intent2 = new Intent(MessageActivity.this,StoreItemDetailActivity.class);
					intent2.putExtra("from", 1);
					intent2.putExtra("redId", msg.refId);
					startActivity(intent2);
					break;
				}
				
				if(flag){
					HashMap p = new HashMap();
					p.put("messageId", msg.id);
					new setMessageReadedThread(p).start();
				}
				
				
			}
		});
		
		mHandler = new MyHandler();
		//Log.d("thread","t");
		new SetDataListThread().start();
		lv_message.setEmptyView(tv_message_empty);
		//needRefresh = true;
	}
	
	public static MyHandler mHandler;
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int pos = lv_message.getFirstVisiblePosition();
				lv_message.setAdapter(new MessageListAdapter(MessageActivity.this, msgs, lv_message));
				lv_message.setSelection(pos);
				break;
				
			case 1:
				new SetDataListThread().start();
				break;
				
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
	

private class SetDataListThread extends Thread {
		
		@Override
		public synchronized void run() {
			msgs = new ArrayList<com.example.tx.dto.Message>();
			
			try {
				HashMap p = new HashMap();
				if(!C.logged)
					return;

				
				p.put("userId", C.userId);
				
				Log.d("message","start");
				JSONObject res = null;
				if(flag)
					res = C.asyncPost(C.URLget_messages, p);
				else
					res = C.asyncPost(C.URLget_all_messages, p);
				if( !(res.getInt("status") == 0) ) {
					if(C.logged)
						mHandler.sendToast("网络有问题！");
					return ;
				}
				
				Log.d("message",res.getString("description"));
				
				JSONArray messages = res.getJSONArray("messages");
				
				Log.d("message",messages.length()+"==");
				
				for(int i=0;i<messages.length();i++) {
					JSONObject message = messages.getJSONObject(i);
					JSONObject sender = message.getJSONObject("sender");
					
					Log.d("message",sender.getString("userName")+message.getString("refId"));
					Log.d("message",message.getInt("refType")+"=="+message.getInt("isRead"));
					Log.d("message",message.getString("time")+message.getString("content"));
					
					
					msgs.add(new com.example.tx.dto.Message(
							message.getString("id"),
							message.getString("content"),
							message.getString("time"),
							message.getString("senderId"),
							new User(
									sender.getString("userId"),
									sender.getString("account"),
									sender.getString("userName"),
									sender.getString("college"),
									sender.getString("location"),
									sender.getString("avatar")
									),
							message.getInt("refType"),  // 0为商品, 1 为树洞
							message.getString("refId"),
							message.getInt("isRead")    // 0 表示未读， 1 表示已读
							));
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mHandler.sendEmptyMessage(0);
		}
		
	}

	private class setMessageReadedThread extends Thread{
		
		private HashMap p;
		
		public setMessageReadedThread(HashMap p){
			this.p = p;
		}

		@Override
		public void run() {
			JSONObject res = C.asyncPost(C.URLread_message, p);
			try {
				if(res.getInt("status") != 0){
					Log.d("read",res.getString("description"));
					return;
				}
				Log.d("read",res.getString("description")+"===");
				MessageActivity.mHandler.sendEmptyMessage(1);
				MainActivity.mHandler.sendEmptyMessage(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(needRefresh) {
			needRefresh = false;
			new SetDataListThread().start();
			Log.v("message", "Refresh");
		}
	}
}
