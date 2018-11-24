import java.net.ServerSocket;

public class HTTPServer {
	
	private static int port = 80;
	
	public static void main(String[] args) throws Exception {
		final ServerSocket server = new ServerSocket(port);
		System.out.println("Listening on port " + String.valueOf(port));
		
		while (true) {
			
		}
		
	}
	
}
