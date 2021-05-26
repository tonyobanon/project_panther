package com.re.paas.internal.documents.pdf;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.re.paas.api.forms.Column;
import com.re.paas.api.forms.SizeSpec;
import com.re.paas.internal.documents.pdf.gen.Constants;
import com.re.paas.internal.documents.pdf.gen.Font;
import com.re.paas.internal.documents.pdf.gen.Image;
import com.re.paas.internal.documents.pdf.gen.Row;
import com.re.paas.internal.documents.pdf.gen.Table;
import com.re.paas.internal.documents.pdf.gen.TableConfig;
import com.re.paas.internal.documents.pdf.gen.TextControl;
import com.re.paas.internal.documents.pdf.gen.XCoordinate;

public class InvoiceBuilder {

	private final Order order;
	private final SizeSpec bodySize, headerSize;
	
	private static Order getSampleOrder() {

		// Create sample order
		
		Calendar now = Calendar.getInstance();
		
		Integer fromDay = now.get(Calendar.DAY_OF_MONTH);
		String fromMonth = now.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("en-US"));
		Integer fromYear = now.get(Calendar.YEAR);
		
		
		String from = fromMonth + " " + fromDay.toString( )+ ", " + fromYear;
		
		Order sampleOrder = new Order();

		sampleOrder.setOrderId("4347893734");

