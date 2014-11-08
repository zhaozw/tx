package com.example.tx.dto;

public class TalkComment {
	public String id;
	public String time;
	public String content;
	//public String refId;
	public String senderId;
	public String talkId;
	
	public User sender;
	
	public TalkComment( String id,String time,String content,
			String senderId,String talkId,User sender){
		this.id = id;
		this.time = time;
		this.content = content;
		//this.refId = refId;
		this.senderId = senderId;
		this.talkId = talkId;
		this.sender = sender;
	}
}
