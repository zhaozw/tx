package com.example.tx.dto;

public class Category{
	
	public String id;
	public String name;
	public String imageurl;
	public String itemcount;
	
	public Category(String count,String id,String name,String imageurl){
		this.itemcount=count;
		this.id = id;
		this.name = name;
		this.imageurl = imageurl;
	}
}
