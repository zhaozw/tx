package com.example.tx.dto;

import java.util.List;

public class UserDetails {
    
    String detailId;
    String campus;
    String school;
    String major;
    int grade;
    
    String userId;
    User user;

    List<Item> items;
    public UserDetails(String detailId,String campus,String school,String major,int grade,String userId,User user,List<Item> items)
    {
    	this.detailId=detailId;
    	this.campus=campus;
    	this.school=school;
    	this.major=major;
    	this.grade=grade;
    	this.userId=userId;
    	this.user=user;
    	this.items=items;
    }
	public String getDetailId() {
		return detailId;
	}
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String campus) {
		this.campus = campus;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
    
}
