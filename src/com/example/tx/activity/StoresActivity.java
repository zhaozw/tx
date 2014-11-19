package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.adapter.StoresInSchoolAdapter;
import com.example.tx.dto.Shop;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.StoreInSchool;
import com.example.tx.dto.User;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class StoresActivity extends BaseActivity {
	
	public static StoresActivity that;
	
	//内部控件
	private ImageButton ib_back_stores;
	private ListView lv_stores;
	private TextView tv_stores_header;
	
	//商店列表
	private List<StoreInSchool> msgs;
	public List<Shop> list_shop;
	
	private int type;
	
	Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stores);
		
		that = this;
		
		//初始化变量
		msgs = new ArrayList<StoreInSchool>();
		
		Intent it=getIntent();
		b=it.getBundleExtra("store");
		type=b.getInt("type", -1);
		
		//绑定控件
		ib_back_stores = (ImageButton) findViewById(R.id.ib_back_stores);
		lv_stores = (ListView) findViewById(R.id.lv_stores);
		tv_stores_header = (TextView) findViewById(R.id.tv_stores_header);
		// 商铺类型，0表示大学生商铺，1表示校内商铺，2表示校外商铺
		if(type == 0){
			tv_stores_header.setText("大学生商铺");
		}
		else if(type == 1){
			tv_stores_header.setText("校内商铺");
		}
		else if(type == 2){
			tv_stores_header.setText("校外商铺");
		}
			
		
		
		//事件chuli 
		ib_back_stores.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}
			
		});
		
		lv_stores.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("name", msgs.get(arg2).name);
				bundle.putString("shopId",msgs.get(arg2).shopId);
				bundle.putInt("index",arg2);
				intent.putExtras(bundle);
				intent.putExtra("store", b);
				intent.setClass(StoresActivity.this, StoreDetailActivity.class);
				startActivity(intent);
			}
			
		});
		
		mHandler = new MyHandler();
		
		new SetDataThread().start();
	}
	
	static public MyHandler mHandler;
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				lv_stores.setAdapter(new StoresInSchoolAdapter(getBaseContext(),lv_stores,msgs));;
				break;
			case 99:
				makeToast((String)msg.obj);
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
	
	private class SetDataThread extends Thread{

		@Override
		public void run() {
			HashMap req=new HashMap();
			req.put("userId",C.userId);
			
			//获取经纬度
			LocationManager loctionManager;
			String contextService=Context.LOCATION_SERVICE;
			loctionManager=(LocationManager) getSystemService(contextService);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
			criteria.setAltitudeRequired(false);//不要求海拔
			criteria.setBearingRequired(false);//不要求方位
			criteria.setCostAllowed(true);//允许有花费
			criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
			//从可用的位置提供器中，匹配以上标准的最佳提供器
			String provider = null;
			//获得最后一次变化的位置
			Location location=null;
			
			try
			{
				if(loctionManager != null)
					provider= loctionManager.getBestProvider(criteria, true);
				if(provider != null)
					location=loctionManager.getLastKnownLocation(provider);
				if(location != null)
					req.put("location", location.getLongitude()+","+location.getLatitude());
				else
					req.put("location", "");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				makeToast("获取地址信息失败");
				req.put("location","116.322047,39.963808");//116.322047,39.963808  39.983424,116.322987
			}
//			req.put("location","116.322047,39.963808");//116.322047,39.963808  39.983424,116.322987
			req.put("type", type);
			JSONObject res=C.asyncPost(C.URLget_shops, req);
			list_shop=new ArrayList<Shop>();
			try
			{
				if(res.getInt("status")==0)
				{
					JSONArray shops=res.getJSONArray("shops");
					for(int i=0;i<shops.length();i++)
					{
						JSONObject shop=(JSONObject) shops.get(i);
						String logo=null;
						if(shop.has("logo"))
							logo=shop.getString("logo");
						String target=null;
						StoreInSchool sis=new StoreInSchool(shop.getString("shopId"),shop.getString("name"),shop.getString("introduction"),logo);
						sis.setPrice(String.valueOf(shop.getDouble("price")));
						msgs.add(sis);
						
//						String shopId,String category,String name,String address,String introduction,String registerTime,String logo,
//						String background,String ownerId,String bulletin,int speed,String timeStart,String timeEnd,int type,String contact,
//						float price,String target,int orderType,String other,User owner,String[] classes,ShopItem[] items
//						JSONArray classesja=shop.getJSONArray("classes");
//						String[] classes=new String[classesja.length()];
//						for(int j=0;j<classesja.length();j++)
//						{
//							classes[j]=(String)classesja.get(j);
//						}
						list_shop.add(new Shop(shop.getString("shopId"),shop.getString("category"),shop.getString("name"),shop.getString("address"),shop.getString("introduction"),
								shop.getString("registerTime"),logo,null,shop.getString("ownerId"),shop.getString("bulletin"),
								shop.getInt("speed"),shop.getString("timeStart"),shop.getString("timeEnd"),shop.getInt("type"),shop.getString("contact"),
								(float)shop.getDouble("price"),shop.has("target")?shop.getString("target"):null,shop.getInt("orderType"),null,null,null,null));
					}
					mHandler.sendEmptyMessage(0);
				}
				else
				{
					mHandler.sendToast("请求错误");
				}
			}
			catch(Exception e)
			{
				mHandler.sendToast("后台错误");
				e.printStackTrace();
			}
			
//			msgs.add(new StoreInSchool("理工超市","20元起送，其他地区不送",null));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stores, menu);
		return true;
	}

}
