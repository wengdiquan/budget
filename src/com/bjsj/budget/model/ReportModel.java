package com.bjsj.budget.model;

public class ReportModel {

	private Integer index; //序号
	
	private String project_name;
	private String project_id;
	private Double transportFee;
	private Double materialFee;
	private Double installationFee;
	private Double csFee;
	private Double totalAmount;
	private Double projectPercent;
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
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
	public Double getCsFee() {
		return csFee;
	}
	public void setCsFee(Double csFee) {
		this.csFee = csFee;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Double getProjectPercent() {
		return projectPercent;
	}
	public void setProjectPercent(Double projectPercent) {
		this.projectPercent = projectPercent;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	
}
