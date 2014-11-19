package com.example.tx.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.ShoppingCart;
import com.example.tx.StoreItemDetailActivity;
import com.example.tx.StoreShopCart;
import com.example.tx.adapter.StoreCategoryGoodsAdapter;
import com.example.tx.dto.Image;
import com.example.tx.dto.Shop;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.SimpleOrder;
import com.example.tx.dto.SimpleStoreCategory;
import com.example.tx.dto.SimpleStoreGoods;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.view.MyExpandableListView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoreDetailActivity extends BaseActivity {
	
	public static StoreDetailActivity that = null;
	
	//控件
	private ImageButton ib_back_storedetail;
	private TextView ib_buy_storedetail;
	private MyExpandableListView elv_store_category;
	private TextView tv_store_name;
	
	//商店头信息
	private TextView tv_shop_bolletin;
	private TextView tv_shop_price;
	private TextView tv_shop_speed;
	private TextView tv_shop_time;
	private TextView tv_shop_contact;
	private TextView tv_shop_address;
	
	private TextView tv_total;
	public float totalPrice=(float)0.0;
	public SimpleOrder so=new SimpleOrder();
	
	//需要的数据
	public ArrayList<SimpleStoreCategory> groupData;
	public ArrayList<ArrayList<ShopItem>> childData;
	
	public String storename = "";
	public String shopId;
	public int index;
	public Shop theShop;
	
	//点击跳转到详情页，对应的商品
	public ShopItem theShopitem;
	
	StoreCategoryGoodsAdapter store_cate_adapter=null;
	
	Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_detail);
		
		that = this;
		
		//intent
		Bundle bundle = getIntent().getExtras();
		b=getIntent().getBundleExtra("store");
		storename = bundle.getString("name");
		shopId=bundle.getString("shopId");
		Log.d("cart-shopid",shopId);
		index = bundle.getInt("index");
		theShop = StoresActivity.that.list_shop.get(index);
		
		
		//初始化
		groupData = new ArrayList<SimpleStoreCategory>();
		childData = new ArrayList<ArrayList<ShopItem>>();
		
		//绑定控件
		ib_back_storedetail = (ImageButton) findViewById(R.id.ib_backstoredetail);
		ib_buy_storedetail = (TextView) findViewById(R.id.ib_buy_storedetail);
		elv_store_category = (MyExpandableListView) findViewById(R.id.elv_store_category);
		tv_store_name = (TextView) findViewById(R.id.tv_store_name);
		tv_total=(TextView)findViewById(R.id.tv_total);
		
		tv_shop_bolletin = (TextView) findViewById(R.id.tv_shop_bolletin);
		tv_shop_price = (TextView) findViewById(R.id.tv_shop_price);
		tv_shop_speed = (TextView) findViewById(R.id.tv_shop_speed);
		tv_shop_time = (TextView) findViewById(R.id.tv_shop_time);
		tv_shop_contact = (TextView) findViewById(R.id.tv_shop_contact);
		tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);
		
		//
		tv_shop_bolletin.setText("公告："+theShop.bulletin);
		tv_shop_price.setText("起送价："+theShop.price+"元起送");
		tv_shop_speed.setText("送货速度："+theShop.speed+"分钟");
		tv_shop_time.setText("送货时间："+theShop.timeStart.split("T")[1]+"-"+theShop.timeEnd.split("T")[1]);
		tv_shop_contact.setText("联系方式："+theShop.contact);
		tv_shop_address.setText("商店地址："+theShop.address);
		
		elv_store_category.setEmptyView(findViewById(R.id.tv_default));
		elv_store_category.setGroupIndicator(null);
		
		elv_store_category.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
		    @Override  
            public void onGroupCollapse(int groupPosition) {  
				if(store_cate_adapter!=null){
					store_cate_adapter.isOpen[groupPosition]=false;
					Log.d("open","close"+store_cate_adapter.isOpen[groupPosition]);
				}
            }  
		});
		elv_store_category.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				if(store_cate_adapter!=null){
					store_cate_adapter.isOpen[groupPosition]=true;
					Log.d("open",groupPosition+""+"open"+store_cate_adapter.isOpen[groupPosition]);
				}
			}
		});
          
		
		tv_store_name.setText(storename);
		
		//事件处理
		ib_back_storedetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		
		ib_buy_storedetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int totalcount=0;
				for(int i=0;i<so.items.size();i++)
					totalcount+=Integer.valueOf(so.items.get(i).get("quantity").toString());
				if(totalcount<=0)
				{
					new AlertDialog
					.Builder(StoreDetailActivity.this)
					.setTitle("提示")
					.setMessage("您还未购买任何商品，请先购买商品！")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}
				else if(totalPrice < theShop.price){
					new AlertDialog
					.Builder(StoreDetailActivity.this)
					.setTitle("提示")
					.setMessage("距离起送价还差"+ (theShop.price - totalPrice) +"元，只有达到起送价才能下单哦！！")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}
				else{
					Intent i = new Intent();
					i.setClass(StoreDetailActivity.this, StoreShopCart.class);
					so.shopId = shopId;
					i.putExtra("so", so);
					i.putExtra("store",b);
					i.putExtra("price", theShop.price);
					startActivity(i);
				}
			}
			
		});
		
		
		mHandler = new MyHandler();
		new SetDataThread().start();
	}
	
	static public MyHandler mHandler;
	
	public class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				store_cate_adapter=new StoreCategoryGoodsAdapter(getBaseContext(),groupData,childData,tv_total,elv_store_category);
				store_cate_adapter.isOpen=new boolean[groupData.size()];
				elv_store_category.setAdapter(store_cate_adapter);
				break;
			case 1://设置总价
				totalPrice=Float.parseFloat(msg.obj.toString());
				break;
				
				//跳转到详情页
			case 98:
				ShopItem si = (ShopItem) msg.obj;
				theShopitem = si;
				//makeToast(si.itemName);
				Intent intent = new Intent();
				intent.setClass(StoreDetailActivity.this, StoreItemDetailActivity.class);
				startActivity(intent);
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
			req.put("shopId", shopId);
			req.put("clazz", "");
			JSONObject res=C.asyncPost(C.URLget_shop_items, req);
			try
			{
				if(res.getInt("status")==0)
				{

					JSONObject ja=res.getJSONObject("items");
					Iterator it=ja.keys();
					while(it.hasNext())
					{
						String key=(String)it.next();
						ArrayList<ShopItem> siList=new ArrayList<ShopItem>();
						JSONArray items=ja.getJSONArray(key);
						for(int j=0;j<items.length();j++)
						{
							JSONObject o=items.getJSONObject(j);
							JSONArray imagesArray=o.getJSONArray("images");
							Image[] images=new Image[imagesArray.length()];
							for(int k=0;k<imagesArray.length();k++)
							{
								JSONObject oo=imagesArray.getJSONObject(k);
								images[k]=new Image(oo.getString("picId"),oo.getString("picUrl"));
							}
							ShopItem si=new ShopItem(o.getString("itemId"),o.getString("itemName"),(float)o.getDouble("price"),o.getString("details"),
									o.getString("clazz"),o.getString("shopId"),o.getInt("status"),images,null);
							siList.add(si);
						}
						groupData.add(new SimpleStoreCategory(key, items.length()));
						childData.add(siList);
					}
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
			
			Log.d("store",groupData.size()+"=="+childData.size());
			
			mHandler.sendEmptyMessage(0);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store_detail, menu);
		return true;
	}
}
