package com.bjsj.budget.model;

public class UnitProject {

	private Long id;

	private String code;

	private String type;

	private String name;

	private String unit;

	private Double amount;

	private Double dtgcl; // 工程量

	private Double singlePrice; // 合价

	private Double price; // 综合单价

	private Double sumPrice; // 综合合价

	private String remark;

	private Integer parentid;

	private Integer leaf;

	private Integer bitProjectId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDtgcl() {
		return dtgcl;
	}

	public void setDtgcl(Double dtgcl) {
		this.dtgcl = dtgcl;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getLeaf() {
		return leaf;
	}

	public void setLeaf(Integer leaf) {
		this.leaf = leaf;
	}

	public Integer getBitProjectId() {
		return bitProjectId;
	}

	public void setBitProjectId(Integer bitProjectId) {
		this.bitProjectId = bitProjectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getSinglePrice() {
		return singlePrice;
	}

	public void setSinglePrice(Double singlePrice) {
		this.singlePrice = singlePrice;
	}

	public Double getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(Double sumPrice) {
		this.sumPrice = sumPrice;
	}

}