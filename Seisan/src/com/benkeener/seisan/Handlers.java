package com.benkeener.seisan;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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
	
}
