package com.example.tx.dto;

import java.util.List;

public class Item {
	public String itemId;
	public String itemName;
	public float price;
	public String details;
	public String releaseTime;
	public String unshelveTime;
	
	public String categoryId;
	public Category category;//
	
	public String sellerId;//
	public User seller;
	public List<Image> image;
	
	public int quantity;
	
	public boolean is_online;
	
	
	public Item(String itemId,String itemName,float price,String details,String releaseTime,
			String unshelveTime,String categoryId,User seller, List<Image> image) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.price = price;
		this.details = details;
		this.releaseTime = releaseTime;
		this.unshelveTime = unshelveTime;
		this.categoryId = categoryId;

		this.seller = seller;
		this.image = image;
		
		//TODO 在这里判断是否过期
		//if()
		this.releaseTime = this.releaseTime.replace('T', ' ');
	}
	
	public Item(String itemId,String itemName,float price,String details,String releaseTime,
			String unshelveTime,String categoryId,User seller, List<Image> image,int quantity) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.price = price;
		this.details = details;
		this.releaseTime = releaseTime;
		this.unshelveTime = unshelveTime;
		this.categoryId = categoryId;

		this.seller = seller;
		this.image = image;
		
		this.quantity=quantity;
		//TODO 在这里判断是否过期
		//if()
		this.releaseTime = this.releaseTime.replace('T', ' ');
	}
}
