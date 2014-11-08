package com.example.tx.dto;

public class User {
	public String userId;
	public String account;
	public String userName;
	public String college;
	public String location;
	public String avatar;

	public User(String userId,String account,String userName,String college,String location,String avatar)
	{
		this.userId=userId;
		this.account=account;
		this.userName=userName;
		this.college=college;
		this.location=location;
		this.avatar=avatar;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
