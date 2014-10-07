package com.swust.model;

import java.util.Date;

public class UserItem {
private String userId;
private String ItemId;
private String rating;
private String tast;
private String environment;
private String service;
private String times;
private String review;
public String toString(){
	StringBuffer bf = new StringBuffer();
	bf.append("userId:").append(this.userId)
		.append("Itemid:").append(this.ItemId)
		.append("rating:").append(this.rating)
		.append("tast:").append(this.tast)
		.append("environment:").append(this.environment)
		.append("service:").append(this.service)
		.append("times:").append(this.times)
		.append("review:").append(this.review);
	return bf.toString();
}
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getItemId() {
	return ItemId;
}
public void setItemId(String itemId) {
	ItemId = itemId;
}
public String getRating() {
	return rating;
}
public void setRating(String rating) {
	this.rating = rating;
}
public String getTast() {
	return tast;
}
public void setTast(String tast) {
	this.tast = tast;
}
public String getEnvironment() {
	return environment;
}
public void setEnvironment(String environment) {
	this.environment = environment;
}
public String getService() {
	return service;
}
public void setService(String service) {
	this.service = service;
}
public String getTimes() {
	return times;
}
public void setTimes(String times) {
	this.times = times;
}
public String getReview() {
	return review;
}
public void setReview(String review) {
	this.review = review;
}

}
