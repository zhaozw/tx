package com.example.tx.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.tx.R;
import com.example.tx.activity.MineCommodityActivity;
import com.example.tx.activity.MineCommodityDetailActivity;
import com.example.tx.dto.Item;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;
import com.example.tx.util.Request4Image;
import com.example.tx.view.ButtonInsideListViewItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;

@SuppressLint("WrongViewCast")
public class ItemAdapter extends BaseAdapter implements OnScrollListener{
	private Context context;
	private List<Item> items;
	private ListView dataListView;
	private int from;//表示从哪个页面传过来的值，0表示从我的商品，1表示从我收藏的宝贝
	private boolean edit;
	
	private int mFirstVisibleItem, mVisibleItemCount;
	public boolean isFirstEnter = true;
	
	public static boolean[] todel;
	
	//Item item;
	ImageView pic;
	
	public ItemAdapter(Context context,List<Item> items,ListView dataListView,int from,boolean edit)
	{
		super();
		this.context=context;
		this.items=items;
		this.dataListView=dataListView;
		this.from=from;
		this.edit=edit;
		init();
		dataListView.setOnScrollListener(this);
	}

	private void init()
	{
		todel=new boolean[items.size()];
		for(int i=0;i<items.size();i++)
			todel[i]=edit;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("WrongViewCast")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = items.get(position);
		class ViewHolder{
			int flag=-1;
			public TextView expire_time;
			public TextView btn_offline;
			public TextView btn_renew;
		};
		ViewHolder holder;
		if(convertView==null||((ViewHolder)convertView.getTag()).flag!=position)
		{
			holder=new ViewHolder();
			holder.flag=position;
			
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_mine_commodity, null);
			
			
			//设置单项的值
			((TextView)convertView.findViewById(R.id.list_commodity_name)).setText(item.itemName);
			((TextView)convertView.findViewById(R.id.mine_commodity_price)).setText("￥"+String.valueOf(item.price));
			
			//我的商品
			if(from==0)
			{
//				boolean isUnshelf=ifUnshelved(item.unshelveTime);
				holder.expire_time=((TextView)convertView.findViewById(R.id.mine_commodity_date));
				holder.btn_offline = (TextView) convertView.findViewById(R.id.btn_mine_commodity_offline);
				
				holder.btn_renew = (TextView) convertView.findViewById(R.id.btn_mine_commodity_renew);
				holder.btn_renew.setOnClickListener(new ItemOperatorOnClickListener(C.URLrenew_my_item, item.itemId));
				

				
				convertView.setTag(holder);
			}
			//我的宝贝
			else if(from==1)
			{
				((TextView)convertView.findViewById(R.id.mine_commodity_date)).setVisibility(View.GONE);
				((TextView) convertView.findViewById(R.id.btn_mine_commodity_offline)).setVisibility(View.GONE);
				((TextView)convertView.findViewById(R.id.mine_commodity_date)).setVisibility(View.GONE);
				((LinearLayout)convertView.findViewById(R.id.layout_myfavourite_location)).setVisibility(View.VISIBLE);
				((LinearLayout)convertView.findViewById(R.id.layout_myfavourite_name)).setVisibility(View.VISIBLE);
				((TextView)convertView.findViewById(R.id.myfavourite_seller)).setText(item.seller.userName);
				((TextView)convertView.findViewById(R.id.myfavourite_location)).setText(item.seller.location);
				((TextView) convertView.findViewById(R.id.btn_mine_commodity_renew)).setVisibility(View.GONE);
				
				convertView.setTag(holder);
			}
			else
				holder=(ViewHolder) convertView.getTag();
			
			boolean isUnshelf=ifUnshelved(item.unshelveTime);
			if(from==0)
			{
				if(isUnshelf)
				{
					holder.expire_time.setText("已下架");
					holder.expire_time.setTextColor(Color.RED);
					//下架按钮
					holder.btn_offline.setEnabled(false);
					holder.btn_offline.setCompoundDrawables(null, null, null, null);
					holder.btn_offline.setText("已下架");
				}
				else
				{
					holder.expire_time.setText("到期时间:"+item.unshelveTime.replace('T', ' '));
					holder.expire_time.setTextColor(Color.rgb(0, 0x99, 0));
					//下架按钮
					holder.btn_offline.setEnabled(true);
//					Drawable drawable = context.getResources().getDrawable(R.drawable.item_mine_offline);
//					// / 这一步必须要做,否则不会显示.
//					drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
//					btn_offline.setCompoundDrawables(drawable, null, null, null);
					holder.btn_offline.setText("下架  ");
					holder.btn_offline.setOnClickListener(new ItemOperatorOnClickListener(C.URLunshelve_my_item,item.itemId));
				}
			}
			
			//加载商品icon
//			pic=(ImageView) convertView.findViewById(R.id.list_commodity_pic);
//			Bitmap m=null;
//			if(item.image!=null)
//			{
//				m=C.getBitmapFromMemCache(item.image.get(0).picId);
//				if(m==null)
//				{
//					new Request4Image(item.image.get(0).picUrl)
//					{
//						@Override
//						protected void onPostExecute(Bitmap result) {
//							pic.setImageBitmap(result);
//							C.addBitmapToMemoryCache(item.image.get(0).picId, result);
//						}
//					}.execute();
//				}
//				else
//					pic.setImageBitmap(m);
//			}
			
			convertView.setTag(holder);
		}
		
