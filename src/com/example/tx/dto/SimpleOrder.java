package com.example.tx.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleOrder implements Serializable{
	private static final long serialVersionUID = -6839794958150949943L;
	public float totalPrice;
	public ArrayList<HashMap> items=new ArrayList();
	public String note;
	public String contact;
	public String address;
	public String userId;
	public String shopId;
	public String getNum(String itemId){
		for(int i=0;i<items.size();i++)
		{
			HashMap m=items.get(i);
			if(m.get("itemId").equals(itemId))
			{
				return String.valueOf(m.get("quantity"));
			}
		}
		return "0";
	}
	public void AddItem(String itemId,float price)
	{
		int flag=-1;
		for(int i=0;i<items.size();i++)
		{
			HashMap m=items.get(i);
			if(m.get("itemId").equals(itemId))
			{
				flag=i;
				break;
			}
		}
		if(flag==-1)
		{
			HashMap m=new HashMap();
			m.put("itemId", itemId);
			m.put("quantity",1);
			items.add(m);
		}
		else
		{
			HashMap m=items.get(flag);
			int count=Integer.valueOf((String)m.get("quantity").toString());
			m.remove("quantity");
			m.put("quantity", count+1);
		}
		totalPrice+=price;
	}
	public void MinusItem(String itemId,float price) throws Exception
	{
		int flag=-1;
		for(int i=0;i<items.size();i++)
		{
			HashMap m=items.get(i);
			if(m.get("itemId").equals(itemId))
			{
				flag=i;
				break;
			}
		}
		if(flag==-1)
			throw new Exception("购物车中未找到相关商品");
		else
		{
			HashMap m=items.get(flag);
			int count=Integer.valueOf(m.get("quantity").toString());
			m.remove("quantity");
			m.put("quantity", count-1);
			if(count==1)
				items.remove(flag);
			totalPrice-=price;
		}
	}
}
