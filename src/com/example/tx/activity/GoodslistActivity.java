package com.example.tx.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tx.R;
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.dto.Categorys;
import com.example.tx.dto.Image;
import com.example.tx.dto.Item;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.view.RefreshListView;
import com.example.tx.view.RefreshListView.PullToRefreshListener;

public class GoodslistActivity extends BaseActivity  {
	
	public static GoodslistActivity that = null;
	
	//定义控件
	private TextView tv_header;
	private Button b_new;
	private Button b_pricedown;
	private Button b_priceup;
	private ListView lv_home;
	private TextView tv_market_empty;
	
	private RefreshListView freshListView;
	
	private String title;
	
	private String category_id; //商品分类的keyword
	
	public List<Item> msgs;
	public static boolean needRefresh = false;
	
	//排序方式
	public int order;
	
	//设置每个页面显示商品的个数
	private int startNumber;
	private int endNumber;
	
	//最后一条数据显示的id
	public int lastItemId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goodslist);
		
		that = this;
		
		order = 0;
		
		tv_header = (TextView)findViewById(R.id.tv_header);
		b_new =  (Button)findViewById(R.id.b_new);
		b_pricedown = (Button)findViewById(R.id.b_pricedown);
		b_priceup = (Button)findViewById(R.id.b_priceup);
		lv_home = (ListView)findViewById(R.id.lv_home);
		tv_market_empty = (TextView)findViewById(R.id.tv_market_empty);
		//刷新
		freshListView = (RefreshListView) findViewById(R.id.swipe_container);
		
		//获取市场传来的信息
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		title = bundle.getString("title");
		category_id = bundle.getString("categoryid");
		
		tv_header.setText(title);
			
		b_new.setBackgroundResource(R.drawable.round_left_click);
		
		
		//设置下拉刷新
		freshListView.setOnRefreshListener(new PullToRefreshListener() {
			
			@Override
			//下面实现刷新逻辑
			public void onRefresh() {
				// TODO Auto-generated method stub
				new SetDataListThread().start();
				freshListView.finishRefreshing();
			}
		},0);   //用第二个参数标识不同的界面
	
		
		
		//三个排序按钮的功能
		b_new.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setBackgroundResource(R.drawable.round_left_click);
				b_pricedown.setBackgroundResource(R.color.snow);
				b_priceup.setBackgroundResource(R.drawable.round_right);
				
				order = 0;
				//msgs.clear();
				new SetDataListThread().start();
			}
			
		});
		
		b_pricedown.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.color.lightblue);
				b_new.setBackgroundResource(R.drawable.round_left);
				b_priceup.setBackgroundResource(R.drawable.round_right);
				
				order = 1;
				new SetDataListThread().start();
			}
			
		});
		
		b_priceup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.round_right_click);
				b_new.setBackgroundResource(R.drawable.round_left);
				b_pricedown.setBackgroundResource(R.color.snow);
				
				order = 2;
				new SetDataListThread().start();
			}
			
		});
		
		//lv_home设置内容
		
		lv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long id) {
				//TO-DO 
				Intent intent = new Intent(GoodslistActivity.this, ItemDetailActivity.class);
				intent.putExtra("from", 0);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});
		
		
		((ImageButton) findViewById(R.id.ib_back)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		mHandler = new MyHandler();
		new SetDataListThread().start();
		lv_home.setEmptyView(tv_market_empty);
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				int pos = lv_home.getFirstVisiblePosition();
				lv_home.setAdapter(new MarketListAdapter(GoodslistActivity.this, msgs, lv_home));
				lv_home.setSelection(pos);
				if(msgs.size() == 0)
					mHandler.sendEmptyMessage(88);
				//SetRefreshingFalse();
				break;
				
			case 1:
				new SetDataListThread().start();
				makeToast("正在刷新");
				break;
				
			case 88:
				tv_market_empty.setText("这里还没有商品，先去别处逛逛吧    ^_^");
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
			msgs = new ArrayList<Item>();
			try {
				HashMap p = new HashMap();
				//Log.d("goodlist",category_id+","+order);
				p.put("keyWord", "");
				if( !category_id.equals(Categorys.categorys.get(0).id)){
					p.put("categoryId", category_id);
					//Log.d("all","all");
				}
				p.put("order", order);
				p.put("cntStart","0");
				p.put("cntEnd", "20");
				JSONObject res = C.asyncPost(C.URLget_items, p);
				if( !(res.getInt("status") == 0) ) {
					mHandler.sendToast("网络有问题！");
					//SetRefreshingFalse();
					return ;
				}
				
				
				JSONArray items = res.getJSONArray("items");
				
				//Log.d("goodlist",res.getString("description")+items.length());
				
				for(int i=0;i<items.length();i++) {
					JSONObject item=items.getJSONObject(i);
					JSONArray a=item.getJSONArray("images");
					List<Image> images=new ArrayList<Image>();
					
					//Log.d("goodlist",".........................................."+a.length());
					
					for(int j=0;j<a.length();j++)
					{
						JSONObject oo = a.getJSONObject(j);
						Log.d("goodlist1",item.getString("itemName")+"||"+oo.getString("picId")+"||"+oo.getString("picUrl"));
						images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
					}
					JSONObject so=item.getJSONObject("seller");
					
					//Log.d("goodlist",so.getString("location"));
					////Log.d("goodlist",so.getString("account"));
					//Log.d("goodlist",so.getString("userName"));
					//Log.d("goodlist",so.getString("avatar"));
					
					User su=new User(so.getString("userId"),so.getString("account"),so.getString("userName"),so.getString("college"),so.getString("location"),so.getString("avatar"));
					msgs.add(new Item(item.getString("itemId")
							,item.getString("itemName")
							,(float)item.getDouble("price")
							,item.getString("details")
							,item.getString("releaseTime")
							,item.getString("unshelveTime")
							,item.getString("categoryId")
							,su
							,images
							));
					//Log.d("goodlist",item.getString("itemName")+item.getString("details"));
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
		getMenuInflater().inflate(R.menu.goodslist, menu);
		return true;
	}
/*
	@Override
	public void onRefresh() {
		mHandler.sendEmptyMessage(1);
	}

	public void SetRefreshingFalse(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipe_container.setRefreshing(false);
			}
		}, 1000);
	}
	
*/
}
