package com.example.tx.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.tx.R;
import com.example.tx.activity.MineCommodityActivity.GetReviewing;
import com.example.tx.activity.StoreDetailActivity;
import com.example.tx.adapter.CommentListAdapter.ViewHolder;
import com.example.tx.dto.Image;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.SimpleStoreCategory;
import com.example.tx.dto.SimpleStoreGoods;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;
import com.example.tx.view.MyExpandableListView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StoreCategoryGoodsAdapter extends BaseExpandableListAdapter {
	
	public ArrayList<SimpleStoreCategory> groupData;
	public ArrayList<ArrayList<ShopItem>> childData;
	public Context context;
	public TextView total;
	public float totalPrice=(float) 0.0;
	public MyExpandableListView elv_store_category;
	
	public boolean[] isOpen;
	
	public StoreCategoryGoodsAdapter(Context context , ArrayList<SimpleStoreCategory> groupData ,
			ArrayList<ArrayList<ShopItem>> childData,TextView total,MyExpandableListView elv_store_category){
		this.context = context;
		this.groupData = groupData;
		this.childData = childData;
		this.total=total;
		this.elv_store_category = elv_store_category;
		
		tv_totals=new TextView[childData.size()];
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return childData.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		long id = 0;
		for(int i = 0 ; i < arg0 ; i ++){
			id += childData.get(i).size();
		}
		id += arg1;
		return id;
//		return arg1;
	}

	class ParentViewHolder
	{
		int flag=-1;
		TextView tv_total;
		ImageView iv_group;
		public TextView tv_categoryname;
	}
	
	class ViewHolder{
		public LinearLayout ll_storeitem;
		public TextView tv;
		public TextView tv_storegoodsdes;
		public TextView tv_storegoodsname;
		public ImageView iv_logo;
		public ImageButton ib_plus;
		public ImageButton ib_minus;
		public TextView et_num;
		int flag = -1;
	}
	
	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View convertView,
			ViewGroup parent) {
//		||((ViewHolder)convertView.getTag()).flag!=(int)getChildId(arg0,arg1)
		ViewHolder holder;
		final ShopItem si=childData.get(arg0).get(arg1);//取数据
		if(convertView == null || ((ViewHolder)convertView.getTag()).flag != (int) getChildId(arg0 , arg1) ){
			holder = new ViewHolder();
			holder.flag = (int) getChildId(arg0 , arg1);
			Log.d("ex","=========="+arg0+","+arg1+"="+holder.flag);

//			((ParentViewHolder)parent.getTag()).
			//makeView
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_goodsinstore, null);
			
			holder.ll_storeitem = (LinearLayout) convertView.findViewById(R.id.ll_storeitem);//用于跳转到详情页
			holder.tv_storegoodsname = (TextView) convertView.findViewById(R.id.tv_storegoodsname);//商品名称
			holder.tv_storegoodsdes = (TextView) convertView.findViewById(R.id.tv_storegoodsdes);//商品
			holder.ib_plus = (ImageButton) convertView.findViewById(R.id.ib_plus);//加
			holder.ib_minus = (ImageButton) convertView.findViewById(R.id.ib_minus);//减
			holder.et_num = (TextView) convertView.findViewById(R.id.et_num);//数量
			holder.iv_logo=(ImageView)convertView.findViewById(R.id.iv_goods_left);//logo
			
			final ImageView t_iv_logo=holder.iv_logo;
			
			//inflate Logo
			if(si.images.length>0&&si.images[0]!=null)
			{
				final Image logo=si.images[0];
				Bitmap bm=C.getBitmapFromMemCache(logo.picId);
				if(bm!=null)
				{
					holder.iv_logo.setImageBitmap(bm);
				}
				else
				{
					new Request4Image(logo.picUrl)
					{
						@Override
						protected void onPostExecute(Bitmap result) {
							if(result!=null)
							{
								t_iv_logo.setImageBitmap(result);
								C.addBitmapToMemoryCache(logo.picId, result);
							}
						}
					}.execute();
				}
			}
			
			holder.ib_plus.setTag(holder);//让"+"按钮获得相关子视图的对象
			holder.ib_minus.setTag(holder);
			convertView.setTag(holder);
			
		}
		else
			holder=(ViewHolder)convertView.getTag();
		
		holder.ll_storeitem.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Message siMessage = StoreDetailActivity.that.mHandler.obtainMessage();
				siMessage.obj = si;
				siMessage.what = 98;
				StoreDetailActivity.that.mHandler.sendMessage(siMessage);
			}
			
		});
		//Log.d("ex",arg0+","+arg1+"="+holder.flag);
		
		holder.tv=tv_totals[arg0];//大类下的商品总个数
		
		holder.tv_storegoodsname.setText(si.itemName);
		holder.tv_storegoodsdes.setText("￥"+si.price+"元");
		int n_num = Integer.parseInt(StoreDetailActivity.that.so.getNum(si.itemId));
		holder.et_num.setText(StoreDetailActivity.that.so.getNum(si.itemId));//yu
		if(n_num > 0){
			holder.ib_minus.setVisibility(View.VISIBLE);
			holder.et_num.setVisibility(View.VISIBLE);
		}
		holder.ib_plus.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				ViewHolder vh=(ViewHolder)v.getTag();
				String n = vh.et_num.getText().toString();
				int nums = Integer.parseInt(n);
				nums ++;
				vh.et_num.setText(String.valueOf(nums));
				if(nums > 0){
					vh.et_num.setVisibility(View.VISIBLE);
					vh.ib_minus.setVisibility(View.VISIBLE);
				}
				calcTotal('+',si.price);
				String temp=vh.tv.getText().toString();
				int count=Integer.valueOf(temp.trim());
				count++;
				vh.tv.setText(String.valueOf(count));
				vh.tv.setVisibility(View.VISIBLE);
				StoreDetailActivity.that.so.AddItem(si.itemId, si.price);
			}
			
		});
		
		holder.ib_minus.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ViewHolder vh=(ViewHolder)v.getTag();
				String n = vh.et_num.getText().toString();
				int nums = Integer.parseInt(n);
				if(nums<=0)
					return;
				nums --;
				if(nums>=0)
				{
					calcTotal('-',si.price);
					String temp=vh.tv.getText().toString();
					int count=Integer.valueOf(temp.trim());
					count--;
					if(count<=0)
					{
						count=0;
						vh.tv.setVisibility(View.INVISIBLE);
					}
					vh.tv.setText(String.valueOf(count));
					try {
						((StoreDetailActivity)context).so.MinusItem(si.itemId, si.price);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(nums <= 0){
					nums=0;
					vh.et_num.setVisibility(View.INVISIBLE);
					vh.ib_minus.setVisibility(View.INVISIBLE);
				}
				vh.et_num.setText(String.valueOf(nums));
				try {
					StoreDetailActivity.that.so.MinusItem(si.itemId, si.price);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		return convertView;
	}
	
	private void calcTotal(char f,float p)
	{
		if(f=='+')
			totalPrice+=p;
		if(f=='-')
			totalPrice-=p;
		total.setText("总共"+totalPrice+"元");
		Message msg=new Message();
		msg.what=1;
		msg.obj=totalPrice;
		StoreDetailActivity.that.mHandler.sendMessage(msg);
	}
	
	@Override
	public int getChildrenCount(int arg0) {
		return childData.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		return groupData.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return groupData.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	public TextView[] tv_totals;
	
	@Override
	public View getGroupView(int arg0, boolean arg1, View convertView, ViewGroup arg3) {
		ParentViewHolder holder;
		if(convertView == null){
			holder = new ParentViewHolder();
			holder.flag = arg0;
			
			//makeView
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_store_category, null);
			
			holder.tv_total=(TextView)convertView.findViewById(R.id.tv_group_count_total);
			
			holder.tv_categoryname = (TextView) convertView.findViewById(R.id.tv_categoryname);
			
			holder.iv_group = (ImageView) convertView.findViewById(R.id.iv_group_right);
			
			isOpen[arg0]=false;
			
			convertView.setTag(holder);
			Log.d("open null",arg0+"="+isOpen[arg0]);
		}
		else{
			holder=(ParentViewHolder)convertView.getTag();
			//参数arg1表示当前状态
			isOpen[arg0] = arg1;
			Log.d("open have",arg0+"="+isOpen[arg0]);
			if(isOpen[arg0])
			{
				holder.iv_group.setImageDrawable(context.getResources().getDrawable(R.drawable.down_icon_50));
			}
			else
			{
				holder.iv_group.setImageDrawable(context.getResources().getDrawable(R.drawable.right_icon_50));
			}
		}
		
		holder.tv_categoryname.setText(groupData.get(arg0).name);
		
		tv_totals[arg0]=holder.tv_total;
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}
}
