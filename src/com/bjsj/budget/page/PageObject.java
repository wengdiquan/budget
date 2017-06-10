package  com.bjsj.budget.page;

import java.util.List;

/**
 * 分页类
 *
 */
public class PageObject {

	/**
	 * Current page
	 */
	private int page;

	/**
	 * Total pages
	 */
	private int total;

	/**
	 * Total number of records
	 */
	private int records;

	/**
	 * Contains the actual data
	 */
	private List<?> rows;

	public PageObject() {

	}

	public PageObject(List<?> rows, PageInfo page) {
		this.rows = rows;
		this.total = page.getTotalPage();
		this.records = page.getTotalResult();
		this.page = page.getCurrentPage();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}
}
