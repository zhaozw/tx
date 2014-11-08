package com.example.tx.dto;

public class Comment {
	public String commentId;
	public String time;
	public String content;
	public String refId;
	public String makerId;
	public User maker;
	
	public Comment(String commentId,String time,String content,String makerId,User maker) {
		this.commentId = commentId;
		this.time = time;
		this.content = content;
		//this.refId = refId;
		this.makerId = makerId;
		this.maker = maker;
	}
}
