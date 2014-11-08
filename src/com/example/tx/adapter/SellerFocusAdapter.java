package com.example.tx.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.dto.User;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.activity.MineSellerActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SellerFocusAdapter extends BaseAdapter implements OnScrollListener{
	
	private Context context;
	private List<User> sellers;
	private ListView dataListView;
	
	private int mFirstVisibleItem, mVisibleItemCount;
	private boolean isFirstEnter = true;
	User seller;
	ImageView pic;
	
	public SellerFocusAdapter(Context context,List<User> sellers,ListView dataListView)
	{
		super();
		this.context=context;
		this.sellers=sellers;
		this.dataListView=dataListView;
		dataListView.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		return sellers.size();
	}

	@Override
	public Object getItem(int position) {
		return sellers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		class ViewHolder{
			int flag=-1;
		}
		ViewHolder holder=new ViewHolder();
		if(convertView==null||(holder = (ViewHolder) convertView.getTag()).flag != position)
		{
			holder = new ViewHolder();
			holder.flag=position;
			
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_seller, null);
			seller=sellers.get(position);
			
			pic=(ImageView) convertView.findViewById(R.id.seller_avatar);
			Bitmap m=C.getBitmapFromMemCache(seller.getUserId());
			if(m==null)
			{
//				new Request4Image(seller.avatar)
//				{
//					@Override
//					protected void onPostExecute(Bitmap result) {
//						C.addBitmapToMemoryCache(seller.userId, result);
//						pic.setImageBitmap(result);
//					}
//				}.execute();
				pic.setImageResource(R.drawable.default_image);
			}
			else
			{
				pic.setImageBitmap(m);
			}
			((TextView)convertView.findViewById(R.id.seller_name)).setText(seller.userName);
			
			convertView.setTag(holder);
		}
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
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
			for (int i = firstVisibleItem; i < firstVisibleItem	+ visibleItemCount; i++) 
			{
				final User msg = sellers.get(i);

				Bitmap bitmap = C.getBitmapFromMemCache(msg.getUserId());
				
				final ImageView imageView = (ImageView)dataListView.getChildAt(i-firstVisibleItem).findViewById(R.id.seller_avatar);

				if (bitmap == null) 
				{
					//JSONObject p = new JSONObject();
					//p.put("UserId", msg.getUserId());
					new Request4Image(msg.avatar)
					{
						@Override
						protected void onPostExecute(Bitmap result) {
							if(result!=null)
							{
								C.addBitmapToMemoryCache(msg.userId, result);
								imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
								//notifyDataSetChanged();
							}
							return;
						}						
					}.execute();					
				} 
				else 
				{
					if (imageView != null) 
					{
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 153, 153, true));
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
