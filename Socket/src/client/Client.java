package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static final int CONNECT_PORT = 5987;
	public static final String CONNECT_ADDRESS = "localhost";

	public static void main(String[] args) throws UnknownHostException, IOException {
		for (int i = 0; i < 2; i++) {
			Socket client = new Socket(CONNECT_ADDRESS, CONNECT_PORT);
			new Thread(new ConnectThread(i, client)).run();
		}
	}
}

class ConnectThread implements Runnable {

	Socket client = null;
	Integer threadNum = null;
	InputStream in = null;
	OutputStream out = null;

	public ConnectThread(Integer threadNum, Socket client) {
		this.threadNum = threadNum;
		this.client = client;
	}

	@Override
	public void run() {
		try {
			in = client.getInputStream();
			out = client.getOutputStream();
			SendMessage();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Closing this socket will also close the socket's InputStream and OutputStream.
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void SendMessage() throws IOException {
		try {
			while (true) {
				String message = "thread" + threadNum + "送出訊息";
				out.write(message.getBytes());
				out.flush();
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}