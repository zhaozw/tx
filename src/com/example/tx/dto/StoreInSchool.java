package com.example.tx.dto;

public class StoreInSchool {
	public String name;
	public String shopId;
	public String description;
	public String avatar;
	public String price;
	//added by fjb
	public boolean isOpen;
	
	public StoreInSchool(String shopId,String name, String description , String avatar){
		this.name = name;
		this.description = description;
		this.avatar = avatar;
		this.shopId=shopId;
	}
	public void setPrice(String price){
		this.price = price;
	}
	//设置是否营业标志位
	public void setIsOpen(boolean isOpen){
		this.isOpen = isOpen;
	}
}
