import java.io.*; 
import java.net.*;
import java.util.*;

public class Client implements Runnable {
	DatagramSocket socket = null;
	Thread t = new Thread(this);
	boolean has_established_connection = false;
	
	boolean SYN = false;
	boolean ACK = false;
	int syn_num = 0;
	int ack_num = 0;
	
	// packet dropping probability
	double [] DROP_PROBABILITY = {0.00, 0.25, 0.50, 0.75};
	String DATA = "Rhey Mark John Q. Casero";
	
	public Client() throws SocketException {
		socket = new DatagramSocket(9090);
		socket.setSoTimeout(100);
		t.start();
	}
	
	public void send(String message) throws IOException {
		byte [] sendData = message.getBytes();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, 8080);
		socket.send(packet);
	}
	
	public boolean dropPackets(double percent){
		double random = Math.random();

		if(percent == 0.0){
			return true;
		}else if(percent == 0.25){
			if (random <= 0.25){
				return true;
			}else{
				return false;
			}	
		}else if(percent == 0.50){
			if (random <= 0.50){
				return true;
			}else{
				return false;
			}
		}else if(percent == 0.75){
			if (random <= 0.75){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) { }
			
			byte [] receiveData = new byte[256];
			DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
			
			try {
				socket.receive(packet);
			} catch (IOException e) { }
			
			String server_data = new String(receiveData);
			server_data = server_data.trim();
			
			if(!has_established_connection && server_data.startsWith("ESTABLISHED")) {				
				System.out.println("Server ACK bit received by client.");
				System.out.println("Server SYN bit received by client.");
				System.out.println("Client ACK bit sent to server.");
				
				ACK = true;
				
				if(ACK && SYN) {
					has_established_connection = true;
					System.out.println("Established connection with server.");
					
					try {
						send("ESTABLISHED");
					} catch (IOException e) { }
				}
			}
			
			else if(!has_established_connection) {
				System.out.println("Establishing connection with server.....");
				System.out.println("Client SYN bit sent to server.");
				
				SYN = true;
				
				try {
					send("CONNECTING");
				} catch (IOException e) { }
			}
			
			else {
				String [] tokens = server_data.split("/");					
				Random rn = new Random();
				double percent = DROP_PROBABILITY[rn.nextInt(4)];
				if(syn_num < DATA.getBytes().length){
					if(server_data.startsWith("DATA")) {						
							syn_num++;	
						if(!dropPackets(percent)){					
							try {
								if(DATA.getBytes().length >= syn_num){
									System.out.println("sync_num: "+syn_num);
									send("DATA/" + DATA.charAt(syn_num)+"/"+syn_num);
								}
							} catch (IOException e) { }									
						}else{
							System.out.println("packet dropped :(");
						}
					}else {
						if(!dropPackets(percent)){
							try {
								System.out.println("sync_num: "+syn_num);
								send("DATA/" + DATA.charAt(syn_num)+"/"+syn_num);
							} catch (IOException e) { }
						}else{
							System.out.println("packet dropped :(");
						}
					}
				}else{
					System.out.println("-EXIT-");
					try {
						send("EXIT");
					} catch (IOException e) { }
					break;
				}
			}
		}
	}
	
	public static void main(String[] args) throws SocketException {
		new Client();
	}

}
