package com.benkeener.seisan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MySQLHandler {
	private Connection connection = null;
	
	private PreparedStatement addProductStatement = null;
	public void loadDataBase() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		
		connection = DriverManager.getConnection("jdbc:mysql://localhost/seisan?user=ben&password=password");
		
		addProductStatement = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?)"); //sku, name, price, qty, description
		connection.prepareStatement("SELECT * FROM products");
	}
	
	public void addProduct(int sku, String name, double price, int qty, String description) throws SQLException {
		addProductStatement.setInt(1, sku);
		addProductStatement.setString(2, name);
		//addProductStatement.setInt(3, price);
		addProductStatement.setDouble(3, price);
		addProductStatement.setInt(4, qty);
		addProductStatement.setString(5, description);
		addProductStatement.executeQuery();
	}
	
	private ResultSet getProducts(int sku, String name) throws SQLException {
		PreparedStatement ps = null;
		if (sku > -1 && name != null) {
			ps = connection.prepareStatement("SELECT * FROM products WHERE sku=? AND name LIKE ?");
			ps.setInt(1, sku);
			ps.setString(2, "%" + name + "%");
		} else if (sku > -1) {
			ps = connection.prepareStatement("SELECT * FROM products WHERE sku=?");
			ps.setInt(1, sku);
		} else if (name != null) {
			ps = connection.prepareStatement("SELECT * FROM products WHERE name LIKE ?");
			ps.setString(1, "%" + name + "%");
		} else {
			ps = connection.prepareStatement("SELECT * FROM products");
		}
		
		System.out.println(ps.toString());
		
		return ps.executeQuery();
	}
	
	public JSONArray getProductsJSON(int sku, String name) throws SQLException, Exception {
		return formatToJSON(getProducts(sku, name));
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray formatToJSON(ResultSet resultSet) throws Exception {
		JSONArray ret = new JSONArray();
		
		while (resultSet.next()) {
			JSONObject tmp = new JSONObject();
			
			tmp.put("sku", resultSet.getInt("sku"));
			tmp.put("name", resultSet.getString("name"));
			tmp.put("price", resultSet.getDouble("price"));
			tmp.put("qty", resultSet.getInt("qty"));
			tmp.put("description", resultSet.getString("description"));
			
			ret.add(tmp);
		}
		
		return ret;
		
	}
	
	public void addContact(String firstName, String lastName, String email, int zip, String comment) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("INSERT INTO contact VALUES (?, ?, ?, ?, ?, DEFAULT)"); //
		ps.setString(1, firstName);
		ps.setString(2, lastName);
		ps.setString(3, email);
		ps.setInt(4, zip);
		ps.setString(5, comment);
		System.out.println(ps.toString());
		ps.executeUpdate();
	}
}
