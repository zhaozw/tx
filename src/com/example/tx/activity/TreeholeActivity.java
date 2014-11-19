package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.TreeholeListAdapter;
import com.example.tx.dto.User;
import com.example.tx.dto.Talk;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TreeholeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	
	private ListView lv_treehole;
	private TextView tv_treehole_empty;
	private SwipeRefreshLayout swipe_container;
	
	public List<Talk> msgs;
	public static boolean needRefresh = false;
	
	public static TreeholeActivity that = null;
	
	//内容缓存
	private ACache mCache;
	private boolean firstIn = true;
	private boolean readfromcache = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_treehole);
		
		that = this;
		
		SharedPreferences sp=getSharedPreferences("TaoxuePref", MODE_PRIVATE);
		
		mCache = ACache.get(this);
		if(Welcome.firstInstall && sp.getBoolean("talkactivity", true)){
			mCache.clear();
			Editor editor=sp.edit();
			editor.putBoolean("talkactivity", false);
			editor.commit();
		}
		
		lv_treehole = (ListView) findViewById(R.id.lv_treehole);
		tv_treehole_empty = (TextView) findViewById(R.id.tv_treehole_empty);
		swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container1);
		
		//设置下拉刷新
		swipe_container.setOnRefreshListener(this);
		swipe_container.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		
		((ImageButton) findViewById(R.id.ib_back_treehole)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		
		((ImageButton) findViewById(R.id.ib_write_treehole)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TreeholeActivity.this, TreeholeWriteActivity.class);
				startActivity(intent);
			}
			
		});
		
		lv_treehole.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("listitem","click");
				Intent intent = new Intent();
				intent.putExtra("index", arg2);
				intent.setClass(TreeholeActivity.this, TreeholeCommentActivity.class);
				startActivity(intent);
			}
			
		});
		
		mHandler = new MyHandler();
		//Log.d("talks", "start");
		lv_treehole.setEmptyView(tv_treehole_empty);
		new SetDataListThread().start();
		
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int pos = lv_treehole.getFirstVisiblePosition();
				lv_treehole.setAdapter(new TreeholeListAdapter(TreeholeActivity.this, msgs, lv_treehole));
				lv_treehole.setSelection(pos);
				SetRefreshingFalse();
				break;
				
			case 1:
				new SetDataListThread().start();
				makeToast("正在刷新");
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
		
		@SuppressWarnings("null")
		@Override
		public synchronized void run() {
			msgs = new ArrayList<Talk>();
			try {
				Log.d("talks","start");
				HashMap p = new HashMap();
				JSONObject res = null;
				if(firstIn){
					res = mCache.getAsJSONObject("talk");
					firstIn = false;
					readfromcache = true;
				}
				if(res == null){
					res = C.asyncPost(C.URLget_talks, p);
					readfromcache = false;
				}
				
				//存入缓存
				if(!readfromcache)
					mCache.put("talk", res, ACache.TIME_HOUR);
				
				if( !(res.getInt("status")==0)) {
					mHandler.sendToast("网络有问题！");
					SetRefreshingFalse();
					return ;
				}
				JSONArray items = res.getJSONArray("talks");
				Log.d("talks", "start here"+items.length());
				for(int i=0;i < items.length();i++) {
					JSONObject item = items.getJSONObject(i);

					JSONObject talksender = item.getJSONObject("sender");

//					JSONArray talkcomments = item.getJSONArray("comments");
					Log.d("talks", "用户名");

					Log.d("talks", talksender.getString("userName"));
					Log.d("talks", talksender.getString("college"));
					Log.d("talks", talksender.getString("location"));
					Log.d("talks", talksender.getString("avatar"));
					

					msgs.add(new Talk(
							item.getString("id"),
							item.getString("time"),
							item.getString("content"),
							item.getString("senderId"),
							new User(
									talksender.getString("userId"),
									talksender.getString("account"),
									talksender.getString("userName"),
									talksender.getString("college"),
									talksender.getString("location"),
									talksender.getString("avatar")
									)
//							comments
							));
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mHandler.sendEmptyMessage(0);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(needRefresh) {
			needRefresh = false;
			new SetDataListThread().start();
			Log.v("Market", "Refresh");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.treehole, menu);
		return true;
	}

	@Override
	public void onRefresh() {
		new SetDataListThread().start();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipe_container.setRefreshing(false);
			}
		}, 5000);
	}
	
	public void SetRefreshingFalse(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipe_container.setRefreshing(false);
			}
		}, 1000);
	}

}
