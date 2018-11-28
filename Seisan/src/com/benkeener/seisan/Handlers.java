package com.benkeener.seisan;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import com.sun.net.httpserver.Headers;
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
				// File does not exist
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

	private static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
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

	public static class ContactHandler implements HttpHandler {
		MySQLHandler mySql;

		public static void parseQuery(String query, Map<String, Object> parameters)
				throws UnsupportedEncodingException {

			if (query != null) {
				String pairs[] = query.split("[&]");
				for (String pair : pairs) {
					String param[] = pair.split("[=]");
					String key = null;
					String value = null;
					if (param.length > 0) {
						key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
					}

					if (param.length > 1) {
						value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
					}

					if (parameters.containsKey(key)) {
						Object obj = parameters.get(key);
						if (obj instanceof List<?>) {
							@SuppressWarnings("unchecked")
							List<String> values = (List<String>) obj;
							values.add(value);

						} else if (obj instanceof String) {
							List<String> values = new ArrayList<String>();
							values.add((String) obj);
							values.add(value);
							parameters.put(key, values);
						}
					} else {
						parameters.put(key, value);
					}
				}
			}
		}

		public ContactHandler(MySQLHandler mySql) {
			super();
			this.mySql = mySql;
		}

		@Override
		public void handle(HttpExchange exc) throws IOException {
			System.out.println("foo");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(exc.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			System.out.println(query);
			parseQuery(query, parameters);
			
			System.out.println(parameters.toString());
			
			String firstName = (String) parameters.get("firstName");
			String lastName = (String) parameters.get("lastName");
			String email = (String) parameters.get("email");
			int zip = Integer.valueOf((String) parameters.get("zip"));
			String comment = (String) parameters.get("comment");
			
			try {
				mySql.addContact(firstName, lastName, email, zip, comment);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String response = "Thanks";
			exc.sendResponseHeaders(200, response.length());
            OutputStream os = exc.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
		}

	}

}
