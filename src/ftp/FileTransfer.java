/*
 * @author antipro
 * ������ 2009-10-24
 * �ļ�������
 */
package ftp;

import java.net.*;
import java.util.Vector;
import java.io.*;

/**
 * 2009-10-24 �ļ����䷽��
 */
public final class FileTransfer {
	public int PORT;
	public boolean unsended = true; // ����δ���ͱ�־
	public String filename;
	public DatagramSocket ds; // ������ר��DatagramSocket

	/**
	 * 
	 */
	public FileTransfer() {
		try {
			ds = new DatagramSocket();
			PORT = ds.getLocalPort();
		} catch (SocketException e) {
			e.printStackTrace();

		}
	}

	/**
	 * ��UDP��ʽ�����ļ�
	 * @param file ��������ļ����������·����
	 * @return ����ɹ�����true��ʧ�ܷ���false��
	 * @throws IOException
	 */
	public void sendFile(File file) throws IOException {
		byte[] buf = new byte[5050];
		try {
			System.out.println("Start Send: " + file);
			DatagramPacket dp = new DatagramPacket(buf, buf.length); // Ԥ��50�ֽڵĿռ�������
			ds.setSoTimeout(5000); // ���������ź����·���
			// ���ɰ���ȫ�����ݵ�����
			byte[][] filebuf = FileProcess.openFile(file);
			while (unsended) {
				// ����״̬��Ϣ
				ds.receive(dp);
				String[] request_lines = Dgram.toString(dp).split(",");
				// System.out.println(Dgram.toString(dp));
				for (int i = 0; i < request_lines.length; i++) {
					// ����Ҫ���͵İ����ͳ�ȥ
					int line_number = Integer.parseInt(request_lines[i].trim());
					ds.send(Dgram.toDatagram(filebuf[line_number], dp.getAddress(), dp.getPort()));
				}
			}
		} catch (Exception e) {
			 e.printStackTrace();
		} finally {
			ds.close();
		}
	}

	/**
	 * ��UDP��ʽ�����ļ�
	 * @param LocalPORT ׼���ı��ض˿�
	 * @param addr ��������ַ
	 * @param packet_number ��������
	 * @throws IOException
	 */
	public boolean receiveFile(InetAddress addr, int packet_number, int PORT) throws IOException {
		DatagramSocket ds = new DatagramSocket();
		try {
			byte[] buf = new byte[5050];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			ds.setSoTimeout(1000);
			// ����UDP��
			Vector<String> line_requested = new Vector<String>();
			for (int i = 0; i < packet_number; i++) {
				line_requested.add(Integer.toString(i));
			}
			byte[] contents = null;
			byte[][] truecontents = new byte[packet_number][];

			label1: while (!line_requested.toString().equals("[]")) {
				ds.send(Dgram.toDatagram(FileProcess.requestedLine(line_requested), addr, PORT));
				while (true) {
					try {
						ds.receive(dp);
						contents = dp.getData();
						FileProcess.parseContent(line_requested, contents, truecontents);
					} catch (SocketTimeoutException e1) {
						// e1.printStackTrace();
						continue label1;
					}
				}
			}
			System.out.println(line_requested);
			// �����ļ�
			FileProcess.storeFile(filename, truecontents);
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			ds.close();
		}
		return false;
	}
}
