package com.bjsj.budget.model;

public class CategoryModelModel {
	private Integer id;
	private String code;
	private String name;
	private String unit;
	private Double price;
	private Double transportFee;
	private Double materialFee;
	private Double installationFee;
	private Double sumPrice;
	private Double dtgcl;
	private Integer parentId;
	private Integer leaf;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getTransportFee() {
		return transportFee;
	}
	public void setTransportFee(Double transportFee) {
		this.transportFee = transportFee;
	}
	public Double getMaterialFee() {
		return materialFee;
	}
	public void setMaterialFee(Double materialFee) {
		this.materialFee = materialFee;
	}
	public Double getInstallationFee() {
		return installationFee;
	}
	public void setInstallationFee(Double installationFee) {
		this.installationFee = installationFee;
	}
	public Double getDtgcl() {
		return dtgcl;
	}
	public void setDtgcl(Double dtgcl) {
		this.dtgcl = dtgcl;
	}
	public Integer getLeaf() {
		return leaf;
	}
	public void setLeaf(Integer leaf) {
		this.leaf = leaf;
	}
	public Double getSumPrice() {
		return sumPrice;
	}
	public void setSumPrice(Double sumPrice) {
		this.sumPrice = sumPrice;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

}
