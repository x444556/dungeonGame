package net.ddns.x444556.dungeonServer;

public class ProgramServer {

	public ProgramServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Server running on port 8088");
		new Server(8088).run();
	}

}
