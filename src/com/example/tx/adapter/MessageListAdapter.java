package com.example.tx.adapter;

import java.net.URL;
import java.util.List;

import com.example.tx.R;
import com.example.tx.adapter.MarketListAdapter.ViewHolder;
import com.example.tx.dto.Message;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {
	protected static final String TAG = "MarketListAdapter";

	private Context context;
	private List<Message> msgs;

	private ListView dataListView;

	public MessageListAdapter(Context context, List<Message> messages,
			ListView dataListView) {
		super();
		this.context = context;
		this.msgs = messages;
		this.dataListView = dataListView;
		
//		loadBitmaps(dataListView.getFirstVisiblePosition(), dataListView.getLastVisiblePosition());
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
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != index) {
			holder = new ViewHolder();
			holder.flag = index;

			// make list item view
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_message, null);

			final Message msg = msgs.get(index);

			final ImageView pic = (ImageView) convertView
					.findViewById(R.id.iv_message);
			
			Bitmap bitmap = null;
			
			bitmap = C.getBitmapFromMemCache(msg.senderId);
			//TODO消息没有给出图片url以及id
			
			//根据url获取图片
			if(bitmap == null){
				new Request4Image(msg.sender.avatar) 
				{
					@Override
					protected void onPostExecute(Bitmap result) 
					{
						if(result==null) 
							return;
						C.addBitmapToMemoryCache(msg.senderId, result);
						pic.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
						//notifyDataSetChanged();
					}
				}.execute();
			}
			else if(pic != null && bitmap != null){
				pic.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 153, 153, true));
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.tv_message_name);
			name.setText(msg.sender.userName);

			TextView price = (TextView) convertView
					.findViewById(R.id.tv_message_content);
			price.setText(msg.content);

			convertView.setTag(holder);

		}

		return convertView;
	}
	
	static class ViewHolder {

		int flag = -1;
	}


}
