package com.bjsj.budget.model;

public class CategoryModelYCAModel {

	private Integer id;
	private Integer unitProject_Id;
	private String code;
	private String name;
	private String unit;
	private Double content;
	private Double amount;
	private Double noPrice;
	private Double price;  //含税单价
	private Double rate;
	private Double sumPrice;
	private Double sumNoPrice;
	private Integer lookValueId;//

	private Integer lookTypeId; // 根据这个类型判断是运输费，材料费，安装费
	private String lookTypeName; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Double getContent() {
		return content;
	}

	public void setContent(Double content) {
		this.content = content;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public Integer getUnitProject_Id() {
		return unitProject_Id;
	}

	public void setUnitProject_Id(Integer unitProject_Id) {
		this.unitProject_Id = unitProject_Id;
	}

	public Double getNoPrice() {
		return noPrice;
	}

	public void setNoPrice(Double noPrice) {
		this.noPrice = noPrice;
	}

	public Double getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(Double sumPrice) {
		this.sumPrice = sumPrice;
	}

	public Double getSumNoPrice() {
		return sumNoPrice;
	}

	public void setSumNoPrice(Double sumNoPrice) {
		this.sumNoPrice = sumNoPrice;
	}

	public Integer getLookValueId() {
		return lookValueId;
	}

	public void setLookValueId(Integer lookValueId) {
		this.lookValueId = lookValueId;
	}

	public Integer getLookTypeId() {
		return lookTypeId;
	}

	public void setLookTypeId(Integer lookTypeId) {
		this.lookTypeId = lookTypeId;
	}

	public String getLookTypeName() {
		return lookTypeName;
	}

	public void setLookTypeName(String lookTypeName) {
		this.lookTypeName = lookTypeName;
	}
}
