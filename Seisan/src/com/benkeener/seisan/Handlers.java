package com.benkeener.seisan;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.benkeener.seisan.MySQLHandler;

public class Handlers {

	public static class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exc) throws IOException {
			String root = "./../static/";
			URI uri = exc.getRequestURI();
			File file = new File(root + uri.getPath()).getCanonicalFile();
			if (uri.toString().equals("/")) {
				file = new File(root + "/index.html").getCanonicalFile();
			}
			
			System.out.println(file.getPath());
			
			if (!file.isFile()) {
				//File does not exist
				String res = "404 File Not Found";
				exc.sendResponseHeaders(404, res.length());
				OutputStream out = exc.getResponseBody();
				out.write(res.getBytes());
				out.close();
			} else {
				exc.sendResponseHeaders(200, 0);
				OutputStream out = exc.getResponseBody();
				FileInputStream fs = new FileInputStream(file);
				final byte[] buffer = new byte[0x10000];
				int i = 0;
				while ((i = fs.read(buffer)) >= 0) {
					out.write(buffer, 0, i);
				}
				fs.close();
				out.close();
			}
		}
		
	}
	
	private static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    return map;  
	}
	
	public static class FooHandler implements HttpHandler {
		private MySQLHandler mySql;

		public FooHandler(MySQLHandler mySql) {
			super();
			this.mySql = mySql;
		}
		
		@Override
		public void handle(HttpExchange exc) throws IOException {
			
			int sku = -1;
			String name = null;
			
			if (exc.getRequestURI().getQuery() != null) {
				String query = exc.getRequestURI().getQuery();
				
				Map<String, String> params = getQueryMap(query);
				if (params.containsKey("sku")) {
					sku = Integer.valueOf(params.get("sku"));
				} else {
					sku = -1;
				}
				if (params.containsKey("name")) {
					name = params.get("name");
				} else {
					name = null;
				}
			}
			
			System.out.println(name + "  :  " + String.valueOf(sku));
			
			JSONArray preFoo = null;
			try {
				preFoo = this.mySql.getProductsJSON(sku, name);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			exc.sendResponseHeaders(200, 0);
			String foo = preFoo.toString();
			System.out.println(foo);
			OutputStream out = exc.getResponseBody();
			out.write(foo.getBytes());
			out.close();
		}
		
	}
	
}
