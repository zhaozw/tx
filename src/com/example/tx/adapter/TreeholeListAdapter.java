package com.example.tx.adapter;

import java.util.List;

import com.example.tx.R;
import com.example.tx.activity.TreeholeCommentActivity;
import com.example.tx.dto.Talk;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class TreeholeListAdapter extends BaseAdapter implements OnScrollListener {
	
	public Context context;
	public List<Talk> msgs;
	
	public ListView dataListView;
	
	public TreeholeListAdapter(Context context,List<Talk> msgs,ListView dataListView){
		this.context = context;
		this.msgs = msgs;
		this.dataListView = dataListView;
		dataListView.setOnScrollListener(this);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != position) {
			holder = new ViewHolder();
			holder.flag = position;
			
			// make list item view
			
			
			final Talk msg = msgs.get(position);
			
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item_treehole, null);
			//convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			final ImageView pic = (ImageView) convertView
					.findViewById(R.id.iv_treeholeitem);
			
			//头像对应的图片没有给出id
			Bitmap b = C.getBitmapFromMemCache(msg.sender.userId);
			if (b == null) {
				//pic.setImageResource(R.drawable.item_pic_default);
				new Request4Image(msg.sender.avatar) 
				{
					@Override
					protected void onPostExecute(Bitmap result) 
					{
						if(result==null) 
							return;
						C.addBitmapToMemoryCache(msg.sender.userId, result);
						pic.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
						notifyDataSetChanged();
					}
				}.execute();
			} else {
				pic.setImageBitmap(b);
				new Request4Image(msg.sender.avatar) 
				{
					@Override
					protected void onPostExecute(Bitmap result) 
					{
						if(result==null) 
							return;
						C.addBitmapToMemoryCache(msg.sender.userId, result);
						pic.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
						notifyDataSetChanged();
					}
				}.execute();
			}
			
			TextView name = (TextView) convertView
					.findViewById(R.id.tv_treeholename);
			name.setText(msg.sender.userName);
			
			TextView time = (TextView) convertView
					.findViewById(R.id.tv_treeholetime);
			time.setText(msg.time);
			
			TextView content = (TextView) convertView
					.findViewById(R.id.tv_treeholecontent);
			content.setText(msg.content);
			
//			Button b_treehole_docomment = (Button)convertView
//					.findViewById(R.id.b_treehole_docomment);
//			b_treehole_docomment.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent();
//					intent.putExtra("index", position);
//					intent.setClass(context, TreeholeCommentActivity.class);
//					context.startActivity(intent);
//				}
//				
//			});
			
			
			
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
				final Talk msg = msgs.get(i);

				//TODO 这里希望user哩存在用户图片的id，用于缓存
				Bitmap bitmap = C.getBitmapFromMemCache(msg.sender.userId);
				//Bitmap bitmap = null;
				
				final ImageView imageView = (ImageView) dataListView
						.getChildAt(i - firstVisibleItem).findViewById(R.id.iv_treeholeitem);
				
				Log.d("talks","getimage"+(i - firstVisibleItem)+msg.sender.avatar);

				if (bitmap == null) {
					Log.d("talks","getimageqq");
					new Request4Image(msg.sender.avatar) 
					{
						@Override
						protected void onPostExecute(Bitmap result) 
						{
							Log.d("talks","getimage123");
							if(result==null) 
								return;
							Log.d("talks","getimage");
							C.addBitmapToMemoryCache(msg.sender.userId, result);
							if(imageView == null)
								Log.d("talks","getimageww");
							if(imageView != null)
								imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
							notifyDataSetChanged();
						}
					}.execute();
					
					//final String id = msg.image.picId;

					//根据url获取图片
					//Bitmap bitmap1 = BitmapFactory.decodeStream(new URL(msg.image.picUrl).openStream());
					//C.addBitmapToMemoryCache(id, bitmap1);
					//imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap1, 153, 153, true));
					//notifyDataSetChanged();
					
					
				} else {
					Log.d("talks","=-=-=-=-=");
					if (imageView != null && bitmap != null) {
						Log.d("talks","-------------");
						imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 153, 153, true));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
