import java.io.*; 
import java.net.*;
import java.util.Objects;

public class Server implements Runnable {
	DatagramSocket server_socket = null;
	Thread t = new Thread(this);	
	String received_string = "";
	// three way handshake flags
	boolean SYN = false;
	boolean ACK = false;	
	int syn_num = 0;
	int ack_num = 0;
	
	public Server() throws SocketException {
		server_socket = new DatagramSocket(8080);
		server_socket.setSoTimeout(100);
		
		System.out.println("Server is running...");
		System.out.println("Waiting for clients...");		
		t.start();
	}
	
	public void send(String message) throws IOException {
		byte [] sendData = message.getBytes();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, 9090);
		server_socket.send(packet);
	}
	
	public void run() {		
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) { }
			
			byte[] receiveData = new byte[256];
			DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
			
			try {
				server_socket.receive(packet);
			} catch (IOException e) { }
			
			String client_data = new String(receiveData);
			client_data = client_data.trim();
			
			if(client_data.startsWith("CONNECTING")) {
				System.out.println("Client SYN bit received by server.");
				System.out.println("Server ACK bit sent to client.");
				System.out.println("Server SYN bit sent to client.");
				
				SYN = true;
				try {
					send("ESTABLISHED");
				} catch (IOException e) { }
			} 
			
			else if(client_data.startsWith("ESTABLISHED")) {
				System.out.println("Client ACK bit received by server.");				
				ACK = true;				
				if(SYN && ACK) {
					System.out.println("Established connection with client.");
					received_string = "";
				}
			}
			
			// receives data from client
			else if(client_data.startsWith("DATA") && ACK && SYN) {
				String [] tokens = client_data.split("/");
				syn_num = Integer.parseInt(tokens[2]);				
				received_string += tokens[1];
				System.out.println(received_string);
				try {
					ack_num++;
					send("DATA/"+syn_num);
					System.out.println("sync_num: "+syn_num);
				} catch (IOException e) {}					
			}else if(client_data.startsWith("EXIT")){
				System.out.println("-EXIT-");
				break;
			}
		}
	}
	
	public static void main(String[] args) throws SocketException {
		new Server();
	}
}
