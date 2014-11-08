package com.example.tx.adapter;

import java.util.zip.Inflater;

import com.example.tx.R;
import com.example.tx.dto.Item;
import com.example.tx.dto.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderItemAdapter extends BaseAdapter{
	
	Context context;
	Order order;
	
	public OrderItemAdapter(Order order,Context context)
	{
		this.order=order;
		this.context=context;
	}

	@Override
	public int getCount() {
		return order.items.length;
	}

	@Override
	public Object getItem(int position) {
		return order.items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null)
		{
			vh=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_order_item, null);
			vh.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			vh.tv_price=(TextView) convertView.findViewById(R.id.tv_single_price);
			vh.tv_quantity=(TextView) convertView.findViewById(R.id.tv_quantity);
			vh.tv_total=(TextView) convertView.findViewById(R.id.tv_single_total);
			convertView.setTag(vh);
		}
		else
			vh=(ViewHolder) convertView.getTag();
		
		Item item=order.items[position];
		vh.tv_name.setText(item.itemName);
		vh.tv_price.setText("￥ "+String.valueOf(item.price));
		vh.tv_quantity.setText(String.valueOf(item.quantity));
		vh.tv_total.setText("￥ "+String.valueOf(item.price*item.quantity));
		
		return convertView;
	}
	
	class ViewHolder
	{
		TextView tv_name,tv_quantity,tv_price,tv_total;
	}

}
