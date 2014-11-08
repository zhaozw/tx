package com.example.tx.adapter;

import java.util.ArrayList;

import com.example.tx.R;
import com.example.tx.adapter.MarketListAdapter.ViewHolder;
import com.example.tx.dto.ShopItem;
import com.example.tx.view.MyListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CartListAdapter extends BaseAdapter {
	
	public ArrayList<ShopItem> data;
	public Context context;
	public MyListView my_lv;
	
	public CartListAdapter(Context context,ArrayList<ShopItem> data){
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null|| (holder = (ViewHolder) convertView.getTag()).flag != index) {
			holder = new ViewHolder();
			holder.flag = index;
			
			//
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_goodsinstore, null);
			
			final ShopItem si = data.get(index);
			
			TextView tv_storegoodsname = (TextView) convertView.findViewById(R.id.tv_storegoodsname);
			TextView tv_storegoodsdes = (TextView) convertView.findViewById(R.id.tv_storegoodsdes);
			ImageButton ib_plus = (ImageButton) convertView.findViewById(R.id.ib_plus);
			final ImageButton ib_minus = (ImageButton) convertView.findViewById(R.id.ib_minus);
			final TextView et_num = (TextView) convertView.findViewById(R.id.et_num);
			final ImageView iv_logo=(ImageView)convertView.findViewById(R.id.iv_goods_left);
			
			//TODO
		}
		
		return convertView;
	}
	
	class ViewHolder{
		int flag = -1;
	}

}
