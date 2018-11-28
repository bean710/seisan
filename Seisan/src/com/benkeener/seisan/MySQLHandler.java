package com.benkeener.seisan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	
	/*private void parseMeta(ResultSet resultSet) throws Exception {

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
        
    }
	
	private void parseData(ResultSet resultSet) throws Exception {
		
		while (resultSet.next()) {
			int id = resultSet.getInt("ID");
			String name = resultSet.getString("name");
			int age = resultSet.getInt("age");
			System.out.println("ID: " + String.valueOf(id));
			System.out.println("Name: " + name);
			System.out.println("Age: " + String.valueOf(age));
		}
		
	}*/
	
	public void addProduct(int sku, String name, int price, int qty, String description) throws SQLException {
		addProductStatement.setInt(1, sku);
		addProductStatement.setString(2, name);
		addProductStatement.setInt(3, price);
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
			tmp.put("price", resultSet.getInt("price"));
			tmp.put("qty", resultSet.getInt("qty"));
			tmp.put("description", resultSet.getString("description"));
			
			ret.add(tmp);
		}
		
		return ret;
		
	}
}
