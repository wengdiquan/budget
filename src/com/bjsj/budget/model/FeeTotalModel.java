package com.bjsj.budget.model;

public class FeeTotalModel {
	private Integer id;
	private String seq;//序号
	private String feeCode;
	private String name;
	private String calculatedRadix;//计算基数
	private String radixRemark;//基数说明
	private Double rate;
	private Double amount;
	private String remark;
	private Integer templetId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getFeeCode() {
		return feeCode;
	}
	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCalculatedRadix() {
		return calculatedRadix;
	}
	public void setCalculatedRadix(String calculatedRadix) {
		this.calculatedRadix = calculatedRadix;
	}
	public String getRadixRemark() {
		return radixRemark;
	}
	public void setRadixRemark(String radixRemark) {
		this.radixRemark = radixRemark;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getTempletId() {
		return templetId;
	}
	public void setTempletId(Integer templetId) {
		this.templetId = templetId;
	}
	
}
