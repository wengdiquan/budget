package com.bjsj.budget.model;

public class ReportModel {

	private String project_name;
	private String project_id;
	private Double transportFee;
	private Double materialFee;
	private Double installationFee;
	private Double csFee;
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
	
}
