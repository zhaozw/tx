package com.example.tx.adapter;

import java.util.List;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tx.R;
import com.example.tx.dto.Category;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

public class SecondHandAdapter extends BaseAdapter{

	//private LayoutInflater mInflater;
	private Context context;
	private List<Category> categorylist;
	//private static boolean isShow[];   //这个用来防止list中同一个分类显示多次
	
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
	
	public SecondHandAdapter(Context context,List<Category> msgs){
		this.context = context;
		categorylist = msgs;
		//isShow = new boolean[msgs.size()];
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categorylist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return categorylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null || (holder = (ViewHolder)convertView.getTag()).flag != position ){
			holder = new ViewHolder();
			//isShow[position] = true; //该分类已经显示了
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
			
			Category category = categorylist.get(position);
			
			
			holder.imageView = (ImageView)convertView.findViewById(R.id.list_item_image);
			holder.titleView = (TextView)convertView.findViewById(R.id.list_item_title);
			holder.amountView = (TextView)convertView.findViewById(R.id.list_item_amount);
			
			Bitmap bitmap = C.getBitmapFromMemCache(category.id);
			final ImageView imageView = (ImageView)convertView.findViewById(R.id.list_item_image);
			//获取该分类的图片
			if(bitmap == null){
				int p = position;
				if(position > k)
					p = k;
				Drawable d = context.getResources().getDrawable(drawables[p]);
				BitmapDrawable bd = (BitmapDrawable)d;
				Bitmap bm = bd.getBitmap();
				imageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 50, 50, true));
				final String id = category.id;
				new Request4Image(category.imageurl) {
					
					@Override
					protected void onPostExecute(Bitmap result) {
						// TODO Auto-generated method stub
						if(result == null)
							return;
						C.addBitmapToMemoryCache(id, result);
						imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 50, 50, true));
						notifyDataSetChanged();
					}
				}.execute();
			}else if(imageView != null){
				imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 50, 50, true));
			}
			holder.imageView = imageView;
			holder.titleView.setText(category.name);
			//System.out.println(holder.amountView.getText().toString()+category.itemcount);
			String formateString = holder.amountView.getText().toString();
			String items = String.format(formateString, new String(category.itemcount));
			//System.out.println("items:"+items);
		
			holder.amountView.setText(items);
			
			convertView.setTag(holder);
		}
		
		return convertView;
	}

	public final class ViewHolder{
		public ImageView imageView;
		public TextView  titleView;
		public TextView  amountView;
		int flag = -1;
	}
	
}


