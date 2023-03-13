package com.example.csvdbhandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class OrdersDao implements OrdersDaoInterface {
	private final String dbUrl;
	private final String dbName;
	private final String username;
	private final String password;

	private static final String DB_BATCHEDSTATEMENTS = "?rewriteBatchedStatements=true";

	public final LinkedHashMap<String, Map<String, String>> fieldPropsMap;

	public OrdersDao(String dbUrl, String dbName, String username, String password, String orderFieldsConfigPath) throws Exception {
		// set DB parameters
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.username = username;
		this.password = password;

		// set fieldPropsMap
		fieldPropsMap = new LinkedHashMap<>();
		try {
			MiscUtils.readFieldsConfig(fieldPropsMap, orderFieldsConfigPath);			
		} catch (Exception e) {
			System.out.println("Failed when reading fields config file.");
			throw e;
		}

		// initialize DB
		dropDB();
		createOrdersDatabase();
		createOrdersTable();
	}

	private void dropDB() throws SQLException {
		try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP DATABASE IF EXISTS " + dbName);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createOrdersDatabase() throws SQLException {
		try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void createOrdersTable() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS orders (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
		// retrieve table field names and types from fieldPropsMap
		for (Map.Entry<String, Map<String, String>> entry : fieldPropsMap.entrySet()) {
			String name = entry.getKey();
			Map<String, String> properties = entry.getValue();
			String typeSQL = properties.get("typeSQL");
			sb.append(name + " " + typeSQL + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		String createOrdersTableString = sb.toString();
		try (Connection connection = DriverManager.getConnection(dbUrl + dbName, username, password);
				Statement statement = connection.createStatement()) {
			statement.executeUpdate(createOrdersTableString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void insertOrders (OrderList orderList) throws SQLException {
		try (Connection connection = DriverManager.getConnection(dbUrl + dbName + DB_BATCHEDSTATEMENTS, username, password)) {
			// Get column names from fieldPropsMap
			List<String> columnNames = new ArrayList<>();
			for (Map.Entry<String, Map<String, String>> entry : fieldPropsMap.entrySet()) {
				columnNames.add(entry.getKey());
			}
			String sql = createInsertStatement(columnNames);
			//			System.out.println("sql: " + sql);

			PreparedStatement preparedStatement = connection.prepareStatement(sql);

			// Set values
			for (Order order : orderList.orders) {
				order.prepareStatement(preparedStatement);
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static String createInsertStatement(List<String> columnNames) {
		StringBuilder sb = new StringBuilder("INSERT INTO orders (");
		StringBuilder values = new StringBuilder("VALUES (");
		for (int i = 0; i < columnNames.size(); i++) {
			sb.append(columnNames.get(i));
			values.append("?");
			if (i < columnNames.size() - 1) {
				sb.append(", ");
				values.append(", ");
			}
		}
		sb.append(") ");
		values.append(")");
		return sb.toString() + values.toString();
	}

	public void displayStats() throws SQLException {
		// retrieve columns which we use to output stats
		ArrayList<String> columnsForStats = new ArrayList<>();
		// Get column names for stats from fieldPropsMap
		for (Map.Entry<String, Map<String, String>> entry : fieldPropsMap.entrySet()) {
			Map<String, String> properties = entry.getValue();
			if (Boolean.parseBoolean(properties.get("stats"))) {
				columnsForStats.add(entry.getKey());				
			}
		}

		// craft query - it will output just one result: a JSON string. The stats aggregation is handled in the SQL query itself
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT JSON_OBJECT(\n");
		for (int i = 0; i < columnsForStats.size(); i++) {
			String column = columnsForStats.get(i);
			String subquery = "SELECT JSON_OBJECTAGG(" + column + ", count) AS result FROM (SELECT " + column + ", COUNT(*) AS count FROM ordersDB.orders GROUP BY " + column + ") AS counts";
			sb.append("  '" + column + "', (\n" + subquery + ")\n");
			if (i < columnsForStats.size() - 1) {
				sb.append("  ,\n");
			}
		}
		sb.append(") AS result;");
		String query = sb.toString();
		//		System.out.println("query: " + query);

		// execute query
		String statsJSONstring = "";
		try (Connection connection = DriverManager.getConnection(dbUrl + dbName, username, password)) {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			statsJSONstring = resultSet.getString("result");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		//		System.out.println(statsJSONstring);

		// Pretty print result
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(statsJSONstring);
		String prettyJsonString = gson.toJson(jsonElement);
		System.out.println(prettyJsonString);
	}
}