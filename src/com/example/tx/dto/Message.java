package com.example.tx.dto;

public class Message {
	public String id;
	public String content;
	public String time;
	
	public String senderId;
	public User sender;
	
	public int refType;//0为商品 1为树洞
	public String refId;
	public int isRead;//0表示未读 1表示已读
	
	public Message(String id,String content,String time,String senderId,User sender,
			int refType,String refId,int isRead){
		this.id = id;
		this.content = content;
		this.time = time;
		this.senderId = senderId;
		this.sender = sender;
		this.refType = refType;
		this.refId = refId;
		this.isRead = isRead;
	}
}
