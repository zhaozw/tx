package com.example.tx.dto;

public class Shop {
	public String shopId;
	public String category;
	public String name;
	public String address;
	public String introduction;
	public String registerTime;
	public String logo;
	public String background;
	public String ownerId;
	public String bulletin;
	public int speed;
	public String timeStart;
	public String timeEnd;
	public int type;
	public String contact;
	public float price;
	public String target;
	public int orderType;
	public String other;
	public User owner;
	public String[] classes;
	public ShopItem[] items;
	public Shop()
	{
		
	}
	public Shop(String shopId,String category,String name,String address,String introduction,String registerTime,String logo,
			String background,String ownerId,String bulletin,int speed,String timeStart,String timeEnd,int type,String contact,
			float price,String target,int orderType,String other,User owner,String[] classes,ShopItem[] items)
	{
		this.shopId=shopId;
		this.category=category;
		this.name=name;
		this.address=address;
		this.introduction=introduction;
		this.registerTime=registerTime;
		this.logo=logo;
		this.background=background;
		this.ownerId=ownerId;
		this.bulletin=bulletin;
		this.speed=speed;
		this.timeStart=timeStart;
		this.timeEnd=timeEnd;
		this.type=type;
		this.contact=contact;
		this.price=price;
		this.target=target;
		this.orderType=orderType;
		this.other=other;
		this.owner=owner;
		this.classes=classes;
		this.items=items;
	}
}
