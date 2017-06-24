package com.bjsj.budget.model;

public class UnitProjectDetail {
	private Integer id;

	private Integer unitprojectId; //单位工程ID

	private String code;       //编码

	private String type;	   //列别

	private String name;		//名称

	private String typeInfo;	//规格说明

	private String unit;		//单位

	private Double content;		//含量

	private Double singleSumPrice;  //

	private Double marketPrice;  //市场价

	private Double amount;

	private Double origCount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUnitprojectId() {
		return unitprojectId;
	}

	public void setUnitprojectId(Integer unitprojectId) {
		this.unitprojectId = unitprojectId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeInfo() {
		return typeInfo;
	}

	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
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

	public Double getSingleSumPrice() {
		return singleSumPrice;
	}

	public void setSingleSumPrice(Double singleSumPrice) {
		this.singleSumPrice = singleSumPrice;
	}

	public Double getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getOrigCount() {
		return origCount;
	}

	public void setOrigCount(Double origCount) {
		this.origCount = origCount;
	}
}