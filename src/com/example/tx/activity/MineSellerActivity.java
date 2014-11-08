package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;

import com.example.tx.R.layout;
import com.example.tx.adapter.SellerFocusAdapter;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MineSellerActivity extends BaseActivity implements OnItemClickListener{
	public static MineSellerActivity that;
	public static MyHandler mHandler;
	public List<User> sellerList;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mine_seller);
		that=this;
		
		//公共框
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("关注的卖家");
		ImageButton ret=(ImageButton) findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView edit=(TextView) findViewById(R.id.common_title_bar_login);
		edit.setText("Edit");
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO 编辑
			}
		});
		
		lv=(ListView) findViewById(R.id.seller_list);	
		lv.setEmptyView((TextView)findViewById(R.id.seller_empty));
		lv.setOnItemClickListener(this);
		mHandler=new MyHandler();
		new setData().start();
	}
	public class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0:
				int pos = lv.getFirstVisiblePosition();
				lv.setAdapter(new SellerFocusAdapter(MineSellerActivity.this, sellerList, lv));
				lv.setSelection(pos);
				break;
			case 1:
				new setData().start();
				break;
			case 99:
				String message= (String) msg.obj;
				makeToast(message);
				break;
			}
		}
		
		public void sendToast(String msg)
		{
			Message message=mHandler.obtainMessage();
			message.what=99;
			message.obj=msg;
			mHandler.sendMessage(message);
		}
	}
	public class setData extends Thread
	{
		@Override
		public synchronized void run()
		{
			sellerList=new ArrayList<User>();
			try 
			{
				HashMap req=new HashMap();
				req.put("userId", C.userId);
				
				JSONObject res=C.asyncPost(C.URLget_my_favorite_sellers, req);
				if(res.getInt("status")!=0)
				{
					mHandler.sendToast("获取我最喜欢的卖家失败，连接超时");	
					return;
				}
				JSONArray sellers=res.getJSONArray("favoriteSellers");
				for(int i=0;i<sellers.length();i++)
				{
					JSONObject seller=sellers.getJSONObject(i);
					sellerList.add(new User(seller.getString("userId")
							, seller.getString("account")
							, seller.getString("userName")
							, seller.getString("college")
							, seller.getString("location")
							, seller.getString("avatar")));					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(0);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		Intent it=new Intent(this,UserinforActivity.class);
		it.putExtra("userId", sellerList.get(position).userId); 
		startActivity(it);
	}
}
