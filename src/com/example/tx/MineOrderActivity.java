package com.example.tx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tx.adapter.MyOrdersAdapter;
import com.example.tx.dto.Item;
import com.example.tx.dto.Order;
import com.example.tx.dto.Shop;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MineOrderActivity extends BaseActivity {

	public MyHandler mhandler;
	ListView lv_order;
	public List<Order> msgs;
	
	public static MineOrderActivity that;
	
	public boolean editmodel = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mine_order);
		that=this;
		
		editmodel=false;
		
		ImageButton ib_return=(ImageButton)findViewById(R.id.ib_return);
		lv_order=(ListView)findViewById(R.id.lv_order);
		final TextView tv_edit=(TextView)findViewById(R.id.tv_edit);
		
		ib_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_edit.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v) {
			// TODO 自动生成的方法存根
				if(editmodel){
					editmodel = false;
					((MyOrdersAdapter)(lv_order.getAdapter())).notifyDataSetChanged();
					tv_edit.setText("编辑");
				}else{
					editmodel = true;
					tv_edit.setText("完成");
					((MyOrdersAdapter)(lv_order.getAdapter())).notifyDataSetChanged();
				}
			}
		});
		lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it=new Intent(MineOrderActivity.this,OrderDetailActivity.class);
				it.putExtra("position", position);
				startActivity(it);
			}
		});
		msgs=new ArrayList<Order>();
		mhandler=new MyHandler();
		new SetDataThread().start();
	}
	public class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case 0:
				lv_order.setAdapter(new MyOrdersAdapter(MineOrderActivity.this, msgs));
				break;
			case 1:
				new SetDataThread().start();
				break;
			case 99:
				makeToast((String)msg.obj);
				break;
			}
		}
		public void sendToast(String toast)
		{
			Message msg=mhandler.obtainMessage();
			msg.what=99;
			msg.obj=toast;
			mhandler.sendMessage(msg);
		}
	}
	
	public class SetDataThread extends Thread
	{
		@Override
		public void run()
		{
			msgs = new ArrayList<Order>();
			HashMap req=new HashMap();
			req.put("userId", C.userId);
			JSONObject res=C.asyncPost(C.URLget_my_orders, req);
			try
			{
				if(res.getInt("status")==0)
				{
					JSONArray ja=res.getJSONArray("orders");
					for(int i=0;i<ja.length();i++)
					{
						JSONObject o=ja.getJSONObject(i);
						Order or=new Order();
						or.orderId=o.getString("orderId");
						or.time=o.getString("time");
						or.shopId=o.getString("shopId");//String          // 商铺ID
						or.userId=o.getString("userId");//String          // 买家ID
						or.address=o.getString("address");//String         // 配送地址
						or.contact=o.getString("contact");//String         // 买家联系方式
						or.status=o.getInt("status");//int         // 订单状态，0表示确认状态，1表示已被用户删除（对用户不可见）
						or.note=o.getString("note");//String            // 备注

						JSONArray itemsJa=o.getJSONArray("items");
						or.items=new Item[itemsJa.length()];//[
						for(int j=0;j<itemsJa.length();j++)
						{
							JSONObject itemJo=itemsJa.getJSONObject(j);
							JSONObject itemD=itemJo.getJSONObject("item");
							Item item=new Item("",itemD.getString("itemName"),(float)itemD.getDouble("price"),"","","","",null,null,itemJo.getInt("quantity"));
							or.items[j]=item;
						}
						
					    or.totalPrice=(float)o.getDouble("totalPrice");//float       // 总价
					    
					    JSONObject so=o.getJSONObject("shop");
					    Shop shop=new Shop();
					    shop.name=so.getString("name");
					    shop.contact=so.getString("contact");
					    
//					    JSONObject user = o.getJSONObject("user");
//					    User u = new User();
//					    u.userName = user.getString("userName");
//					    String name = o.getString("userName");
					    
					    or.shop=shop;
					    or.user=null;
					    msgs.add(or);
					}
					mhandler.sendEmptyMessage(0);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				mhandler.sendToast("后台错误");
			}
		}
	}
}
