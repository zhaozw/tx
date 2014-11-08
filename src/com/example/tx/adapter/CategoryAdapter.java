package com.example.tx.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tx.R;
import com.example.tx.activity.MineCommodityActivity;
import com.example.tx.dto.Category;
import com.example.tx.util.C;
import com.example.tx.util.PostTask;

public class CategoryAdapter extends BaseAdapter 
{
	Context context;
	List<Category> cates;
	ListView lv;
	boolean edit;
	Category cate;
	
	public CategoryAdapter(Context context,List<Category> cates,ListView lv,boolean edit)
	{
		this.context=context;
		this.cates=cates;
		this.lv=lv;
		this.edit=edit;
	}
	@Override
	public int getCount() {
		return cates.size();
	}

	@Override
	public Object getItem(int position) {
		return cates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		class ViewHolder
		{
			int flag=-1;
			TextView txt;
			ImageButton ib;
		}
		ViewHolder vh;
		if(convertView==null||(vh=(ViewHolder)convertView.getTag()).flag!=position)
		{
			vh=new ViewHolder();
			vh.flag=position;
			
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_mine_category, null);
			
//			((TextView)convertView.findViewById(R.id.category_txt)).setText(cate.name+"("+cate.itemcount+")");
			vh.txt=(TextView)convertView.findViewById(R.id.category_txt);
			vh.ib=(ImageButton)convertView.findViewById(R.id.category_del);
			
			convertView.setTag(vh);
		}
		
		cate=cates.get(position);
		
		vh.txt.setText(cate.name+"("+cate.itemcount+")");
		vh.ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap map=new HashMap();
				map.put("userId",C.userId);
				map.put("categoryId", cate.id);
				new PostTask(C.URLget_items,map)
				{
					@Override
					protected void onPostExecute(JSONObject result) {
						try
						{
							if(result.getInt("status")==0)
							{
								JSONArray ja=result.getJSONArray("items");
								for(int i=0;i<ja.length();i++)
								{
									JSONObject item=ja.getJSONObject(i);
									String id=item.getString("itemId");
									HashMap req=new HashMap();
									req.put("userId", C.userId);
									req.put("itemId", id);
									new PostTask(C.URLdelete_my_item,req)
									{
										@Override
										protected void onPostExecute(
												JSONObject result) {
											try
											{
												if(result.getInt("status")==0);
												else
													MineCommodityActivity.mHandler.sendToast("删除错误："+result.getString("description"));
											}
											catch(Exception e)
											{
												MineCommodityActivity.mHandler.sendToast("后台错误");
												e.printStackTrace();
											}
												
										}
										
									}.execute();
								}
							}
							else
								MineCommodityActivity.mHandler.sendToast("删除失败："+result.getString("description"));
						}
						catch(Exception e)
						{
							MineCommodityActivity.mHandler.sendToast("后台错误");
							e.printStackTrace();
						}
					}
				}.execute();
			}
		});
		if(edit)
			vh.ib.setVisibility(View.VISIBLE);
		else
			vh.ib.setVisibility(View.GONE);
		
		return convertView;
	}
	
}
