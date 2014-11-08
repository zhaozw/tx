package com.example.tx.dto;

import java.util.List;

public class Talk {
	public String id;
	public String time;
	public String content;
	public String senderId;
	
	public User sender;
	
	
	public Talk(String id,String time,String content,String senderId,User sender){
		this.id = id;
		this.time = time;
		this.content = content;
		this.senderId = senderId;
		this.sender = sender;
		
		this.time = this.time.replace('T', ' ');
	}

}
