package com.benkeener.seisan;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

public class Server {
	private HttpServer server;
	
	public void Start(int port, MySQLHandler mySql) {
		
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Server started on port " + String.valueOf(port));

		server.createContext("/productAPI", new Handlers.FooHandler(mySql));
		server.createContext("/contactAPI", new Handlers.ContactHandler(mySql));
		server.createContext("/", new Handlers.RootHandler());
		
		server.setExecutor(null);
		server.start();
		
	}
	
	public void Stop() {
		server.stop(0);
		System.out.println("Server stopped.");
	}
}
