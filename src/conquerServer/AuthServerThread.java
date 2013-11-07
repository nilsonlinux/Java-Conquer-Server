/**
 * 
 */
package conquerServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import packets.*;

/**
 * @author jan-willem
 *
 */
public class AuthServerThread implements Runnable {
	private Socket client = null;
	private AuthServer authServer = null;
	private InputStream in = null;
	private OutputStream out = null;
	private Cryptography cipher = new Cryptography(); 
	
	/**
	 * 
	 * @param client
	 * @param authServer
	 * @throws IOException 
	 */
	public AuthServerThread(Socket client, AuthServer authServer) throws IOException {
		this.client = client;
		this.authServer = authServer;
		in = client.getInputStream();
		out = client.getOutputStream();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("Incomming connection on AuthServer");
		while(true) {
			try {
				byte[] data = new byte[100];
				in.read(data);
				cipher.decrypt(data);
				IncommingPacket ip = new IncommingPacket(data);
			
				switch(ip.getPacketType()) {
					case auth_login_packet:
						Auth_Login_Packet ALP = new Auth_Login_Packet(ip);
						Auth_Login_Response ALR = new Auth_Login_Response(1000000, 0, "127.0.0.1", 5816);
						ALR.encrypt(cipher);
						ALR.send(out);
					default:
						break;
				}
				
			} catch (IOException e) {
				authServer.disconnect(this);
				break;
			}
		}		
	}
	
}
