package com.example.tx.adapter;

import java.util.List;

import com.example.tx.R;
import com.example.tx.dto.StoreInSchool;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StoresInSchoolAdapter extends BaseAdapter {
	
	private Context context;
	private ListView lv_home;
	private List<StoreInSchool> msgs;
	
	public StoresInSchoolAdapter(Context context , ListView lv_home , List<StoreInSchool> li_stores){
		this.context = context;
		this.lv_home = lv_home;
		this.msgs = li_stores;
	}

	@Override
	public int getCount() {
		return msgs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != position ){
			holder = new ViewHolder();
			holder.flag = position;
			
			final StoreInSchool msg = msgs.get(position);
			
			//make View
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_store, null);
			
			TextView tv_storename = (TextView) convertView.findViewById(R.id.tv_storename);
			TextView tv_storedes = (TextView) convertView.findViewById(R.id.tv_storedes);
			final ImageView im_logo=(ImageView)convertView.findViewById(R.id.iv_store);
			
			tv_storename.setText(msg.name);
			tv_storedes.setText(msg.price+"元起送");
			if(msg.avatar!=null)
			{
				if(C.getBitmapFromMemCache(msg.shopId)==null)
					new Request4Image(msg.avatar) 
					{
						@Override
						protected void onPostExecute(Bitmap result) {
							im_logo.setImageBitmap(result);
							C.addBitmapToMemoryCache(msg.shopId, result);
						}
					}.execute();
				else
					im_logo.setImageBitmap(C.getBitmapFromMemCache(msg.shopId));
						
			}
			
			
			convertView.setTag(holder);
		}
		
		return convertView;
	}
	
	static class ViewHolder{
		int flag = -1;
	}

}
