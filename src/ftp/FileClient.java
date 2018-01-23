/*
 * @author antipro
 * ������ 2009-10-23
 * �ļ��ͻ���
 */
package ftp;

import java.io.*;
import java.net.*;

/**
 * 2009-10-23 �ͻ��˳���
 */
public class FileClient {

	private static final String RETRY = "File name is wrong, please try again: ";
	private static String file_chosed_name;

	public static void main(String[] args) throws IOException {
		InetAddress addr = InetAddress.getByName(null);
		System.out.println("Address = " + addr);
		Socket socket = new Socket(addr, FileServer.PORT);
		try {
			System.out.println("Socket = " + socket);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")),
					true);
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

			String s = new String();
			// �������̣���ȡ������,ֱ���յ�READYΪֹ��
			while (true) {
				s = in.readLine();
				if (s.equals(Instructions.READY))
					break;
				System.out.println(s);
				out.println(stdin.readLine());
			}
			out.println(Instructions.REQUEST_FILE_LIST);
			transaction: while (true) {
				String message = in.readLine();
				switch (message) {
				case Instructions.NO_SUCH_FILE: {
					System.out.println(RETRY);
				}
				case Instructions.REQUEST_FILE_NAME: {
					file_chosed_name = stdin.readLine();
					out.println(Instructions.POST_FILE_NAME);
					out.println(file_chosed_name);
				}
					break;
				case Instructions.FILE_EXISTED: {
					// �ļ��ֿ�����
					int packet_number = Integer.parseInt(in.readLine());
					// ָ���˿ں�
					int PORT = Integer.parseInt(in.readLine());
					// ��ʼ����
					FileTransfer filetransfer = new FileTransfer();
					filetransfer.filename = file_chosed_name;
					boolean success = filetransfer.receiveFile(socket.getInetAddress(), packet_number, PORT);
					System.out.println("File Transfered: " + success);
					out.println(Instructions.DONE);
					break transaction;
				}
				default:
					System.out.println(message);
				}
			}
		} finally {
			System.out.println("Closing...");
			socket.close();
		}
	}
}
