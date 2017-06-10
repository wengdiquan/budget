package com.bjsj.budget.response.model;

import java.io.Serializable;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;

public class BaseResponse extends ExtDirectResponse implements Serializable {
	private String msg;
	private Boolean success;
	private Object result;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
