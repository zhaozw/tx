package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.MarketListAdapter;
import com.example.tx.dto.Category;
import com.example.tx.dto.Item;
import com.example.tx.dto.Image;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


@SuppressLint("HandlerLeak")
public class SearchActivity extends BaseActivity {
	
	private final String TAG = "SearchActivity";
	
	public static boolean needRefresh = false;
	
	public static List<Item> msgs;
	private static ListView dataListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);


		
		dataListView = (ListView)findViewById(R.id.lv_search);
		dataListView.setEmptyView(findViewById(R.id.tv_search_empty));
		dataListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long id) {
				Intent intent = new Intent(SearchActivity.this, ItemDetailActivity.class);
				intent.putExtra("from", 2);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});
		
		//返回
		((ImageButton) findViewById(R.id.ib_back_search)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		
		((EditText) findViewById(R.id.et_search_key)).setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {

					EditText et_key = (EditText) findViewById(R.id.et_search_key);
					String key = et_key.getText().toString();
					
					if(key.equals("")){
						makeToast("您还未输入查询信息");
						return false;
					}
					
					boolean b = false;
					for(int i = 0 ;i < key.length() ; i ++){
						if(key.charAt(i) != ' '){
							b = true;
							break;
						}
					}
					if(b == false){
						makeToast("请输入内容");
						return false;
					}
					
					HashMap p = new HashMap();
					try {
						p.put("keyWord", key);
						p.put("order", 0);
						p.put("categoryId", "");
					} catch (Exception e) {
						e.printStackTrace();
					}
					new SetDataListThread(p).start();

					return true;
				}
				return false;
			}
		});
		
		mHandler = new MyHandler();
	}
	
	public static MyHandler mHandler;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				dataListView.setAdapter(new MarketListAdapter(SearchActivity.this, msgs, dataListView));
				break;
				
			case 1:
				try {
					HashMap p = (HashMap) msg.obj;
					new SetDataListThread(p).start();
				} catch (Exception e) {
					Log.v(TAG, "No param");
					e.printStackTrace();
					break;
				}
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
		
		private HashMap p;
		
		public SetDataListThread(HashMap p) {
			this.p = p;
		}
		
		@Override
		public void run() {
			msgs = new ArrayList<Item>();
			try {
				JSONObject res = C.asyncPost(C.URLget_items, p);
				if( !(res.getInt("status") == 0) ) {
					mHandler.sendToast("网络有问题！");
					return ;
				}
				
				
				
				JSONArray items = res.getJSONArray("items");
				
				Log.d("search", items.length()+"");
				
				for(int i=0;i<items.length();i++) {
					JSONObject item=items.getJSONObject(i);
					
					JSONArray a=item.getJSONArray("images");
					List<Image> images=new ArrayList<Image>();
					Log.d("search", a.length()+" images");
					for(int j=0;j<a.length();j++)
					{
						JSONObject oo=a.getJSONObject(j);
						images.add(new Image(oo.getString("picId"),oo.getString("picUrl")));
					}
					
					JSONObject so=item.getJSONObject("seller");
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
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mHandler.sendEmptyMessage(0);
		}
		
	}

}
