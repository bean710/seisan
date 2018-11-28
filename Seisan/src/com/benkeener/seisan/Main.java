package com.benkeener.seisan;
import com.benkeener.seisan.Server;

public class Main {
	public static int port = 80;
	
	public static void main(String[] args) throws Exception {
		MySQLHandler sql = new MySQLHandler();
		sql.loadDataBase();
		
		Server httpServer = new Server();
		httpServer.Start(port, sql);
		
	}
}
