package com.example.tx.dto;

public class Order {
	public String orderId;
	public String time;
	public String shopId;
	public String userId;
	public String address;
	public String contact;
	public int status;
	public String note;
	public Item[] items; 
	public float totalPrice;
	public Shop shop;
	public User user;
	public Order(String orderId,String time,String shopId,String userId,String address,String contact,int status
			,String note,Item[] items,float totalPrice,Shop shop,User user)
	{
		this.orderId=orderId;
		this.time=time;
		this.shopId=shopId;
		this.userId=userId;
		this.address=address;
		this.contact=contact;
		this.status=status;
		this.note=note;
		this.items=items;
		this.totalPrice=totalPrice;
		this.shop=shop;
		this.user=user;
	}
	public Order()
	{
		
	};
}
