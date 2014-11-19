package com.example.tx.dto;

public class StoreInSchool {
	public String name;
	public String shopId;
	public String description;
	public String avatar;
	public String price;
	
	public StoreInSchool(String shopId,String name, String description , String avatar){
		this.name = name;
		this.description = description;
		this.avatar = avatar;
		this.shopId=shopId;
	}
	public void setPrice(String price){
		this.price = price;
	}
}
