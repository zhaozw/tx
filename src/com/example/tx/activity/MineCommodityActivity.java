package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.R.id;
import com.example.tx.R.layout;
import com.example.tx.adapter.CategoryAdapter;
import com.example.tx.dto.Category;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MineCommodityActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	
	ImageButton all,computer,book,phone;
	boolean edit_display=true;
	ListView lv;
	
	Thread tc,tr;
	
	boolean edit_status;
	
	private List<Category> myCates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mine_commodity);
		
		lv=(ListView)findViewById(R.id.mine_category_list);
		lv.setOnItemClickListener(this);
		lv.setEmptyView(((TextView)findViewById(R.id.mine_category_default)));
		
		mHandler=new MyHandler();
		
		((TextView)findViewById(R.id.commen_title_bar_txt)).setText("我的商品");
		ImageButton ret=(ImageButton)findViewById(R.id.commen_title_bar_ret);
		ret.setVisibility(View.VISIBLE);
		ret.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		myCates=new ArrayList<Category>();
		tc=new GetCategory();
		tc.start();
		tr=new GetReviewing();
		tr.start();
	}
	public class GetReviewing extends Thread
	{
		@Override
		public synchronized void run()
		{
			try
			{
				HashMap req=new HashMap();
				req.put("userId",C.userId);
				JSONObject res=C.asyncPost(C.URLget_my_reviewing_item_count, req);
				if(res.getInt("status")==0)
				{
					int a;
					if((a=res.getInt("itemCount"))>0)
					{
						myCates.add(new Category(String.valueOf(a),"reviewing","审核中",null));
					}
				}
				else
					makeToast("获取正在审核的商品错误:"+res.getString("description"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
				makeToast("后台错误");
			}
		}
	}
	
	public class GetCategory extends Thread
	{
		@Override
		public synchronized void run()
		{
			try
			{
				myCates.clear();
				HashMap map=new HashMap();
				map.put("userId", C.userId);
				JSONObject res=C.asyncPost(C.URLget_my_categories, map);
				if(res.getInt("status")==0)
				{
					JSONArray ja=res.getJSONArray("categories");
					int sum=0;
					for(int i=0;i<ja.length();i++)
					{
						JSONObject o=ja.getJSONObject(i);
						myCates.add(new Category(o.getString("itemCount"),o.getString("id"), o.getString("name"), o.getString("image")));
						sum+=o.getInt("itemCount");
					}
					if(sum>0)
						myCates.add(new Category(String.valueOf(sum),null,"全部",null));
					tr.join();
					//把“全部”换到第一个
					for(int i=0;i<myCates.size();i++)
					{
						if(myCates.get(i).name.equals("全部"))
						{
							Category c=myCates.get(i);
							myCates.set(i, myCates.get(0));
							myCates.set(0, c);
						}
						if(myCates.get(i).name.equals("正在审核中的商品"))
						{
							Category c=myCates.get(i);
							myCates.set(i, myCates.get(myCates.size()-1));
							myCates.set(myCates.size()-1, c);
						}
					}
					mHandler.sendEmptyMessage(0);
				}
				else
				{
					mHandler.sendToast("获取商品种类失败");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public static MyHandler mHandler;
	public class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			{
			case 0:
				lv.setAdapter(new CategoryAdapter(MineCommodityActivity.this,myCates,lv,false));
				lv.setSelection(lv.getFirstVisiblePosition());
				break;
			case 1:
				myCates=new ArrayList<Category>();
				tc=new GetCategory();
				tc.start();
				tr=new GetReviewing();
				tr.start();
//				new GetCategory().start();
				break;
			case 99:
				String toast=(String)msg.obj;
				makeToast(toast);
				break;
			}
		}
		public void sendToast(String toast)
		{
			Message msg=mHandler.obtainMessage();
			msg.obj=toast;
			msg.what=99;
			mHandler.sendMessage(msg);
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		Intent it=new Intent(MineCommodityActivity.this,MineCommodityDetailActivity.class);
		it.putExtra("from", 0);
		startActivity(it);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		Intent it=new Intent(MineCommodityActivity.this,MineCommodityDetailActivity.class);
		it.putExtra("from",0);
		it.putExtra("categoryId", myCates.get(position).id);
		it.putExtra("categoryName", myCates.get(position).name);
		startActivity(it);
	}
}
