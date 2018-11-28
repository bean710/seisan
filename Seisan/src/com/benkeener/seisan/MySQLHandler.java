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
	private Statement statement = null;
	
	private PreparedStatement addProductStatement = null;
	private PreparedStatement getProductsStatement = null;
	
	public void loadDataBase() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		
		connection = DriverManager.getConnection("jdbc:mysql://localhost/seisan?user=ben&password=password");
		
		statement = connection.createStatement();
		
		addProductStatement = connection.prepareStatement("INSERT INTO products VALUES (?, ?, ?, ?, ?)"); //sku, name, price, qty, description
		getProductsStatement = connection.prepareStatement("SELECT * FROM products");
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
		addProductStatement.setInt(3, qty);
		addProductStatement.setString(4, description);
		addProductStatement.executeQuery();
	}
	
	private ResultSet getProducts() throws SQLException {
		return getProductsStatement.executeQuery();
	}
	
	public JSONArray getProductsJSON() throws SQLException, Exception {
		return formatToJSON(getProducts());
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
