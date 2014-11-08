package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.UserDetailInfor;
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.dto.Category;
import com.example.tx.dto.Categorys;
import com.example.tx.dto.Item;
import com.example.tx.dto.Image;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;
import com.example.tx.view.MyListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserinforActivity extends BaseActivity {
	
	public static UserinforActivity that;
	
	private MyListView lv_home;
	private TextView tv_market_empty;
	
	private String theUserId;
	
	private User theUser;
	
	public List<Item> msgs;
	public static boolean needRefresh = false;
	
	private String major;
	private int grade;
	
	//关注卖家
	private ImageButton ib_like_userinfor;
	private boolean liked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfor);
		
		that = this;
		
		mHandler = new MyHandler();
		
		lv_home = (MyListView)findViewById(R.id.lv_ui_goodslist);
		tv_market_empty = (TextView)findViewById(R.id.tv_ui_market_empty);
		ib_like_userinfor = (ImageButton) findViewById(R.id.ib_like_userinfor);
		
		theUserId = getIntent().getExtras().getString("userId");

		((LinearLayout) findViewById(R.id.ll_ui)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserinforActivity.this,UserDetailInfor.class);
				intent.putExtra("userId", theUserId);
				startActivity(intent);
				
			}
			
		});
		
		//返回
		((ImageButton) findViewById(R.id.ib_back_userinfor)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		//点击关组用户
		((ImageButton) findViewById(R.id.ib_like_userinfor)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!liked)
					new likeUserThread().start();
				else
					new DislikeUserThread().start();
			}
			
		});
		
		
		lv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long id) {
				//TO-DO 
				Intent intent = new Intent(UserinforActivity.this, ItemDetailActivity.class);
				intent.putExtra("from", 1);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				if(!C.logged){
					//mHandler.sendToast("您还未登陆");
					return;
				}
				HashMap p = new HashMap();
				p.put("userId", C.userId);
				p.put("sellerId", theUserId);
				Log.d("userinfor",C.userId+"==="+theUserId);
				JSONObject res = C.asyncPost(C.URLis_favorite_seller, p);
				try {
					if(res.getInt("status") == 0){
						liked = false;
						mHandler.sendEmptyMessage(11);
					}
					else if(res.getInt("status") == 1){
						liked = true;
						mHandler.sendEmptyMessage(11);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
		
		
		new SetDataListThread().start();
		lv_home.setEmptyView(tv_market_empty);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.userinfor, menu);
		return true;
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if(msgs.size() != 0){
					
					int pos = lv_home.getFirstVisiblePosition();
					lv_home.setAdapter(new MarketListAdapter(UserinforActivity.this, msgs, lv_home));
					lv_home.setSelection(pos);
				}
				
				((TextView)findViewById(R.id.tv_ui_name)).setText(theUser.userName);
				((TextView)findViewById(R.id.tv_ui_major)).setText(major);
				((TextView)findViewById(R.id.tv_ui_class)).setText(grade+"级");
				
				Bitmap b = C.getBitmapFromMemCache(theUser.avatar);
				final ImageView pics = (ImageView) findViewById(R.id.iv_ui_pic);
				if (b == null) {
		
					//根据url获取图片
					new Request4Image(theUser.avatar) 
					{
						@Override
						protected void onPostExecute(Bitmap result) 
						{
							if(result==null) 
								return;
							//C.addBitmapToMemoryCache(id, result);
							pics.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
							pics.setBackgroundResource(R.drawable.round_image_bg);
							//notifyDataSetChanged();
						}
					}.execute();
		
							
				} else {
					pics.setImageBitmap(b);
				}
				
				break;
				
			case 1:
				new SetDataListThread().start();
				break;
				
				//改变收藏按钮
			case 11:
				if(liked)
					//变为蓝色心
					ib_like_userinfor.setImageResource(R.drawable.like_dislike);
					
				else
					//变为原来的心
					ib_like_userinfor.setImageResource(R.drawable.like_50);
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
				
				p.put("sellerId", theUserId);
				JSONObject res = C.asyncPost(C.URLget_seller_details, p);
				Log.d("userinfor",theUserId+res.getInt("status"));
				if( !(res.getInt("status") == 0) ) {
					mHandler.sendToast("网络有问题！");
					return ;
				}
				Log.d("userinfor",theUserId+res.getString("description")+"=-=");
				JSONObject userDetails = res.getJSONObject("sellerDetails");//跟文档不符合，这个是对的
				
				
				major = userDetails.getString("major");
				grade = userDetails.getInt("grade");
				
				JSONObject so= userDetails.getJSONObject("user");
				User su=new User(so.getString("userId"),
						so.getString("account"),
						so.getString("userName"),
						so.getString("college"),
						so.getString("location"),
						so.getString("avatar"));
				theUser = su;
				//Log.d("userinfor",theUserId+"============="+so.getString("userName"));
				JSONArray items = userDetails.getJSONArray("items");
				Log.d("userinfor",items.length()+"");
				for(int i=0;i<items.length();i++) {
					JSONObject item=items.getJSONObject(i);
					JSONArray a=item.getJSONArray("images");
					List<Image> images=new ArrayList<Image>();
					for(int j=0;j<a.length();j++)
					{
						JSONObject oo=a.getJSONObject(j);
						images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
					}
					
					
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
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mHandler.sendEmptyMessage(0);
		}
		
	}
	
	private class likeUserThread extends Thread{

		@Override
		public void run() {
			if(!C.logged){
				mHandler.sendToast("您还未登陆");
				return;
			}
			HashMap p = new HashMap();
			p.put("userId", C.userId);
			p.put("sellerId", theUserId);
			Log.d("userinfor",C.userId+"==="+theUserId);
			JSONObject res = C.asyncPost(C.URLadd_seller_to_favorite, p);
			try {
				if(!(res.getInt("status") == 0)){
					mHandler.sendToast(res.getString("description"));
					return;
				}
				
				mHandler.sendToast(res.getString("description"));
				liked = true;
				mHandler.sendEmptyMessage(11);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	} 
	
	private class DislikeUserThread extends Thread{

		@Override
		public void run() {
			if(!C.logged){
				mHandler.sendToast("您还未登陆");
				return;
			}
			HashMap p = new HashMap();
			p.put("userId", C.userId);
			p.put("sellerId", theUserId);
			Log.d("userinfor",C.userId+"==="+theUserId);
			JSONObject res = C.asyncPost(C.URLremove_seller_from_favorite, p);
			try {
				if(!(res.getInt("status") == 0)){
					mHandler.sendToast(res.getString("description"));
					return;
				}
				
				mHandler.sendToast(res.getString("description"));
				liked = false;
				mHandler.sendEmptyMessage(11);
			} catch (JSONException e) {
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
			Log.v("Market", "Refresh");
		}
	}

}
