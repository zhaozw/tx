package com.example.tx.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import com.example.tx.R;
import com.example.tx.dto.ShopItem;
import com.example.tx.dto.SimpleOrder;
import com.example.tx.util.C;
import com.example.tx.util.Request4Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShoppingCartAdapter extends BaseAdapter{
	
	HashMap m;
	ViewHolder vh;
	Bitmap bm;
	
	private Context context;
	private List<ShopItem> lsi;
	SimpleOrder so;
	
	public ShoppingCartAdapter(Context context,List<ShopItem> lsi,SimpleOrder so)
	{
		this.context=context;
		this.lsi=lsi;
		this.so=so;
	}

	@Override
	public int getCount() {
		return lsi.size();
	}

	@Override
	public Object getItem(int position) {
		return lsi.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ShopItem si=(ShopItem) getItem(position);
		for(HashMap hm:so.items)
			if(hm.get("itemId").equals(si.itemId))
			{
				m=(HashMap) hm.clone();
				break;
			}
		if(convertView==null)
		{
			vh=new ViewHolder();
			
			convertView=LayoutInflater.from(context).inflate(R.layout.list_item_shopping_cart, null);
			
			vh.flag=position;
			vh.ib_minus=(ImageButton)convertView.findViewById(R.id.ib_minus);
			vh.ib_plus=(ImageButton)convertView.findViewById(R.id.ib_plus);
			vh.iv_logo=(ImageView)convertView.findViewById(R.id.iv_logo);
			vh.tv_count=(TextView)convertView.findViewById(R.id.tv_count);
			vh.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
			vh.tv_price=(TextView)convertView.findViewById(R.id.tv_price);
			vh.si=si;
			
			vh.ib_minus.setOnClickListener(new View.OnClickListener() {
				@Override
				public synchronized void onClick(View v) {
					ViewHolder vht=(ViewHolder)v.getTag();
					int quantity=(Integer)vht.map.get("quantity");
					if(quantity<=0)
						return;
					quantity--;
					vht.map.remove("quantity");
					vht.map.put("quantity",quantity);
					vht.tv_count.setText(String.valueOf(quantity));
					try {
						so.MinusItem(vht.si.itemId, vht.si.price);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(quantity<=0)
					{
						vht.ib_minus.setVisibility(View.INVISIBLE);
					}
				}
			});
			vh.ib_plus.setOnClickListener(new View.OnClickListener() {
				@Override
				public synchronized void onClick(View v) {
					ViewHolder vht=((ViewHolder)v.getTag());
					int quantity=(Integer)vht.map.get("quantity");
					quantity++;
					vht.map.remove("quantity");
					vht.map.put("quantity",quantity);
					vht.tv_count.setText(String.valueOf(quantity));
					so.AddItem(vht.si.itemId, vht.si.price);
					vht.ib_minus.setVisibility(View.VISIBLE);
				}
			});
			if(si.images.length>0)
			{
				if((bm=(C.getBitmapFromMemCache(si.images[0].picId)))!=null)
				{
					vh.iv_logo.setImageBitmap(bm);
				}
				else
					new Request4Image(si.images[0].picUrl)
					{
						@Override
						protected void onPostExecute(Bitmap result) {
							bm=result;
							C.addBitmapToMemoryCache(si.images[0].picId, bm);
						}
					}.execute();
			}
			vh.map=m;
			convertView.setTag(vh);
			vh.ib_minus.setTag(vh);
			vh.ib_plus.setTag(vh);
		}
		else
			vh=(ViewHolder)convertView.getTag();
		vh.tv_count.setText(String.valueOf((Integer)m.get("quantity")));
		vh.tv_name.setText(si.itemName);
		vh.tv_price.setText("￥"+String.valueOf(si.price));
		
		return convertView;
	}
	
	public class ViewHolder
	{
		int flag=-1;
		ImageView iv_logo;
		TextView tv_name,tv_price,tv_count;
		ImageButton ib_plus,ib_minus;
		HashMap map;
		ShopItem si;
	}

}
