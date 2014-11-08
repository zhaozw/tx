package com.example.tx.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.activity.MineCommodityDetailActivity;
import com.example.tx.adapter.MarketListAdapter.ViewHolder;
import com.example.tx.dto.Item;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.view.ButtonInsideListViewItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyCommodityAdapter extends BaseAdapter implements OnScrollListener
{
	private Context context;
	private List<Item> msgs;
	private ListView dataListView;
	
	private int mFirstVisibleItem, mVisibleItemCount;
	private boolean isFirstEnter = true;
	
	public MyCommodityAdapter(Context context, List<Item> messages,ListView dataListView)
	{
		this.context=context;
		this.msgs=messages;
		this.dataListView=dataListView;
		dataListView.setOnScrollListener(this);
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
		class ViewHolder{
			int flag=-1;
		}
		ViewHolder holder=new ViewHolder();
		if(convertView == null|| (holder = (ViewHolder) convertView.getTag()).flag != position)
		{
			holder = new ViewHolder();
			holder.flag=position;
			//让item能在view中显示
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_mine_commodity, null);
			
			Item msg = msgs.get(position);
			
			ImageView pic=(ImageView) convertView.findViewById(R.id.list_commodity_pic);
			Bitmap m=C.getBitmapFromMemCache(msg.image.get(0).picId);
			if(m==null)
				pic.setImageResource(R.drawable.item_pic_default);
			else
				pic.setImageBitmap(m);
			
			((TextView) convertView.findViewById(R.id.list_commodity_name)).setText(msg.itemName);
			((TextView) convertView.findViewById(R.id.mine_commodity_price)).setText("￥"+String.valueOf(msg.price));
			TextView expire_time=((TextView)convertView.findViewById(R.id.mine_commodity_date));
			if (msg.is_online)
			{
				expire_time.setText("到期时间：" + msg.unshelveTime);
				expire_time.setTextColor(Color.rgb(0, 0x99, 0));
			}
			else
			{
				expire_time.setText("已下架");
				expire_time.setTextColor(Color.RED);
			}
			ButtonInsideListViewItem btn_offline = (ButtonInsideListViewItem) convertView.findViewById(R.id.btn_mine_commodity_offline);
			if (msg.is_online) 
			{
				btn_offline.setEnabled(true);
				Drawable drawable = context.getResources().getDrawable(R.drawable.item_mine_offline);
				// / 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
				btn_offline.setCompoundDrawables(drawable, null, null, null);
				btn_offline.setText("下架  ");
			} 
			else 
			{
				btn_offline.setEnabled(false);
				btn_offline.setCompoundDrawables(null, null, null, null);
				btn_offline.setText("已下架");
			}
			
			btn_offline.setOnClickListener(new ItemOperatorOnClickListener(C.URLunshelve_my_item, msg.itemId));
			
			ButtonInsideListViewItem btn_renew = (ButtonInsideListViewItem) convertView.findViewById(R.id.btn_mine_commodity_renew);
			btn_renew.setOnClickListener(new ItemOperatorOnClickListener(C.URLrenew_my_item, msg.itemId));
			
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
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
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
				Item msg = msgs.get(i);

				Bitmap bitmap = C.getBitmapFromMemCache(msg.image.get(0).picId);
				
				final ImageView imageView = (ImageView)dataListView.getChildAt(i-firstVisibleItem).findViewById(R.id.list_commodity_pic);

				if (bitmap == null) 
				{
					final String iid = msg.image.get(0).picId;

					new Request4Image(msg.image.get(0).picUrl) 
					{
						@Override
						protected void onPostExecute(Bitmap result) 
						{
							C.addBitmapToMemoryCache(iid, result);
							imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
							notifyDataSetChanged();
						}
					}.execute();
					
				} 
				else 
				{
					if (imageView != null && bitmap != null) 
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
	
	private class ItemOperatorOnClickListener implements OnClickListener {

		private String url;
		private String iid;

		public ItemOperatorOnClickListener(String url, String iid) {
			this.url = url;
			this.iid = iid;
		}

		@Override
		public void onClick(View v) {
			try {
				HashMap p = new HashMap();
//				p.put("uid", C.uid);
//				p.put("sid", C.sid);
				p.put("iid", iid);
				new PostTask(url, p) {
					@Override
					protected void onPostExecute(JSONObject result) {
						MineCommodityDetailActivity.mhandler.sendToast("操作成功");
						MineCommodityDetailActivity.mhandler.sendEmptyMessage(1);
//						MarketActivity.needRefresh = true;
					}
				}.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
