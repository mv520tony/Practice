package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static final int ServerPort = 5987; // 使用socket的port

	public void run() throws IOException {
		ServerSocket server = null;
		try {
			// new socket server
			server = new ServerSocket(ServerPort);
			// 管理 thread
			ExecutorService threadExecutor = Executors.newCachedThreadPool();
			while (true) {
				// 如果client沒連線，會一卡在這行，直到連線出現
				System.out.println("等待client連線中....");
				Socket socket = server.accept();
				System.out.println("client已連線("+ socket.getRemoteSocketAddress() + ")... 時間:"
						+ Calendar.getInstance().getTime());
				threadExecutor.execute(new Request(socket));
			}
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
			 server.close();
		}
	}

	public class Request implements Runnable {

		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;

		public Request(Socket request) {
			this.socket = request;
		}

		public void run() {
			try {
				in = socket.getInputStream();
				out = socket.getOutputStream();
				PrintReqest();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {	
				try {
					//Closing this socket will also close the socket's InputStream and OutputStream.
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void PrintReqest() throws IOException {
			// 緩衝，如果講求效率可以調整此數字 http://defshine.github.io/java-socket.html
			byte[] buffer = new byte[1024];
			InputStream in = null;
			ByteArrayOutputStream tmp = null;

			try {
				in = socket.getInputStream();
				tmp = new ByteArrayOutputStream();
				int len;
				while ((len = in.read(buffer)) != -1) {
					tmp.write(buffer, 0, len);
				}
				byte[] inBytes = tmp.toByteArray();
				//後面用UTF-8是防止亂碼
				System.out.println("[Client request]" + new String(inBytes, "UTF-8"));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Server socketServer = new Server();
		socketServer.run();
	}

}
