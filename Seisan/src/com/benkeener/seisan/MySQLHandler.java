package com.benkeener.seisan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MySQLHandler {
	private Connection connection = null;
	private Statement statement = null;
	
	private PreparedStatement addFoo = null;
	
	public void loadDataBase() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		
		connection = DriverManager.getConnection("jdbc:mysql://localhost/jtest?user=ben&password=password");
		
		statement = connection.createStatement();
		
		addFoo = connection.prepareStatement("INSERT INTO foo VALUES (default, ?, ?)");
		addFoo.setString(1, "bar");
		addFoo.setInt(2, 32);
		addFoo.executeUpdate();
		
		ResultSet resultSet = statement.executeQuery("SELECT * FROM foo");
		parseMeta(resultSet);
		parseData(resultSet);
		
	}
	
	private void parseMeta(ResultSet resultSet) throws Exception {

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
		
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray formatToJSON(ResultSet resultSet) throws Exception {
		JSONArray ret = new JSONArray();
		
		while (resultSet.next()) {
			JSONObject tmp = new JSONObject();
			
			tmp.put("id", resultSet.getInt("ID"));
			tmp.put("name", resultSet.getString("name"));
			tmp.put("age", resultSet.getInt("age"));
			
			ret.add(tmp);
		}
		
		return ret;
		
	}
}
