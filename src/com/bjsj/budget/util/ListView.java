package com.bjsj.budget.util;

import java.util.List;

/**
 * @author Yang Tian
 * @email 1298588579@qq.com
 */
public class ListView<E> {

	private Integer totalRecord;
	private List<E> data;

	public Integer getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(Integer totalRecord) {
		this.totalRecord = totalRecord;
	}

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> data) {
		this.data = data;
	}

}
