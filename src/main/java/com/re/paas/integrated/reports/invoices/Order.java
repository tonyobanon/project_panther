package com.re.paas.integrated.reports.invoices;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Order {

	private String orderId;
	private String customerId;
	
	private String companyName;
	private URL companyLogo;
	
	private String companyEmail;
	private String companyAddress;
	private String companyCity;
	private String companyState;
	private String companyZIP;
	private String companyPhone;
	private String companyFax;
	
	private String customerName;
	private String customerAddress;
	private String customerCity;
	private String customerState;
	private String customerZIP;
	private String customerPhone;
	
	private Number subtotal;
	private Number taxable;
	private Number taxRate;
	private Number taxDue;
	private Number other;
	private Number total;
	

	private List<String> comments;
	private List<OrderItem> products;
	
	
	private String dateCreated;


	public Order() {
		this.comments = new ArrayList<>();
		this.products = new ArrayList<>();
	}


	public String getOrderId() {
		return orderId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getCompanyAddress() {
		return companyAddress;
	}


	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}


	public String getCompanyCity() {
		return companyCity;
	}


	public void setCompanyCity(String companyCity) {
		this.companyCity = companyCity;
	}


	public String getCompanyZIP() {
		return companyZIP;
	}


	public void setCompanyZIP(String companyZIP) {
		this.companyZIP = companyZIP;
	}


	public String getCompanyPhone() {
		return companyPhone;
	}


	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}


	public String getCompanyFax() {
		return companyFax;
	}


	public void setCompanyFax(String companyFax) {
		this.companyFax = companyFax;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public String getCustomerAddress() {
		return customerAddress;
	}


	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}


	public String getCustomerCity() {
		return customerCity;
	}


	public void setCustomerCity(String customerCity) {
		this.customerCity = customerCity;
	}


	public String getCustomerZIP() {
		return customerZIP;
	}


	public void setCustomerZIP(String customerZIP) {
		this.customerZIP = customerZIP;
	}


	public String getCustomerPhone() {
		return customerPhone;
	}


	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}


	public Number getSubtotal() {
		return subtotal;
	}


	public void setSubtotal(Number subtotal) {
		this.subtotal = subtotal;
	}


	public Number getTaxable() {
		return taxable;
	}


	public void setTaxable(Number taxable) {
		this.taxable = taxable;
	}


	public Number getTaxRate() {
		return taxRate;
	}


	public void setTaxRate(Number taxRate) {
		this.taxRate = taxRate;
	}


	public Number getTaxDue() {
		return taxDue;
	}


	public void setTaxDue(Number taxDue) {
		this.taxDue = taxDue;
	}


	public Number getOther() {
		return other;
	}


	public void setOther(Number other) {
		this.other = other;
	}


	public Number getTotal() {
		return total;
	}


	public void setTotal(Number total) {
		this.total = total;
	}


	public List<String> getComments() {
		return comments;
	}


	public void addComment(String comment) {
		this.comments.add(comment);
	}


	public List<OrderItem> getProducts() {
		return products;
	}


	public void addProduct(OrderItem product) {
		this.products.add(product);
	}


	public String getDateCreated() {
		return dateCreated;
	}


	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}


	public URL getCompanyLogo() {
		return companyLogo;
	}


	public void setCompanyLogo(URL companyLogo) {
		this.companyLogo = companyLogo;
	}


	public String getCompanyState() {
		return companyState;
	}


	public void setCompanyState(String companyState) {
		this.companyState = companyState;
	}


	public String getCustomerState() {
		return customerState;
	}


	public void setCustomerState(String customerState) {
		this.customerState = customerState;
	}


	public String getCompanyEmail() {
		return companyEmail;
	}


	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}
	
	
}
