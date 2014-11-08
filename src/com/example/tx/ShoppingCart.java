package com.example.tx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.example.tx.activity.StoreDetailActivity;
import com.example.tx.activity.StoresActivity;
import com.example.tx.adapter.ShoppingCartAdapter;
import com.example.tx.dto.Shop;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.SimpleOrder;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingCart extends BaseActivity {

	SimpleOrder so;
	List<ArrayList<ShopItem>> list_shop;
	List<ShopItem> order_item;
	ListView lv_goods;
	
	TextView tv_remark,tv_location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shopping_cart);
		
		Intent preIt=getIntent();
		so=(SimpleOrder) preIt.getSerializableExtra("so");
		list_shop=StoreDetailActivity.that.childData;
		
		lv_goods=(ListView)findViewById(R.id.lv_goods);
		TextView tv_phone=(TextView)findViewById(R.id.tv_phone);
		tv_location=(TextView)findViewById(R.id.tv_location);
		TextView tv_submit=(TextView)findViewById(R.id.ib_buy_storedetail);
		tv_remark=(TextView)findViewById(R.id.et_remark);
		
		
		if(C.logged == false){
			makeToast("您还未登陆！请返回登陆。");
			finish();
		}
		
		so.userId=C.userId;
		so.address=C.location;
		so.contact=C.account;
		
		tv_phone.setText(C.account);
		tv_location.setText(C.location);
		tv_remark.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it=new Intent(ShoppingCart.this,OrderInfoModifyActivity.class);
				it.putExtra("head", "添加备注");
				it.putExtra("value", "");
				startActivityForResult(it, 0);
			}
		});
		tv_location.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it=new Intent(ShoppingCart.this,OrderInfoModifyActivity.class);
				it.putExtra("head","地址");
				it.putExtra("value",tv_location.getText().toString());
				startActivityForResult(it, 1);
			}
		});
		tv_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap req=new HashMap();
				req.put("userId", so.userId);
				req.put("shopId", so.shopId);
				req.put("address", so.address);
				req.put("contact", so.contact);
				req.put("note",so.note);
				
				//
				Log.d("cart", so.userId+"#"+so.shopId+"#"+so.address+"#"+so.contact+"#"+so.note);
				
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
				new PostTask(C.URLcreate_order, req)
				{
					@Override
					protected void onPostExecute(JSONObject result) {
						try
						{
							if(result.getInt("status")==0)
							{
								new AlertDialog
								.Builder(ShoppingCart.this)
								.setTitle("提示")
								.setMessage("您已成功提交订单！您可以在“我”->“我的订单”中管理和产看订单哦")
								.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								})
								.show();
							}
							else
								makeToast(result.getString("description"));
						}
						catch(Exception e)
						{
							makeToast("后台错误");
							e.printStackTrace();
						}
					}
				}.execute();
			}
		});
		order_item=new ArrayList<ShopItem>();
		mHandler=new MyHandler();
		new SetDataThread().start();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode==RESULT_OK)
		{
			switch(requestCode)
			{
			case 0:
				String remark=data.getStringExtra("result");
				tv_remark.setText(remark);
				so.note=remark;
				break;
			case 1:
				String location=data.getStringExtra("result");
				tv_location.setText(location);
				so.address=location;
				break;
			}
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
	
	public static MyHandler mHandler;
	
	class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				lv_goods.setAdapter(new ShoppingCartAdapter(ShoppingCart.this,order_item,so));
				break;
			}
		}
	}
}
