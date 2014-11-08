package com.example.tx.adapter;

import java.util.List;

import com.example.tx.R;
import com.example.tx.adapter.MarketListAdapter.ViewHolder;
import com.example.tx.dto.Category;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryMarketAdapter extends BaseAdapter {
	
	private List<Category> msgs;
	private Context context;
	private GridView gv;
	private int[] drawables = {
			R.drawable.k1,
			R.drawable.k2,
			R.drawable.k3,
			R.drawable.k4,
			R.drawable.k5,
			R.drawable.k6,
			R.drawable.k7,
			R.drawable.k8,
			R.drawable.k9,
			R.drawable.k10,
			R.drawable.k11,
			R.drawable.k12,
			R.drawable.k13,
			R.drawable.k14,
			R.drawable.k15,
			R.drawable.k16};
	private int k = 15;
	
	public CategoryMarketAdapter(Context context,List<Category> msgs,GridView gv){
		this.msgs = msgs;
		this.context = context;
		this.gv = gv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		
		if(convertView==null|| (holder = (ViewHolder) convertView.getTag()).flag != position)
		{
			holder = new ViewHolder();
			holder.flag = position;
			
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category, null);
			//convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			Category msg= msgs.get(position);
			
			((TextView)convertView.findViewById(R.id.tv_categoryitem)).setText(msg.name);
			//Log.d("getcategory",msg.name+position+msg.id);
			
			Bitmap bitmap = C.getBitmapFromMemCache(msg.id);
			final ImageView iv_category = (ImageView)convertView.findViewById(R.id.iv_categoryitem);
			
			if(bitmap == null){
				//iv_category.setImageResource(R.drawable.k1);
				int p = position;
				if(position > k)
					p = k;
				Drawable d= context.getResources().getDrawable(drawables[p]); //xxx根据自己的情况获取drawable
				BitmapDrawable bd = (BitmapDrawable) d;
				Bitmap bm = bd.getBitmap();
				iv_category.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 120, true));
				final String id = msg.id;
				new Request4Image(msg.imageurl){

					@Override
					protected void onPostExecute(Bitmap result) {
						// TODO Auto-generated method stub
						if(result == null)
							return;
						
						C.addBitmapToMemoryCache(id, result);
						iv_category.setImageBitmap(Bitmap.createScaledBitmap(result, 120, 120, true));
						//result.recycle();
						notifyDataSetChanged();
					}
					
				}.execute();
			}else if(iv_category != null){
				iv_category.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, true));
			}
			convertView.setTag(holder);
		}
		
		return convertView;
	}
	
	static class ViewHolder {

		int flag = -1;
	}

}
