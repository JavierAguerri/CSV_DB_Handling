package com.example.csvdbhandling;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class Order implements Comparable<Order> {

	private static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("M/d/yyyy");

	private final String region;
	private final String country;
	private final String item_type;
	private final String sales_channel;
	private final String order_priority;
	private final Date order_date;
	private final int order_id;
	private final Date ship_date;
	private final int units_sold;
	private final BigDecimal unit_price;
	private final BigDecimal unit_cost;
	private final BigDecimal total_revenue;
	private final BigDecimal total_cost;
	private final BigDecimal total_profit;

	public Order(String[] csvLine) throws Exception {
		this.region = csvLine[0];
		this.country = csvLine[1];
		this.item_type = csvLine[2];
		this.sales_channel = csvLine[3];
		this.order_priority = csvLine[4];
		java.util.Date order_date = CSV_DATE_FORMAT.parse(csvLine[5]);
		this.order_date = new Date(order_date.getTime());
		java.util.Date ship_date = CSV_DATE_FORMAT.parse(csvLine[7]);
		this.ship_date = new Date(ship_date.getTime());
		this.order_id = Integer.parseInt(csvLine[6]);
		this.units_sold = Integer.parseInt(csvLine[8]);
		this.unit_price = new BigDecimal(csvLine[9]);
		this.unit_cost = new BigDecimal(csvLine[10]);
		this.total_revenue = new BigDecimal(csvLine[11]);
		this.total_cost = new BigDecimal(csvLine[12]);
		this.total_profit = new BigDecimal(csvLine[13]);
	}

	@Override
	public int compareTo(Order other) {
		return Integer.compare(this.order_id, other.getOrderId());
	}

	public int getOrderId() {
		return this.order_id;
	}

	public String exportCSVline() throws Exception {
		String[] values = {
				this.region,
				this.country,
				this.item_type,
				this.sales_channel,
				this.order_priority,
				CSV_DATE_FORMAT.format(this.order_date),
				Integer.toString(this.order_id),
				CSV_DATE_FORMAT.format(this.ship_date),
				Integer.toString(this.units_sold),
				this.unit_price.toString(),
				this.unit_cost.toString(),
				this.total_revenue.toString(),
				this.total_cost.toString(),
				this.total_profit.toString()
		};
		String csvLine = String.join(",", values);
		//	    System.out.println(csvLine);
		return csvLine;
	}

	public void prepareStatement(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, region);
		preparedStatement.setString(2, country);
		preparedStatement.setString(3, item_type);
		preparedStatement.setString(4, sales_channel);
		preparedStatement.setString(5, order_priority);
		preparedStatement.setDate(6, order_date);
		preparedStatement.setInt(7, order_id);
		preparedStatement.setDate(8, ship_date);
		preparedStatement.setInt(9, units_sold);
		preparedStatement.setBigDecimal(10, unit_price);
		preparedStatement.setBigDecimal(11, unit_cost);
		preparedStatement.setBigDecimal(12, total_revenue);
		preparedStatement.setBigDecimal(13, total_cost);
		preparedStatement.setBigDecimal(14, total_profit);
	}
}