		ImageButton btn_del=((ImageButton)convertView.findViewById(R.id.item_delete));
		if(from==0)
		{
			btn_del.setVisibility(todel[position]?View.VISIBLE:View.GONE);
			btn_del.setOnClickListener(new ItemOperatorOnClickListener(C.URLdelete_my_item,item.itemId));
		}
		else if(from==1)
		{
			btn_del.setVisibility(todel[position]?View.VISIBLE:View.GONE);
			btn_del.setOnClickListener(new ItemOperatorOnClickListener(C.URLremove_item_from_favorite,item.itemId));
		}
		
		return convertView;
	}
	
	public class ItemOperatorOnClickListener implements OnClickListener
	{
		String url;
		String itemId;
		public ItemOperatorOnClickListener(String url,String itemId)
		{
			this.url=url;
			this.itemId=itemId;
		}
		@Override
		public void onClick(View v) {
			try {
				//TODO 商品下架 商品续期传参
				HashMap p = new HashMap();
				p.put("itemId",itemId);
				p.put("userId", C.userId);
				new PostTask(url, p) {
					@Override
					protected void onPostExecute(JSONObject result) {
						if(from==0)
						{
							MineCommodityDetailActivity.mhandler.sendToast("操作成功");
							MineCommodityDetailActivity.mhandler.sendEmptyMessage(1);
							if(from==0&&url==C.URLdelete_my_item)
								MineCommodityActivity.mHandler.sendEmptyMessage(1);
						}
						else
						{
							MineCommodityDetailActivity.mhandler.sendToast("操作成功");
							MineCommodityDetailActivity.mhandler.sendEmptyMessage(3);
						}
//						MarketActivity.needRefresh = true;
					}
				}.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public boolean ifUnshelved(String unshelfDate)
	{
		String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()+5*60*1000);
		time=time.replace(' ', 'T');
		char[] now=time.toCharArray();
		char[] utime=unshelfDate.toCharArray();
		for(int i=0;i<now.length;i++)
		{
			if(now[i]>utime[i])
				return true;
			else if(now[i]<utime[i])
				return false;
		}
		return true;
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
				final Item msg = items.get(i);

				Bitmap bitmap=null;
				if(msg.image.size()>0)
				{
					bitmap = C.getBitmapFromMemCache(msg.image.get(0).picId);
					
					final ImageView imageView = (ImageView)dataListView.getChildAt(i-firstVisibleItem).findViewById(R.id.list_commodity_pic);
	
					if (bitmap == null) 
					{
	//					JSONObject p = new JSONObject();
	//					p.put("iid", Integer.valueOf(msg.itemId));
						
	//					for(int j = 0 ; j < msg.image.size() ; j ++)
	//					{
	//						final int jj = j;
						if(msg.image.get(0)!=null)
							new Request4Image(msg.image.get(0).picUrl) 
							{
								@Override
								protected void onPostExecute(Bitmap result) 
								{
									if(result==null) 
										return;
									C.addBitmapToMemoryCache(msg.image.get(0).picId, result);
									imageView.setImageBitmap(Bitmap.createScaledBitmap(result, 153, 153, true));
									notifyDataSetChanged();
								}
							}.execute();
	//					}
						
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
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
