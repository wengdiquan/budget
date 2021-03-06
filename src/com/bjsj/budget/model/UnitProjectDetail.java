package com.bjsj.budget.model;

public class UnitProjectDetail {
	private Integer id;

	private Integer unitprojectId; // 子目Id

	private String code; // 编码

	private String type; // 类别

	private String name; // 名称

	private String typeInfo; // 规格说明

	private String unit; // 单位

	private Double content; // 含量
	
	private Double amount; //数量
	
	private Double singleSumPrice; // 不含税合价
	private Double taxSingleSumPrice; // 含税合价

	private Double taxPrice; // 含税单价
	private Double noTaxPrice; // 不含税单价

	private Double origCount;

	private Integer lookValueId;
	
	private Integer seq;
	
	private Integer isSuppleCost; //是不是调整费
	
	private Integer isSupType; //是不是补充费用

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

	public Double getTaxSingleSumPrice() {
		return taxSingleSumPrice;
	}

	public void setTaxSingleSumPrice(Double taxSingleSumPrice) {
		this.taxSingleSumPrice = taxSingleSumPrice;
	}

	public Double getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(Double taxPrice) {
		this.taxPrice = taxPrice;
	}

	public Double getNoTaxPrice() {
		return noTaxPrice;
	}

	public void setNoTaxPrice(Double noTaxPrice) {
		this.noTaxPrice = noTaxPrice;
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

	@Override
	public String toString() {
		return "UnitProjectDetail [id=" + id + ", unitprojectId=" + unitprojectId + ", code=" + code + ", type=" + type
				+ ", name=" + name + ", typeInfo=" + typeInfo + ", unit=" + unit + ", content=" + content
				+ ", singleSumPrice=" + singleSumPrice + ", taxSingleSumPrice=" + taxSingleSumPrice + ", taxPrice="
				+ taxPrice + ", noTaxPrice=" + noTaxPrice + ", amount=" + amount + ", origCount=" + origCount + "]";
	}

	public Integer getLookValueId() {
		return lookValueId;
	}

	public void setLookValueId(Integer lookValueId) {
		this.lookValueId = lookValueId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public Integer getIsSuppleCost() {
		return isSuppleCost;
	}

	public void setIsSuppleCost(Integer isSuppleCost) {
		this.isSuppleCost = isSuppleCost;
	}

	public Integer getIsSupType() {
		return isSupType;
	}

	public void setIsSupType(Integer isSupType) {
	}
}