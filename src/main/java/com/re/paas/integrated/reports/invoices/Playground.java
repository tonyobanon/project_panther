package com.re.paas.integrated.reports.invoices;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Locale;

import com.re.paas.api.classes.ClientResources;

public class Playground {

	public static void main(String[] args) {
		
		Order order = getSampleOrder();
		Path file = Paths.get("/documents/invoices/order-" + order.getOrderId() + ".pdf");
		new InvoiceBuilder(order).asPdf(file);
	}

	private static Order getSampleOrder() {

		// Create sample order
		
		Calendar now = Calendar.getInstance();
		
		Integer fromDay = now.get(Calendar.DAY_OF_MONTH);
		String fromMonth = now.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("en-US"));
		Integer fromYear = now.get(Calendar.YEAR);
		
		
		String from = fromMonth + ClientResources.HtmlCharacterEntities.SPACE + fromDay.toString()+ ClientResources.HtmlCharacterEntities.COMMA + ClientResources.HtmlCharacterEntities.SPACE + fromYear;
		
		Order sampleOrder = new Order();

		sampleOrder.setOrderId("4347893734");

		sampleOrder.setCompanyLogo(Playground.class.getClassLoader().getResource("com/re/paas/api/reporting/pdf/resources/logo2.png"));

		sampleOrder.setCompanyName("Retail XYZ Company");

		sampleOrder.setCompanyEmail("help@retail-xyz-company.co.uk");
		
		sampleOrder.setCompanyState("Washington D.C.");

		sampleOrder.setCompanyCity("City ABC");
		
		sampleOrder.setCompanyAddress("123, Wilson Avenue");

		sampleOrder.setCompanyFax("123489");

		sampleOrder.setCompanyPhone("+1-325-343-343");

		sampleOrder.setCompanyZIP("129673");

		sampleOrder.setCustomerId("34321");
		
		sampleOrder.setCustomerName("Vivian Fowler");

		sampleOrder.setCustomerState("Illinios");
		
		sampleOrder.setCustomerCity("City XYZ");

		sampleOrder.setCustomerAddress("123, Douglas Close");

		sampleOrder.setCustomerPhone("+1-325-343-343");

		sampleOrder.setCustomerZIP("129673");

		sampleOrder.addProduct(new OrderItem("Baby Diapers", "Medium Sized Diapers for toddlers", 457.34));

		sampleOrder.addProduct(new OrderItem("Black Gucci Shirt", "XXL-Sized Gucci T-Shirt with bow tie", 1054.23));

		sampleOrder.addProduct(new OrderItem(from, "Nice black shoes of size 45", 674.00));

		sampleOrder.setSubtotal(950.00);
		sampleOrder.setTaxable(345.00);
		sampleOrder.setTaxRate(6.250);
		sampleOrder.setTaxDue(21.56);
		sampleOrder.setOther(0);

		sampleOrder.setTotal(971.56);

		sampleOrder.addComment("Total payments due in 30 days");

		sampleOrder.addComment("Please include the invoice number on your check");

		sampleOrder.setDateCreated("12/06/2017");

		return sampleOrder;
	}

}
