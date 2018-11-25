package com.benkeener.seisan;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handlers {

	public static class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exc) throws IOException {
			String res = "Success";
			exc.sendResponseHeaders(200, res.length());
			OutputStream out = exc.getResponseBody();
			out.write(res.getBytes());
			out.close();
		}
		
	}
	
}
