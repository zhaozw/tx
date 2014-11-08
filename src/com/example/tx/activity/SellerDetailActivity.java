package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.id;
import com.example.tx.R.layout;
import com.example.tx.adapter.ItemAdapter;
import com.example.tx.dto.Image;
import com.example.tx.dto.Item;
import com.example.tx.dto.User;
import com.example.tx.dto.UserDetails;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.app.Activity;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

public class SellerDetailActivity extends BaseActivity {

	TextView focus;
	User seller=null;
	MyHandler mHandler;
	boolean isFocus=true;
	UserDetails sellerDetails=null;
	ListView lv=null;
	Bitmap avatar=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_seller_detail);
		
		//公共框
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("个人信息");
		ImageButton ret=(ImageButton)findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		focus=(TextView)findViewById(R.id.common_title_bar_login);
		focus.setText("取消关注");
		focus.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(isFocus)
					new UnFocus().run();
				else
					new Focus().run();
			}
		});
		int index=getIntent().getIntExtra("index", -1);
		if(index!=-1)
			seller=MineSellerActivity.that.sellerList.get(index);
		else
			makeToast("通信失败");		
		
		mHandler=new MyHandler();
		
		lv=(ListView) findViewById(R.id.seller_detail_list);
		
		new GetSellerDetail().start();
	}
	public class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0:
				focus.setText("取消关注");
				break;
			case 1:
				focus.setText("关注");
				break;
			case 2:
				((TextView)findViewById(R.id.seller_detail_name)).setText(seller.userName);
				((TextView)findViewById(R.id.seller_detail_college)).setText(seller.college);
				((TextView)findViewById(R.id.seller_detail_grade)).setText(sellerDetails.getGrade());
				((ImageView)findViewById(R.id.seller_avatar)).setImageBitmap(avatar);
				//TODO 设置adapter
//				lv.setAdapter(new ItemAdapter(SellerDetailActivity.this,msgs,lv));
				lv.setSelection(lv.getFirstVisiblePosition());
				
			case 99:
				String info=(String)msg.obj;
				makeToast(info);
				break;
			}
		}
		public void sendToast(String info)
		{
			Message msg=mHandler.obtainMessage();
			msg.what=99;
			msg.obj=info;
			mHandler.sendMessage(msg);
		}
	}
	public class UnFocus extends Thread
	{
		@Override
		public void run()
		{
			try{
				HashMap o=new HashMap();
				o.put("userId", C.userId);
				if(seller!=null)
					o.put("sellerId", seller.userId);
				else
				{
					mHandler.sendToast("卖家信息初始化失败");
					return;
				}
				JSONObject res=C.asyncPost(C.URLunfocus_seller, o);
				if(res.getInt("status")==0)
				{
					mHandler.sendToast("取消关注成功");
					mHandler.sendEmptyMessage(1);
				}
				else
					mHandler.sendToast("取消失败，连接超时");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public class Focus extends Thread
	{
		@Override
		public void run()
		{
			try{
				HashMap o=new HashMap();
				o.put("userId", C.userId);
				if(seller!=null)
					o.put("sellerId", seller.userId);
				else
				{
					mHandler.sendToast("卖家信息初始化失败");
					return;
				}
				JSONObject res=C.asyncPost(C.URLadd_seller_to_favorite, o);
				if(res.getInt("status")==0)
				{
					mHandler.sendToast("关注成功");
					mHandler.sendEmptyMessage(0);
				}
				else
					mHandler.sendToast("关注失败，连接超时");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public class GetSellerDetail extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				HashMap req=new HashMap();
				req.put("sellerId", seller.userId);
				JSONObject res=C.asyncPost(C.URLget_seller_details, req);
				if(res.getInt("status")==0)
				{
					JSONArray array=res.getJSONArray("items");
					ArrayList<Item> items=new ArrayList();
					for(int i=0;i<array.length();i++)
					{
						JSONObject o=array.getJSONObject(i);
						JSONArray a=o.getJSONArray("images");
						List<Image> images=new ArrayList<Image>();
						for(int j=0;j<a.length();j++)
						{
							JSONObject oo=a.getJSONObject(j);
							images.add(new Image(oo.getString("imageId"),oo.getString("imageUrl")));
						}
						JSONObject so=o.getJSONObject("seller");
						User su=new User(so.getString("userId"),so.getString("account"),so.getString("userName"),so.getString("college"),so.getString("location"),so.getString("avatar"));
						items.add(new Item(o.getString("itemId"),
								o.getString("itemName"),
								(float) o.getDouble("price"),
								o.getString("details"),
								o.getString("releaseTime"),
								o.getString("unshelveTime"),
								o.getString("categoryId"),
								su,
								images));
					}
					sellerDetails=new UserDetails(res.getString("detailId"), res.getString("campus"), res.getString("school"), res.getString("major"), res.getInt("grade"), res.getString("userId"), seller, items);					
				}
				else
				{
					mHandler.sendToast("获取卖家详细失败，连接超时");
					return;
				}
				new Request4Image(seller.avatar)
				{
					@Override
					protected void onPostExecute(Bitmap result) {
						if(result!=null)
							avatar=result;
						else
						{
							mHandler.sendToast("获取头像失败");
							return;
						}
					}					
				}.execute();
				mHandler.sendEmptyMessage(2);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
