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
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.dto.Image;
import com.example.tx.dto.Item;
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

public class MineCommodityDetailActivity extends BaseActivity{

	public List<Item> msgs;
	public static MineCommodityDetailActivity that;
	public ListView commodityList;
	
	private ItemAdapter ia;
	
	private boolean edit_status=true;//true为显示编辑，false为显示完成
	
	String categoryId,categoryName;
	
	@Override
	public void onStart()
	{
		super.onStart();
		edit_status=true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mine_commodity_detail);
		that=this;
		
		mhandler=new MyHandler();
		
		ImageButton ret=(ImageButton)findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) {
				finish();				
			}
		});	
		
		final int from=getIntent().getIntExtra("from",-1);
		
		((TextView)findViewById(R.id.common_title_bar_login)).setText("编辑");
		((TextView)findViewById(R.id.common_title_bar_login)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.common_title_bar_login)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(from==0)
				{
					if(edit_status)
					{
						((TextView)findViewById(R.id.common_title_bar_login)).setText("完成");
						for(int i=0;i<ItemAdapter.todel.length;i++)
							ItemAdapter.todel[i]=true;
						ia.notifyDataSetChanged();
					}
				}
			}
		});
		
		switch(from)
		{
		case 0:
			String cateId=getIntent().getStringExtra("categoryId");
			String cateName=getIntent().getStringExtra("categoryName");
			categoryId=cateId;
			categoryName=cateName;
			renderFromMyCommodity(cateId,cateName);
			break;
		case 1:
			renderFromMyFavourite();
			break;
		case -1:
			makeToast("数据错误");
			return;
		}
		commodityList=(ListView) findViewById(R.id.commodity_list);
		commodityList.setOnItemClickListener(new MyItemClick(from));
		commodityList.setEmptyView((TextView)findViewById(R.id.mine_commodity_list_empty));
	}
	private void renderFromMyFavourite()
	{
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("收藏的宝贝");
		TextView edit=(TextView) findViewById(R.id.common_title_bar_login);
		edit.setText("编辑");
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(edit_status)
				{
					((TextView)findViewById(R.id.common_title_bar_login)).setText("完成");
					for(int i=0;i<ItemAdapter.todel.length;i++)
						ItemAdapter.todel[i]=true;
					ia.notifyDataSetChanged();
					edit_status=false;
				}
				else
				{
					((TextView)findViewById(R.id.common_title_bar_login)).setText("编辑");
					for(int i=0;i<ItemAdapter.todel.length;i++)
						ItemAdapter.todel[i]=false;
					ia.notifyDataSetChanged();
					edit_status=true;
				}
			}
		});
		new MyFavouriteListThread().start();
		
	}
	private void renderFromMyCommodity(String id,String name) 
	{
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("我-"+name);
		
		((TextView)findViewById(R.id.common_title_bar_login)).setText("编辑");
		((TextView)findViewById(R.id.common_title_bar_login)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.common_title_bar_login)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(edit_status)
				{
					((TextView)findViewById(R.id.common_title_bar_login)).setText("完成");
					for(int i=0;i<ItemAdapter.todel.length;i++)
						ItemAdapter.todel[i]=true;
					ia.notifyDataSetChanged();
					edit_status=false;
				}
				else
				{
					((TextView)findViewById(R.id.common_title_bar_login)).setText("编辑");
					for(int i=0;i<ItemAdapter.todel.length;i++)
						ItemAdapter.todel[i]=false;
					ia.notifyDataSetChanged();
					edit_status=true;
				}
			}
		});
		
		new MyCommodityListThread(id).start();
	}
	public static MyHandler mhandler;
	public class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0:
				int pos = commodityList.getFirstVisiblePosition();
				ia=new ItemAdapter(MineCommodityDetailActivity.this, msgs, commodityList,0,!edit_status);
				ia.isFirstEnter = true;
				commodityList.setAdapter(ia);
				commodityList.setSelection(pos);
				break;
			case 1:
				new MyCommodityListThread(categoryId).start();
				break;
			case 2:
				ia=new ItemAdapter(MineCommodityDetailActivity.this,msgs,commodityList,1,!edit_status);
				ia.isFirstEnter = true;
				commodityList.setAdapter(ia);
				commodityList.setSelection(commodityList.getFirstVisiblePosition());
				break;
			case 3:
				new MyFavouriteListThread().start();
				break;
			case 99:
				String toast=(String) msg.obj;
				makeToast(toast);
				break;
			}
		}
		public void sendToast(String msg)
		{
			Message toastMsg=mhandler.obtainMessage();
			toastMsg.what=99;
			toastMsg.obj=msg;
			mhandler.sendMessage(toastMsg);
			return;
		}
	}
	private class MyFavouriteListThread extends Thread
	{
		@Override
		public synchronized void run()
		{
			msgs=new ArrayList<Item>();
			try
			{
				HashMap req=new HashMap();
				req.put("userId", C.userId);
				JSONObject res=C.asyncPost(C.URLget_my_favorite_items, req);
				if(res.getInt("status")==0)
				{
					JSONArray items=res.getJSONArray("favoriteItems");
					for(int i=0;i<items.length();i++)
					{
						JSONObject item=items.getJSONObject(i);

						JSONArray a=item.getJSONArray("images");
						List<Image> images=new ArrayList<Image>();
						for(int j=0;j<a.length();j++)
						{
							JSONObject oo=a.getJSONObject(j);
							images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
						}
						JSONObject so=item.getJSONObject("seller");
						User su=new User(so.getString("userId")
								,so.getString("account")
								,so.getString("userName")
								,so.getString("college")
								,so.getString("location")
								,so.getString("avatar"));
						msgs.add(new Item(item.getString("itemId")
								,item.getString("itemName")
								,(float) item.getDouble("price")
								,item.getString("details")
								,item.getString("releaseTime")
								,item.getString("unshelveTime")
								,item.getString("categoryId")
								,su
								,images));
					}
					mhandler.sendEmptyMessage(2);
				}
				else
				{
					mhandler.sendToast("获取我收藏的宝贝失败");
					return;
				}
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	private class MyCommodityListThread extends Thread
	{
		String categoryId;
		public MyCommodityListThread(String categoryId)
		{
			this.categoryId=categoryId;
		}
		@Override
		public synchronized void run()
		{
			msgs=new ArrayList<Item>();
			try
			{
				HashMap o=new HashMap();
				o.put("userId", C.userId);
				JSONObject res;
				if(categoryId==null)
				{
					res=C.asyncPost(C.URLget_my_items, o);
				}
				else if(!categoryId.equals("reviewing"))
				{
					o.put("categoryId", categoryId);
					res=C.asyncPost(C.URLget_my_items, o);
				}
				else
				{
					res=C.asyncPost(C.URLget_my_reviewing_items, o);
				}
				
				if(res.getInt("status")!=0)
				{
					mhandler.sendToast("网络出现问题");
					return ;
				}
				JSONArray items=res.getJSONArray("items");
				for(int i=0;i<items.length();i++)
				{
					JSONObject item=items.getJSONObject(i);
					JSONArray a=item.getJSONArray("images");
					List<Image> images=new ArrayList<Image>();
					for(int j=0;j<a.length();j++)
					{
						JSONObject oo=a.getJSONObject(j);
						images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
					}
//					JSONObject so=item.getJSONObject("seller");
//					User su=new User(so.getString("userId"),so.getString("account"),so.getString("userName"),so.getString("college"),so.getString("location"),so.getString("avatar"));
					msgs.add(new Item(item.getString("itemId")
							,item.getString("itemName")
							,(float)item.getDouble("price")
							,item.getString("details")
							,item.getString("releaseTime")
							,item.getString("unshelveTime")
							,item.getString("categoryId")
							,null
							,images
							));
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			mhandler.sendEmptyMessage(0);
		}
	}
	private class MyItemClick implements OnItemClickListener
	{
		int from=0;
		public MyItemClick(int from)
		{
			super();
			this.from=from;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int index,long id)
		{
			if(from==0)
			{
				Intent it=new Intent(MineCommodityDetailActivity.this,CommodityItemDetailActivity.class);
				it.putExtra("from", this.from);
				it.putExtra("index", index);
				startActivity(it);
			}
			else
			{
				Intent intent = new Intent(MineCommodityDetailActivity.this,ItemDetailActivity.class);
				intent.putExtra("from", 4);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		}
	}
}
