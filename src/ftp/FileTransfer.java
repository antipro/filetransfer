/*
 * @author antipro
 * 创建于 2009-10-24
 * 文件传输类
 */
package ftp;

import java.net.*;
import java.util.Vector;
import java.io.*;

/**
 * 2009-10-24 文件传输方法
 */
public final class FileTransfer {
	public int PORT;
	public boolean unsended = true; // 设置未发送标志
	public String filename;
	public DatagramSocket ds; // 服务器专用DatagramSocket

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
	 * 用UDP方式传输文件
	 * @param file 待传输的文件（包括相对路径）
	 * @return 传输成功返回true，失败返回false。
	 * @throws IOException
	 */
	public void sendFile(File file) throws IOException {
		byte[] buf = new byte[5050];
		try {
			System.out.println("Start Send: " + file);
			DatagramPacket dp = new DatagramPacket(buf, buf.length); // 预留50字节的空间放置序号
			ds.setSoTimeout(5000); // 五秒内无信号重新发送
			// 生成包含全文内容的向量
			byte[][] filebuf = FileProcess.openFile(file);
			while (unsended) {
				// 接受状态信息
				ds.receive(dp);
				String[] request_lines = Dgram.toString(dp).split(",");
				// System.out.println(Dgram.toString(dp));
				for (int i = 0; i < request_lines.length; i++) {
					// 将需要发送的包发送出去
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
	 * 用UDP方式接收文件
	 * @param LocalPORT 准备的本地端口
	 * @param addr 服务器地址
	 * @param packet_number 包的数量
	 * @throws IOException
	 */
	public boolean receiveFile(InetAddress addr, int packet_number, int PORT) throws IOException {
		DatagramSocket ds = new DatagramSocket();
		try {
			byte[] buf = new byte[5050];
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			ds.setSoTimeout(1000);
			// 接受UDP包
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
			// 保存文件
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
