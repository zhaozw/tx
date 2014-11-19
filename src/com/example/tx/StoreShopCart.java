package com.example.tx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.ShoppingCart.SetDataThread;
import com.example.tx.activity.SecondHandActivity;
import com.example.tx.activity.MainActivity;
import com.example.tx.activity.StoreDetailActivity;
import com.example.tx.activity.StoresActivity;
import com.example.tx.adapter.CartListAdapter;
import com.example.tx.adapter.ShoppingCartAdapter;
import com.example.tx.adapter.StoresInSchoolAdapter;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.SimpleOrder;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.view.MyListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class StoreShopCart extends BaseActivity {
	
	SimpleOrder so;
	List<ArrayList<ShopItem>> list_shop;
	List<ShopItem> order_item;
	//ListView lv_goods;
	
	private TextView tv_address;
	private TextView tv_addremark;
	private MyListView lv_cart;
	private TextView tv_contact,tv_submit;
	
	private ArrayList<ShopItem> data;
	
	private float price;
	Bundle b;
	boolean flag=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_shop_cart);
		
		Intent preIt=getIntent();
		so=(SimpleOrder) preIt.getSerializableExtra("so");
		price = preIt.getFloatExtra("price", (float) 0.0);
		list_shop=StoreDetailActivity.that.childData;
		
		b=preIt.getBundleExtra("store");
		
		//
		tv_address = (TextView) findViewById(R.id.tv_cartaddress);//送货地址
		tv_addremark = (TextView) findViewById(R.id.tv_addremark);//备注信息
		lv_cart = (MyListView) findViewById(R.id.lv_mycart);//订单里的列表
		tv_contact = (TextView) findViewById(R.id.tv_contact);//联系方式
		
		so.userId=C.userId;
		so.address=C.location;
		so.contact=C.account;
		
		
		if(C.logged == false){
			makeToast("您还未登陆！请返回登陆。");
			finish();
		}
		tv_contact.setText(C.account);
		tv_address.setText(C.location);
		
		//
		((ImageButton) findViewById(R.id.ib_back_cart)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		tv_submit=((TextView) findViewById(R.id.ib_submit));
		tv_submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO 提交订单
				so.contact = tv_contact.getText().toString();
				//判断总价是否大于起送价
				if(so.totalPrice < price){
					new AlertDialog
					.Builder(StoreShopCart.this)
					.setTitle("提示")
					.setMessage("距离起送价还差"+ (price - so.totalPrice) +"元，只有达到起送价才能下单哦！！")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
					return;
				}
				int totalcount=0;
				for(int i=0;i<so.items.size();i++)
					totalcount+=Integer.valueOf(((HashMap)so.items.get(i)).get("quantity").toString());
				if(totalcount<=0)
				{
					new AlertDialog
					.Builder(StoreShopCart.this)
					.setTitle("提示")
					.setMessage("请先选择商品再下单")
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
					return;
				}
				//判断手机号和地址
				if(so.contact.length() != 11){
					makeToast("请输入11位手机号码");
					return;
				}
				if(so.address.equals("")){
					makeToast("请输入地址");
					return;
				}
				
				if(flag)
					return;
				flag=true;
				makeToast("正在提交订单");
				HashMap req=new HashMap();
				req.put("userId", so.userId);
				req.put("shopId", so.shopId);
				req.put("address", so.address);
				req.put("contact", so.contact);
				if(so.note!=null)
					req.put("note",so.note);
				else
					req.put("note", "");
				
				//以下注释的一些内容是我尝试的解法 yu
//				String[] ids = new String[so.items.size()];
//				int[] nums = new int[so.items.size()];
				
				//
				Log.d("cart", so.userId+"#"+so.shopId+"#"+so.address+"#"+so.contact+"#"+so.note);
				
//				StringBuffer itemIds=new StringBuffer("[");
//				StringBuffer quantities=new StringBuffer("[");
//				int i = 0;
//				for(HashMap m:so.items)
//				{
////					ids[i] = m.get("itemId").toString();
////					nums[i++] = Integer.parseInt(m.get("quantity").toString());
//					Log.d("cart",m.get("itemId").toString()+"买了"+m.get("quantity").toString());
//					itemIds.append(m.get("itemId").toString());
//					itemIds.append(",");
//					quantities.append(m.get("quantity").toString());
//					quantities.append(",");
//				}
//				itemIds.deleteCharAt(itemIds.length()-1);
//				itemIds.append("]");
//				quantities.deleteCharAt(quantities.length()-1);
//				quantities.append("]");
				String[] itemIds=new String[so.items.size()];
				String[] quantities=new String[so.items.size()];
				for(int i=0;i<so.items.size();i++)
				{
					HashMap m=so.items.get(i);
					itemIds[i]=m.get("itemId").toString();
					quantities[i]=m.get("quantity").toString();
				}
				req.put("itemIds", itemIds);
				req.put("quantities", quantities);
//				req.put("itemIds", ids);
//				req.put("quantities", nums);
//				new CreatOrderThread(req).start();
				new PostTask(C.URLcreate_order, req)
				{
					@Override
					protected void onPostExecute(JSONObject result) {
						try
						{
							if(result.getInt("status")==0)
							{
								new AlertDialog
								.Builder(StoreShopCart.this)
								.setTitle("提示")
								.setMessage("您已成功提交订单！您可以在“我”->“我的订单”中管理和产看订单哦")
								.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
										StoreDetailActivity.that.finish();
//										StoresActivity.that.finish();
										finish();
//										Intent it=new Intent(StoreShopCart.this,MainActivity.class);
//										it.putExtra("store", b);
//										startActivity(it);
									}
								})
								.show();
							}
							else
							{
								makeToast(result.getString("description")+",状态码:"+result.getInt("status"));
								flag=false;
							}
						}
						catch(Exception e)
						{
							flag=false;
							makeToast("后台错误");
							e.printStackTrace();
						}
					}
				}.execute();
			}
			
		});
		
		tv_address.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString("address",tv_address.getText().toString());
				i.putExtras(b);
				i.setClass(StoreShopCart.this, StoreChangeAddress.class);
				startActivityForResult(i,2);
			}
			
		});
		
		tv_addremark.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString("remark",tv_addremark.getText().toString());
				i.putExtras(b);
				i.setClass(StoreShopCart.this, AddRemarkActivity.class);
				startActivityForResult(i,3);
			}
			
		});
		
		order_item=new ArrayList<ShopItem>();
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
				lv_cart.setAdapter(new ShoppingCartAdapter(StoreShopCart.this,order_item,so));
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
	
	protected class SetDataThread extends Thread
	{
		@Override
		public void run()
		{
			for(HashMap m:so.items)
			{
				label:for(ArrayList<ShopItem> al:list_shop)
				{
					for(ShopItem si:al)
						if(si.itemId.equals((String)m.get("itemId")))
						{
							order_item.add(si);
							break label;
						}
				}
			}
			mHandler.sendEmptyMessage(0);
//			lv_goods.setAdapter(new ShoppingCartAdapter(ShoppingCart.this,order_item,so));
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
			case 3:
				String remark=data.getStringExtra("result");
				tv_addremark.setText(remark);
				so.note=remark;
				break;
			case 2:
				String location=data.getStringExtra("result");
				location = location.replace(" ", "");
				tv_address.setText(location);
				so.address=location;
				break;
			}
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store_shop_cart, menu);
		return true;
	}

}
