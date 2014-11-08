package com.example.tx.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.MineOrderActivity;
import com.example.tx.R;
import com.example.tx.dto.Order;
import com.example.tx.util.C;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyOrdersAdapter extends BaseAdapter{
	
	Context context;
	List<Order> msgs;
	
	public MyOrdersAdapter(Context context,List<Order> msgs)
	{
		this.context=context;
		this.msgs=msgs;
	}

	@Override
	public int getCount() {
		return msgs.size();
	}

	@Override
	public Object getItem(int position) {
		return msgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		final Order order=msgs.get(position);
		if(convertView==null || ((ViewHolder)convertView.getTag()).flag!=position)
		{
			vh=new ViewHolder();
			
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_order, null);
			
			vh.flag=position;
			vh.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
			vh.tv_name=(TextView) convertView.findViewById(R.id.tv_name);

			vh.iv_del=(ImageView)convertView.findViewById(R.id.iv_del);
			vh.iv_detail=(ImageView)convertView.findViewById(R.id.iv_detail);
			
			vh.iv_del.setTag(position);
			
			vh.iv_del.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					HashMap req=new HashMap();
					new Thread(new Runnable(){

						@Override
						public void run() {
							HashMap map = new HashMap();
							map.put("userId", C.userId);
							map.put("orderId", order.orderId);
							JSONObject res = C.asyncPost(C.URLdelete_shop_order, map);
							try {
								if(res.getInt("status") != 0){
									MineOrderActivity.that.mhandler.sendToast("连接失败");
									return;
								}
								MineOrderActivity.that.mhandler.sendEmptyMessage(1);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
					}).start();;
					
				}
			});

			
			
			vh.tv_time.setText(order.time.replace("T", " "));
			vh.tv_name.setText(order.shop.name);
			
			
			convertView.setTag(vh);
		}
		else
			vh=(ViewHolder) convertView.getTag();
		
		vh.iv_del.setVisibility((MineOrderActivity.that.editmodel)?View.VISIBLE:View.GONE);
		
		
		return convertView;
	}
	
	public class ViewHolder
	{
		int flag=-1;
		TextView tv_time,tv_name;
		ImageView iv_detail,iv_del;
	}
}
