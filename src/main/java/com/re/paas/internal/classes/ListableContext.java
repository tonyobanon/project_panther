package com.re.paas.internal.classes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.ListableIndex;

public class ListableContext implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private ListableIndex type;
	
	private Integer pageSize;
	
	private Integer currentPage;
	private Integer pageCount;
	
	Map<Integer, List<String>> pages = new FluentHashMap<>();

	public String getId() {
		return id;
	}

	public ListableContext setId(String id) {
		this.id = id;
		return this;
	}

	public ListableIndex getType() {
		return type;
	}

	public ListableContext setType(ListableIndex type) {
		this.type = type;
		return this;
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public ListableContext setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		return this;
	}

	public Map<Integer, List<String>> getPages() {
		return pages;
	}
	
	public List<String> getPage(Integer page){
		return pages.get(page);
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public ListableContext setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public ListableContext setPages(Map<Integer, List<String>> pages) {
		this.pages = pages;
		return this;
	}
	
	public ListableContext addPage(Integer page, List<String> keys) {
		this.pages.put(page, keys);
		return this;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public ListableContext setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
		return this;
	}
	
}
