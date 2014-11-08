package com.example.tx.dto;

public class TreeholeMessage {
	
	public int iid;
	public String name;
	public String content;
	public String time;
	public String imageId;
	
	public int refType;
	public String refId;
	
	public TreeholeMessage(int iid,String name,String content,String time,String imageId,int refType,String refId){
		this.iid = iid;
		this.name = name;
		this.content = content;
		this.time = time;
		this.imageId = imageId;
		this.refType = refType;
		this.refId = refId;
	}

}
