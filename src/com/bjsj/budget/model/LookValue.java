package com.bjsj.budget.model;

public class LookValue {
	private Integer lookvalueId;

	private String lookvalueCode;

	private String lookvalueName;

	private String remark;

	private Double seq;

	private Integer enableFlag;
	
	private Integer lookTypeId;

	public Integer getLookvalueId() {
		return lookvalueId;
	}

	public void setLookvalueId(Integer lookvalueId) {
		this.lookvalueId = lookvalueId;
	}

	public String getLookvalueCode() {
		return lookvalueCode;
	}

	public void setLookvalueCode(String lookvalueCode) {
		this.lookvalueCode = lookvalueCode;
	}

	public String getLookvalueName() {
		return lookvalueName;
	}

	public void setLookvalueName(String lookvalueName) {
		this.lookvalueName = lookvalueName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getSeq() {
		return seq;
	}

	public void setSeq(Double seq) {
		this.seq = seq;
	}

	public Integer getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Integer enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Integer getLookTypeId() {
		return lookTypeId;
	}

	public void setLookTypeId(Integer lookTypeId) {
		this.lookTypeId = lookTypeId;
	}
}