package com.swust.model;

public class Item {
private String id;
private String name;
private String star;
private String cost;
private String tast;
private String environment;
private String service;
private String reviewCount;
public String toString(){
	StringBuffer bf = new StringBuffer();
	bf.append("id:").append(this.id)
	  .append("name:").append(this.name)
	  .append("start:").append(this.star)
	  .append("cost:").append(this.cost)
	  .append("tast:").append(this.tast)
	  .append("environment:").append(this.environment)
	  .append("service:").append(this.service)
	  .append("reviewCount:").append(this.reviewCount);
	return bf.toString();
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getStar() {
	return star;
}
public void setStar(String star) {
	this.star = star;
}
public String getCost() {
	return cost;
}
public void setCost(String cost) {
	this.cost = cost;
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
public String getReviewCount() {
	return reviewCount;
}
public void setReviewCount(String reviewCount) {
	this.reviewCount = reviewCount;
}

}
