package com.example.csvdbhandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrderList {

	public List<Order> orders;
	public final LinkedHashMap<String, Map<String, String>> fieldPropsMap;

	public OrderList(String orderFieldsConfigPath) throws Exception {
		orders = new ArrayList<>();
		fieldPropsMap = new LinkedHashMap<>();
		try {
			MiscUtils.readFieldsConfig(fieldPropsMap, orderFieldsConfigPath);			
		} catch (Exception e) {
			System.out.println("Failed when reading fields config file.");
			throw e;
		}
	}

	public void loadOrderListFromCSV(String orderCSVPath) throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader(orderCSVPath))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Order order;
				order = new Order(values);
				orders.add(order);
			}
		} catch (Exception e) {
			// if there is a problem loading orders, it won't load any order at all (will assign an empty list)
			orders = new ArrayList<>();
			e.printStackTrace();
			throw e;
		}
	}

	public void sortOrders() {
		Collections.sort(orders);
	}

	public void exportOrderListToCSV(String outputOrdersPath) throws Exception {
		try (PrintWriter writer = new PrintWriter(new FileWriter(outputOrdersPath))) {
			// Write header line
			List<String> headers = new ArrayList<>();
			for (Map.Entry<String, Map<String, String>> entry : fieldPropsMap.entrySet()) {
				headers.add(entry.getValue().get("nameCSV"));
			}
			writer.println(String.join(",", headers));

			// Write data lines
			for (Order order : orders) {
				String csvLine = order.exportCSVline();
				writer.println(csvLine);
			}
		} catch (Exception e) {
			// if there is a problem writing orders, delete the file in case it was partially written
			File outputFile = new File(outputOrdersPath);
			if (outputFile.exists()) {
				outputFile.delete();
			}
			e.printStackTrace();
			throw e;
		}
	}

	//	FOR TESTING ONLY METHODS

	public void displayFieldPropsMap() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(fieldPropsMap);
		System.out.println(json);
	}

	public void displayOrders(int i) {
		List<Order> ordersToDisplay = orders.subList(0, Math.min(i, orders.size()));
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(ordersToDisplay);
		System.out.println(json);
	}
}