		try {
			sampleOrder.setCompanyLogo(new URL("https://static.remove.bg/sample-gallery/products/blur-brass-bronze-2113994-thumbnail.jpg"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

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
	
	public InvoiceBuilder(Order order) {
		this(SizeSpec.body(), SizeSpec.header(), order);
	}
	
	public InvoiceBuilder(SizeSpec bodySize, SizeSpec headerSize, Order order) {
		this.bodySize = bodySize;
		this.headerSize = headerSize;
		this.order = order;
	}
	
	public void asPdf(Path dest) {

		try {

			PDFBuilder writer = new PDFBuilder();

			// Add company address info

			Table companyAddressSection = new Table(new TableConfig(30));
			companyAddressSection
					.withRow(new Row(headerSize)
							.withColumn(
									new Column(new Image(order.getCompanyLogo().toString())).withPercentileWidth(20))
							.withColumn(new Column("[" + order.getCompanyName() + "]").withPercentileWidth(80)))
					.withRow(new Row(bodySize))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCompanyAddress())))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCompanyCity())))
					.withRow(new Row(bodySize)
							.withColumn(new Column(order.getCompanyState() + " " + order.getCompanyZIP())))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCompanyPhone())))
					.withRow(new Row(bodySize).withColumn(new Column("Fax: " + order.getCompanyFax())));
			companyAddressSection.commit();
			writer.appendTable(companyAddressSection, false);

			// Reset Y Coordinates
			writer.resetY();

			// Add Invoice Id and Customer Id

			Table DateSection = new Table(new TableConfig());
			DateSection
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(80))
							.withColumn(new Column(
									new TextControl("INVOICE").withFont(Font.HELVETICA_BOLD_OBLIQUE).setFontSize(12))))
					.withRow(new Row(bodySize))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(80))
							.withColumn(new Column("Date: " + order.getDateCreated())))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(80))
							.withColumn(new Column("Invoice #: " + order.getOrderId())))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(80))
							.withColumn(new Column("Customer Id: " + order.getCustomerId())))
					.withRow(new Row(bodySize)).withRow(new Row(bodySize));
			DateSection.commit();
			writer.appendTable(DateSection, false);

			// Add Billing Address

			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 2);
			writer.drawRect(new Color(255, 229, 204), Constants.PAGE_WIDTH / 3f, Constants.DEFAULT_XS_PADDING, true);
			writer.writeText(new XCoordinate(Constants.DEFAULT_BORDER_X, Constants.PAGE_WIDTH), new SizeSpec(4),
					new TextControl("BILL TO"));

			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 2);
			writer.drawRect(new Color(255, 229, 204), Constants.PAGE_WIDTH / 3f, Constants.DEFAULT_XS_PADDING, true);

			Table billingAddressSection = new Table(new TableConfig());
			billingAddressSection.withRow(new Row(bodySize).withColumn(new Column(order.getCustomerName())))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCustomerAddress())))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCustomerCity())))
					.withRow(new Row(bodySize)
							.withColumn(new Column(order.getCustomerState() + " " + order.getCustomerZIP())))
					.withRow(new Row(bodySize).withColumn(new Column(order.getCustomerPhone())));
			billingAddressSection.commit();
			writer.appendTable(billingAddressSection, false);

			// Add Order Items

			float borderStartY = 0.0f;
			float borderEndY = 0.0f;

			// Header
			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 2);

			borderStartY = writer.currentY();
			writer.nextY(Constants.DEFAULT_SMALL_PADDING);
			// writer.drawRect(Color.LIGHT_GRAY, PDFWriter.PAGE_WIDTH,
			// PDFWriter.DEFAULT_XS_PADDING, true);

			Table orderItemsHeaders = new Table(new TableConfig());
			orderItemsHeaders.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(5))
					.withColumn(new Column("NAME").withPercentileWidth(25f))
					.withColumn(new Column("DESCRIPTION").withPercentileWidth(50f))
					.withColumn(new Column("PRICE").withPercentileWidth(20f)));
			orderItemsHeaders.commit();
			writer.appendTable(orderItemsHeaders, false);

			writer.drawRect(Color.LIGHT_GRAY, Constants.PAGE_WIDTH, Constants.DEFAULT_XS_PADDING, true);
			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 2);

			// Body

			List<OrderItem> products = order.getProducts();

			for (int i = 0; i < products.size(); i++) {

				Table orderItemsBody = new Table(new TableConfig());

				orderItemsBody.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(5))
						.withColumn(new Column(products.get(i).getName()).withPercentileWidth(25f))
						.withColumn(new Column(products.get(i).getDescription()).withPercentileWidth(50f))
						.withColumn(new Column("$ " + products.get(i).getPrice().toString()).withPercentileWidth(20f)));

				orderItemsBody.commit();
				writer.appendTable(orderItemsBody, false);

				if (i < products.size() - 1) {
					// writer.nextY(PDFWriter.DEFAULT_XS_PADDING);
					writer.drawRect(new Color(204, 229, 255), Constants.PAGE_WIDTH, Constants.DEFAULT_XS_PADDING, true,
							true, false);
				}
			}

			borderEndY = writer.currentY();

			// Draw Table Outline

			writer.drawRect(Color.lightGray, Constants.DEFAULT_BORDER_X, borderStartY, Constants.PAGE_WIDTH,
					borderEndY - borderStartY, false, true, false);

			// Add separator

			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 5);
			writer.drawRect(Color.LIGHT_GRAY, Constants.PAGE_WIDTH, Constants.DEFAULT_XS_PADDING, true);

			// Add Total Prices

			Table pricesSection = new Table(new TableConfig());
			pricesSection
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
							.withColumn(new Column("Subtotal").withPercentileWidth(20))
							.withColumn(new Column("$ " + order.getSubtotal()).withPercentileWidth(20)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
							.withColumn(new Column("Taxable").withPercentileWidth(20))
							.withColumn(new Column("$ " + order.getTaxable()).withPercentileWidth(20)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
							.withColumn(new Column("Tax rate").withPercentileWidth(20))
							.withColumn(new Column("$ " + order.getTaxRate()).withPercentileWidth(20)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
							.withColumn(new Column("Tax due").withPercentileWidth(20))
							.withColumn(new Column("$ " + order.getTaxDue()).withPercentileWidth(20)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
							.withColumn(new Column("Other").withPercentileWidth(20))
							.withColumn(new Column("$ " + order.getOther()).withPercentileWidth(20)))

			;
			pricesSection.commit();
			writer.appendTable(pricesSection, false);

			// Add separator

			writer.nextY(Constants.DEFAULT_XS_PADDING);
			writer.drawRect(Color.LIGHT_GRAY, Constants.PAGE_WIDTH, Constants.DEFAULT_XS_PADDING, true, true, false);

			Table totalPriceSection = new Table(new TableConfig());
			totalPriceSection.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(60))
					.withColumn(
							new Column(new TextControl("TOTAL DUE").setFont(Font.TIMES_BOLD)).withPercentileWidth(20))
					.withColumn(new Column("$ " + order.getTotal()).withPercentileWidth(20)));
			totalPriceSection.commit();
			writer.appendTable(totalPriceSection, false);

			writer.drawRect(Color.LIGHT_GRAY, Constants.PAGE_WIDTH, Constants.DEFAULT_XS_PADDING, true);
			writer.nextY(Constants.DEFAULT_XS_PADDING);

			// Check payment information

			Table totalPriceExtrasSection = new Table(new TableConfig());
			totalPriceExtrasSection.withRow(new Row(bodySize)).withRow(new Row(bodySize)
					.withColumn(new Column().withPercentileWidth(65))
					.withColumn(new Column(new TextControl("Make all checks payable to").setFont(Font.COURIER_OBLIQUE))
							.withPercentileWidth(35)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(65)).withColumn(
							new Column(new TextControl(order.getCompanyName()).setFont(Font.COURIER_OBLIQUE))
									.withPercentileWidth(35)));
			totalPriceExtrasSection.commit();
			writer.appendTable(totalPriceExtrasSection, false);

			writer.nextY(Constants.DEFAULT_SMALL_PADDING * 12);

			// Footer Section

			Table footerSection = new Table(new TableConfig());
			footerSection
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(25)).withColumn(
							new Column(new TextControl("If you have any questions about this invoice, please contact")
									.setFont(Font.HELVETICA_OBLIQUE)).withPercentileWidth(50))
							.withColumn(new Column().withPercentileWidth(25)))
					.withRow(new Row(bodySize).withColumn(new Column().withPercentileWidth(37))
							.withColumn(new Column(
									new TextControl(order.getCompanyEmail()).setFont(Font.HELVETICA_OBLIQUE)))
							.withColumn(new Column()));
			footerSection.commit();
			writer.appendTable(footerSection, false);

			writer.flush(dest);

		} catch (Exception e) {
			throw new RuntimeException("Error occured during PDF Generation: " + e.getMessage());
			// return null;
		}
	}

}
