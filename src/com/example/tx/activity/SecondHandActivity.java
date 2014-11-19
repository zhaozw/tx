package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tx.R;
import com.example.tx.adapter.SecondHandAdapter;
import com.example.tx.dto.Category;
import com.example.tx.dto.Categorys;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

public class SecondHandActivity extends BaseActivity {
	
	//private static String d_address = "http://112.126.67.182:8080/Discovery/Activity";

	private ImageButton searchButton;
	private ListView listView;
	private List<Category> categoryList;
	
	private ImageView[] tips;
	private int bitcount;
	private Bitmap[] bitmaps;
	private Bitmap default_bitmap;
	private String[] urls[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_secondhand);
		
		//TextView tv_header = (TextView)findViewById(R.id.tv_header);
		//tv_header.setText(R.string.secondhand);
		
		Drawable d= getResources().getDrawable(R.drawable.loading); //xxx根据自己的情况获取drawable
		BitmapDrawable bd = (BitmapDrawable) d;
		default_bitmap = bd.getBitmap();
		
		searchButton = (ImageButton)findViewById(R.id.ib_search);
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SecondHandActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		
		categoryList = new ArrayList<Category>();
		
		listView = (ListView)findViewById(R.id.second_hand_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SecondHandActivity.this,GoodslistActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", categoryList.get(position).name);
				bundle.putString("categoryid", categoryList.get(position).id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		
		//listView.setAdapter(new ArrayAdapter<Category>(SecondHandActivity.this, android.R.layout.simple_list_item_1, catgoryList));
		
		mHandler = new MyHandler();
		new SetDataListThread().start();
	}
	/*
	@Override
	protected void onListItemClick(ListView listView,View v,int position,long id){
		Log.v("listView-onclick:",categoryList.get(position).name);
	}*/
	
	private class SetDataListThread extends Thread {
		
		@Override
		public synchronized void run() {
			
			try {
			
				HashMap p = new HashMap();
				JSONObject res = C.asyncPost(C.URLget_categories, p);
				
				if( !(res.getInt("status") == 0) ) {
					//mHandler.sendToast("网络有问题！");
					makeToast("网络有问题!!");
					return ;
				}
				
				JSONArray categories = res.getJSONArray("categories");
				System.out.println("categories:" + categories);
				//System.out.println(categories.length());
				if(Categorys.categorys != null)
					Categorys.categorys.clear();
				System.out.println("before for");
				for(int i=0;i<categories.length();i++) {
					JSONObject category = categories.getJSONObject(i);
					//System.out.println("category count:" + category.getString("items"));
					categoryList.add(new Category(
							category.getString("itemCount"),
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
					
					//Log.d("getcategory",category.getString("name")+category.getString("image"));
					
					
					//把种类信息保存起来
					
					Categorys.categorys.add(new Category(
							category.getString("itemCount"),
							category.getString("id"),
							category.getString("name"),
							category.getString("image")
							));
					
					
				}	
				//System.out.println("after for:"+categoryList);
				mHandler.sendEmptyMessage(0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				listView.setAdapter(new SecondHandAdapter(SecondHandActivity.this,categoryList));
				break;
			case 1:
				new SetDataListThread().start();
				break;
			case 3:
				break;
				
			/*case 44:
				tips = new ImageView[bitcount];
				for(int i=0;i<bitcount;i++){
					ImageView mImageView= new ImageView(SecondHandActivity.this);
					tips[i] = mImageView;
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					layoutParams.leftMargin = 3;
					//layoutParams.rightMargin = 3;
					
					listView.addView(mImageView,layoutParams);
				}
				new setBitmapsThread(urls).start();
				break;*/
			case 99:
				String toast = (String) msg.obj;
				//makeToast(toast);
				Toast.makeText(getApplicationContext(), toast,Toast.LENGTH_SHORT).show();
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
	
	//设置Bitmaps内容
	private class setBitmapsThread extends Thread{
		String[] picUrls;
		public setBitmapsThread(String[] picUrls){
			this.picUrls = picUrls;
		}
		@Override
		public void run(){
			for(int i = 0 ; i < picUrls.length ; i ++){
				
				new Request4Image(picUrls[i]) 
				{
					@Override
					protected void onPostExecute(Bitmap result) 
					{
						if(result==null) 
							return;
						//cachebitmap = result;
						for(int j = 0 ;j < picUrls.length ; j ++){
							if(bitmaps[j].equals(default_bitmap)){
								bitmaps[j] = result;
								break;
							}
						}
						
					}
				}.execute();
			}
		}
	}
	
}

