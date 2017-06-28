package com.bjsj.budget.model;

public class UnitProject {

	private Integer id; // 主键

	private String code; // 编码

	private String type; // 类型

	private String name; // 名称

	private String unit; // 单位

	private Double content; // 含量

	private Double dtgcl; // 工程量

	private Double singlePrice; // 不含税单价

	private Double taxSinglePrice; // 含税单价

	private Double singleSumPrice; // 不含税合价

	private Double taxSingleSumPrice; // 含税合价

	private Double price; // 综合单价

	private Double sumPrice; // 综合合价

	private String remark; // 备注

	private Integer parentid; // 用于树形结构

	private Integer leaf; // 用于树形结构

	private Integer bitProjectId; // 单位工程id

	private Integer lookValueId; // 费用类型ID,如果 是 定额的话，为空

	private Integer seq; // 排序

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

	public Double getContent() {
		return content;
	}

	public void setContent(Double content) {
		this.content = content;
	}

	public Double getDtgcl() {
		return dtgcl;
	}

	public void setDtgcl(Double dtgcl) {
		this.dtgcl = dtgcl;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getLeaf() {
		return leaf;
	}

	public void setLeaf(Integer leaf) {
		this.leaf = leaf;
	}

	public Integer getBitProjectId() {
		return bitProjectId;
	}

	public void setBitProjectId(Integer bitProjectId) {
		this.bitProjectId = bitProjectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getSinglePrice() {
		return singlePrice;
	}

	public void setSinglePrice(Double singlePrice) {
		this.singlePrice = singlePrice;
	}

	public Double getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(Double sumPrice) {
		this.sumPrice = sumPrice;
	}

	public Integer getLookValueId() {
		return lookValueId;
	}

	public void setLookValueId(Integer lookValueId) {
		this.lookValueId = lookValueId;
	}

	@Override
	public String toString() {
		return "UnitProject [id=" + id + ", code=" + code + ", type=" + type + ", name=" + name + ", unit=" + unit
				+ ", content=" + content + ", dtgcl=" + dtgcl + ", singlePrice=" + singlePrice + ", price=" + price
				+ ", sumPrice=" + sumPrice + ", remark=" + remark + ", parentid=" + parentid + ", leaf=" + leaf
				+ ", bitProjectId=" + bitProjectId + ", lookValueId=" + lookValueId + "]";
	}

	public Double getSingleSumPrice() {
		return singleSumPrice;
	}

	public void setSingleSumPrice(Double singleSumPrice) {
		this.singleSumPrice = singleSumPrice;
	}

	public Double getTaxSinglePrice() {
		return taxSinglePrice;
	}

	public void setTaxSinglePrice(Double taxSinglePrice) {
		this.taxSinglePrice = taxSinglePrice;
	}

	public Double getTaxSingleSumPrice() {
		return taxSingleSumPrice;
	}

	public void setTaxSingleSumPrice(Double taxSingleSumPrice) {
		this.taxSingleSumPrice = taxSingleSumPrice;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
}