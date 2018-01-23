/*
 * @author antipro
 * 创建于 2009-10-23
 * 文件服务器
 */
package ftp;

import java.io.*;
import java.net.*;

/**
 * 文件传输线程
 **/
class TransferThread extends Thread {
	File file;
	FileTransfer filetansfer;

	public TransferThread(File file) {
		this.file = file;
		filetansfer = new FileTransfer();
		start();
	}

	public void run() {
		try {
			filetansfer.sendFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSended(boolean unsended) {
		filetansfer.unsended = unsended;
	}

	public int getPORT() {
		return filetansfer.PORT;
	}
}

/**
 * 单用户守护线程 建立一个连接以便发送文件
 */
class UserThread extends Thread {

	private static final String USERNAME = "antipro";
	private static final String PASSWORD = "348834627";
	private static final String REQUEST_USERNAME = "Please enter your name: ";
	private static final String REQUEST_PASSWORD = "Please enter your password: ";

	Socket socket;
	BufferedReader in;
	PrintWriter out;
	private File folder;
	private TransferThread tf;

	public UserThread(Socket s, File folder) throws IOException {
		this.socket = s;
		this.folder = folder;
		start();
	}

	public void run() {
		try {
			System.out.println("Connection accepted: " + socket);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
			// 请求输入用户名与密码
			// 正确后才会跳出循环
			while (true) {
				out.println(REQUEST_USERNAME);
				if (USERNAME.equals(in.readLine()))
					out.println(REQUEST_PASSWORD);
				else
					continue;
				if (PASSWORD.equals(in.readLine()))
					break;
				else
					continue;
			}
			out.println(Instructions.READY);
			transaction: while (true) {
				String message = in.readLine();
				switch (message) {
				case Instructions.REQUEST_FILE_LIST: {
					String[] list = this.folder.list();
					for (int i = 0; i < list.length; i++) {
						out.println(list[i]);
					}
					out.println("=====================");
					out.println(Instructions.REQUEST_FILE_NAME);
				}
					break;
				case Instructions.POST_FILE_NAME: {
					String fileName = in.readLine();
					File file = new File(this.folder, fileName);
					if (file.exists() && !file.isDirectory()) {
						out.println(Instructions.FILE_EXISTED);
						int packet_number = FileUtils.packetNumber(file);
						out.println(packet_number);
						tf = new TransferThread(file);
						out.println(tf.getPORT());
					} else {
						out.println(Instructions.NO_SUCH_FILE);
					}
				}
					break;
				case Instructions.DONE: {
					if (tf != null)
						tf.setSended(false);
					break transaction; // 跳出循环并关闭
				}
				default:
					System.out.println(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Closing this socket");
			try {
				socket.close();
			} catch (IOException e1) {
			}
		}
	}
}

/**
 * 2009-10-23 服务器线程
 */
public class FileServer {

	private static final String REQUEST_FOLDER = "Please enter one folder path which to be used as file directory: ";
	public static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("Server Started: " + s);
		System.out.println(REQUEST_FOLDER);
		BufferedReader pathReader = new BufferedReader(new InputStreamReader(System.in));
		File folder = null;
		while (true) {
			String folderPath = pathReader.readLine();
			if (folderPath.equals("")) {
				System.out.println(REQUEST_FOLDER);
				continue;
			}
			folder = new File(folderPath);
			if (!folder.isDirectory()) {
				System.out.println(REQUEST_FOLDER);
				continue;
			}
			break;
		}
		System.out.println("Directory Is Ready");
		try {
			while (true) {
				// Blocks until a connection occurs:
				Socket socket = s.accept();
				try {
					// 产生一个新的连接
					new UserThread(socket, folder);
				} catch (IOException e) {
					// If it fails,close the socket,
					// otherwise the thread will close it:
					socket.close();
				}
			}
		} finally {
			System.out.println("Closing this server, bye.");
			s.close();
		}
	}
}