package com.bjsj.budget.model;

public class YCAModel {
	private Integer looktype_id;
	private Integer lookvalue_id;
	private String lookvalue_code;
	private String looktypeName;
	private Integer id;
	private String category;
	private String code;
	private String name;
	private String unit;

	private Double noPrice; //不含税价
	private Double price;   //含税价
	private Double rate;    //税率
	
	
	public String getLookvalue_code() {
		return lookvalue_code;
	}
	public void setLookvalue_code(String lookvalue_code) {
		this.lookvalue_code = lookvalue_code;
	}
	public Integer getLooktype_id() {
		return looktype_id;
	}
	public void setLooktype_id(Integer looktype_id) {
		this.looktype_id = looktype_id;
	}
	public String getLooktypeName() {
		return looktypeName;
	}
	public void setLooktypeName(String looktypeName) {
		this.looktypeName = looktypeName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Integer getLookvalue_id() {
		return lookvalue_id;
	}
	public void setLookvalue_id(Integer lookvalue_id) {
		this.lookvalue_id = lookvalue_id;
	}
	public Double getNoPrice() {
		return noPrice;
	}
	public void setNoPrice(Double noPrice) {
		this.noPrice = noPrice;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	@Override
	public String toString() {
		return "YCAModel [looktype_id=" + looktype_id + ", lookvalue_id=" + lookvalue_id + ", lookvalue_code="
				+ lookvalue_code + ", looktypeName=" + looktypeName + ", id=" + id + ", category=" + category
				+ ", code=" + code + ", name=" + name + ", unit=" + unit + ", noPrice=" + noPrice + ", price=" + price
				+ ", rate=" + rate + "]";
	}
}
