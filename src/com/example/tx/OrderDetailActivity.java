package com.example.tx;

import com.example.tx.adapter.OrderItemAdapter;
import com.example.tx.dto.Order;
import com.example.tx.util.BaseActivity;
import com.example.tx.util.C;
import com.example.tx.view.MyListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity {
	
	private TextView tv_od_storename;              //商店名
	private TextView tv_od_contact;                //商店联系方式
	private TextView tv_od_time;                   //下单时间
	private MyListView lv_od_items;                //TODO 商品列表   orderDetailListAdapter
	private TextView tv_od_sum;                    //总价
	private TextView tv_od_username;               //下单人
	private TextView tv_od_address;                //下单人地址
	private TextView tv_od_tel;                    //下单人联系方式
	private TextView tv_od_remark;                 //备注

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detail);
		
		//
		tv_od_storename = (TextView) findViewById(R.id.tv_od_storename);
		tv_od_contact = (TextView) findViewById(R.id.tv_od_contact);
		tv_od_time = (TextView) findViewById(R.id.tv_od_time);
		tv_od_sum = (TextView) findViewById(R.id.tv_od_sum);
		tv_od_username = (TextView) findViewById(R.id.tv_od_username);
		tv_od_address = (TextView) findViewById(R.id.tv_od_address);
		tv_od_tel = (TextView) findViewById(R.id.tv_od_tel);
		tv_od_remark = (TextView) findViewById(R.id.tv_od_remark);
		lv_od_items = (MyListView) findViewById(R.id.lv_od_items);
		
		int position=getIntent().getIntExtra("position", -1);
		Order order;
		if(position!=-1)
			order=MineOrderActivity.that.msgs.get(position);
		else
		{
			makeToast("错误");
			order=new Order();
		}
		
		tv_od_storename.setText(order.shop.name);
		tv_od_contact.setText(order.shop.contact);
		tv_od_time.setText(order.time.split("T")[0]+" "+order.time.split("T")[1]);
		tv_od_sum.setText("￥ "+String.valueOf(order.totalPrice));
		tv_od_username.setText(getSharedPreferences("TaoxuePref", MODE_PRIVATE).getString("username", ""));
		tv_od_address.setText(order.address);
		tv_od_tel.setText(order.contact);
		tv_od_remark.setText(order.note);
		lv_od_items.setAdapter(new OrderItemAdapter(order,OrderDetailActivity.this));
		
		//
		((ImageButton) findViewById(R.id.ib_back_od)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		
		
		mHandler = new MyHandler();
	}
	
	static public MyHandler mHandler;
	
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order_detail, menu);
		return true;
	}

}
