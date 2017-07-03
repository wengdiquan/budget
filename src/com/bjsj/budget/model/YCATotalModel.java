package com.bjsj.budget.model;

public class YCATotalModel {

	private Integer id;
	private String code;
	private String type;
	private String name;
	private String unit;
	private Double tax_Price;
	private Double notax_Price;
	private Double amount;
	private Double tax_Single_SumPrice;
	private Double single_SumPrice;
	private Integer bitProjectId;
	
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getTax_Price() {
		return tax_Price;
	}
	public void setTax_Price(Double tax_Price) {
		this.tax_Price = tax_Price;
	}
	public Double getNotax_Price() {
		return notax_Price;
	}
	public void setNotax_Price(Double notax_Price) {
		this.notax_Price = notax_Price;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTax_Single_SumPrice() {
		return tax_Single_SumPrice;
	}
	public void setTax_Single_SumPrice(Double tax_Single_SumPrice) {
		this.tax_Single_SumPrice = tax_Single_SumPrice;
	}
	public Double getSingle_SumPrice() {
		return single_SumPrice;
	}
	public void setSingle_SumPrice(Double single_SumPrice) {
		this.single_SumPrice = single_SumPrice;
	}
	public Integer getBitProjectId() {
		return bitProjectId;
	}
	public void setBitProjectId(Integer bitProjectId) {
		this.bitProjectId = bitProjectId;
	}

}
