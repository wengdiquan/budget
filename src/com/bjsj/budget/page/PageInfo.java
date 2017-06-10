package com.bjsj.budget.page;

import java.io.Serializable;

/**
 * 分页信息
 * 
 */
public class PageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3928042714007403363L;
	private int showCount; //数据显示条数
	private int totalPage; //总页数
	private int totalResult; //数据总条数
	private int currentPage; //当前页面
	private int currentResult; //当前显示数据

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getCurrentResult() {
		return currentResult;
	}

	public void setCurrentResult(int currentResult) {
		this.currentResult = currentResult;
	}
}
