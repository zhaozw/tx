//商品列表的adapter

package com.example.tx.adapter;

import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.dto.Item;
import com.example.tx.R;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.view.RefreshListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class MarketListAdapter extends BaseAdapter implements OnScrollListener {
	protected static final String TAG = "MarketListAdapter";

	private Context context;
	private List<Item> msgs;

	private  ListView dataListView;

	public MarketListAdapter(Context context, List<Item> messages,
			 ListView dataListView) {
		super();
		this.context = context;
		this.msgs = messages;
		this.dataListView = dataListView;
		dataListView.setOnScrollListener(this);
		
//		loadBitmaps(dataListView.getFirstVisiblePosition(), dataListView.getLastVisiblePosition());
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
	public View getView(int index, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null|| (holder = (ViewHolder) convertView.getTag()).flag != index) {
			holder = new ViewHolder();
			holder.flag = index;

			// make list item view
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_market, null);

			Item msg = msgs.get(index);

			ImageView pic = (ImageView) convertView.findViewById(R.id.iv_item_pic);
			Bitmap bitmap = null;
			if(msg.image.size() == 0){
				Drawable d= context.getResources().getDrawable(R.drawable.default_image); //xxx根据自己的情况获取drawable
				BitmapDrawable bd = (BitmapDrawable) d;
				Bitmap bm = bd.getBitmap();
				bitmap = bm;
			}
			else
				bitmap = C.getBitmapFromMemCache(msg.image.get(0).picId); 
			if (bitmap == null) {
				pic.setImageResource(R.drawable.default_image);
			} else {
				pic.setImageBitmap(bitmap);
			}
			
			TextView price = (TextView) convertView.findViewById(R.id.tv_item_price);
			price.setText("¥" + msg.price);
			
			Log.d("debug",msg.price+"");
			
			TextView time = (TextView) convertView.findViewById(R.id.tv_llitem_time);
			//time.setTextSize(15);
			String t = msg.releaseTime;
			t = t.replace('T', ' ');
			time.setText(t);
			Log.d("debug",t);

			TextView name = (TextView) convertView.findViewById(R.id.tv_item_name);
			name.setText(msg.itemName);

			TextView position = (TextView) convertView.findViewById(R.id.tv_item_position);
			String l = msg.seller.location;
			if(l.equals(""))
				l = "未知";
			position.setText(l);

			TextView owner = (TextView) convertView.findViewById(R.id.tv_item_owner);
			owner.setText(msg.seller.userName);

			

			convertView.setTag(holder);

		}

		return convertView;
	}

	static class ViewHolder {

		int flag = -1;
	}

	
	private int mFirstVisibleItem, mVisibleItemCount;
	private boolean isFirstEnter = true;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		Log.e("a", "" + scrollState+","+mFirstVisibleItem+" - "+mVisibleItemCount);
		// 仅当ListView静止时才去下载图片
		if (scrollState == SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				Item msg = msgs.get(i);
				Bitmap bitmap = null;
				if(msg.image.size() == 0){
					Drawable d= context.getResources().getDrawable(R.drawable.default_image); //xxx根据自己的情况获取drawable
					BitmapDrawable bd = (BitmapDrawable) d;
					Bitmap bm = bd.getBitmap();
					bitmap = bm;
					
				}
				else{
					bitmap = C.getBitmapFromMemCache(msg.image.get(0).picId);
					Log.d("image",i+"---"+msg.image.get(0).picUrl+"---"+msg.image.get(0).picId);
				}
				
				final ImageView imageView = (ImageView) dataListView
						.getChildAt(i - firstVisibleItem).findViewById(R.id.iv_item_pic);

				if (bitmap == null) {
					for(int j = 0 ; j < msg.image.size() && j < 1 ; j ++){
						final String id = msg.image.get(j).picId;
	
						//根据url获取图片
						new Request4Image(msg.image.get(j).picUrl) 
						{
							@Override
							protected void onPostExecute(Bitmap result) 
							{
								if(result==null) 
									return;
								C.addBitmapToMemoryCache(id, result);
								imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
								//notifyDataSetChanged();
							}
						}.execute();
					}
					
					
					
				} else {
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 153, 153, true));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
