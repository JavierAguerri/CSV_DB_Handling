package com.example.csvdbhandling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	private static final String ORDERS_LINK = System.getenv("ORDERS_LINK");
	private static final String ORDER_FIELDS_CONFIG = "src/config/tables/orderFieldsConfig.json";
	private static final String INPUT_ORDERS_PATH= "src/config/csv/orders.csv";
	private static final String OUTPUT_ORDERS_PATH = INPUT_ORDERS_PATH.replace(".csv", "_sorted.csv");

	private static final String DB_URL = System.getenv("DB_URL");
	private static final String DB_NAME = System.getenv("DB_NAME");
	private static final String DB_USER = System.getenv("DB_USER");
	private static final String DB_PASS = System.getenv("DB_PASS");

	public static void main( String[] args )
	{
		// Initialization
		try {
			Files.deleteIfExists(Paths.get(INPUT_ORDERS_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to delete file: " + INPUT_ORDERS_PATH);
		}
		try {
			Files.deleteIfExists(Paths.get(OUTPUT_ORDERS_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to delete file: " + OUTPUT_ORDERS_PATH);
		}
		try {
			MiscUtils.downloadCSVfromLink(ORDERS_LINK,INPUT_ORDERS_PATH);
		} catch (IOException e) {
			System.out.println("Failed when downloading CSV file.");
		}

		// Dado un fichero .csv con registros de pedidos, la aplicación deberá generar otro fichero con los registros ordenados por número de pedido
		OrderList orderList = null;
		try {
			orderList = new OrderList(ORDER_FIELDS_CONFIG);
		} catch (Exception e1) {
			System.out.println("Failed when initializing OrderList.");
		}
		try {
			orderList.loadOrderListFromCSV(INPUT_ORDERS_PATH);
		} catch (Exception e) {
			System.out.println("Failed when loading orders from CSV file.");
		}
		orderList.sortOrders();
		try {
			orderList.exportOrderListToCSV(OUTPUT_ORDERS_PATH);
		} catch (Exception e) {
			System.out.println("Failed when exporting orders to CSV file.");
		}
		// For testing only methods
		//		orderList.displayFieldPropsMap();
		//		orderList.displayOrders(5);

		// Además de eso deberá importar en una BD todos esos datos
		OrdersDao ordersDao = null;
		try {
			ordersDao = new OrdersDao(DB_URL, DB_NAME, DB_USER, DB_PASS, ORDER_FIELDS_CONFIG);
		} catch (Exception e) {
			System.out.println("Failed when initializing OrdersDao.");
		}
		try {
			ordersDao.insertOrders(orderList);
		} catch (Exception e) {
			System.out.println("Failed when inserting orders.");
		}

		// y mostrar, al terminar el procesado, un resumen del número de pedidos de cada tipo según distintas columnas.
		try {
			ordersDao.displayStats();
		} catch (Exception e) {
			System.out.println("Failed when displaying stats.");
		}
		
		// improvement: load CSV directly into BBDD. Will require to adjust field names and date types.
	}
}
