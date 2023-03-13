package com.example.csvdbhandling;

import java.sql.SQLException;

public interface OrdersDaoInterface {
	void insertOrders(OrderList orderList) throws SQLException;
	void displayStats() throws SQLException;
}
