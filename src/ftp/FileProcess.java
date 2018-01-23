/*
 * @author antipro
 * 创建于 2009-11-3
 * 相关的文件处理类
 * 
 */
package ftp;

import java.io.*;
import java.util.Vector;

/**
 * 2009-11-3 文件处理类 一个文件行是指一个包含文件某段内容的字节数组，形式一般为<行号:字节数?实际内容>， 不包括尖括号
 */
public final class FileProcess {
	/**
	 * 将接受到的内容contents按照自带的行号添加进二维数组filebuf，并将仍要求发送的文件 行的状况以line_requested方式返回
	 * @param line_requested 仍然要求发送的行的向量
	 * @param contents 接受到的解析前的字节数组
	 * @param truecontents 解析后的二维数组 说明：contents的格式 行号:字节数?实际内容 例如：
	 *          25:5000:xxxxxxx...xxxxxx
	 */
	public static void parseContent(Vector<String> line_requested, byte[] contents, byte[][] filebuf) {
		// 获得行号
		int index_length = search(contents, (byte) ':');
		byte[] index_byte = new byte[index_length];
		System.arraycopy(contents, 0, index_byte, 0, index_length);
		int index = Integer.parseInt(new String(index_byte));
		// 获得内容长度
		int number_length = search(contents, (byte) '?') - index_length - 1;
		byte[] number_byte = new byte[number_length];
		System.arraycopy(contents, index_length + 1, number_byte, 0, number_length);
		int line_length = Integer.parseInt(new String(number_byte));
		// System.out.println(line_length);
		// 获得真正的内容
		filebuf[index] = new byte[line_length];
		System.arraycopy(contents, search(contents, (byte) '?') + 1, filebuf[index], 0, line_length);
		// 删除这一行
		line_requested.remove(Integer.toString(index));
	}

	/**
	 * 将文件打开并解析为二维数组
	 * @param file 要打开的文件
	 * @return 包含文件内容的二维向量格式为 行号:字节数?内容
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[][] openFile(File file) throws IOException {
		String head = null;
		byte[] buf = new byte[5000];
		FileInputStream infile = new FileInputStream(file);
		int packet_number = packetNumber(file);
		byte[][] filebuf = new byte[packet_number][];
		int leave = (int) file.length() % 5000;
		for (int i = 0; i < packet_number - 1; i++) {
			infile.read(buf, 0, 5000);
			head = new String(i + ":" + "5000" + "?");
			filebuf[i] = new byte[head.getBytes().length + 5000];
			System.arraycopy(head.getBytes(), 0, filebuf[i], 0, head.getBytes().length);
			System.arraycopy(buf, 0, filebuf[i], head.getBytes().length, 5000);
		}
		infile.read(buf, 0, leave);
		head = new String((packet_number - 1) + ":" + Integer.toString(leave) + "?");
		filebuf[packet_number - 1] = new byte[head.getBytes().length + leave];
		System.arraycopy(head.getBytes(), 0, filebuf[packet_number - 1], 0, head.getBytes().length);
		System.arraycopy(buf, 0, filebuf[packet_number - 1], head.getBytes().length, leave);
		infile.close();
		return filebuf;
	}

	/**
	 * 将收到的二维数组重新保存为文件
	 * @param filename 要保存的文件名
	 * @param filebuf 包含文件内容的字符串数组
	 * @throws IOException
	 */
	public static void storeFile(String filename, byte[][] filebuf) throws IOException {
		FileOutputStream outfile = new FileOutputStream(filename);
		for (int i = 0; i < filebuf.length; i++) {
			outfile.write(filebuf[i]);
		}
		outfile.close();
	}

	/**
	 * 返回待传输文件的包的数量
	 * @param file 要计算包数量的文件
	 * @return 包的数量
	 */
	public static int packetNumber(File file) {
		// 获取文件长度
		long file_length = file.length();
		// 设定包的数量
		int packet_number = 0;
		if (file_length % 5000 != 0)
			packet_number = (int) (file_length / 5000 + 1);
		else
			packet_number = (int) (file_length / 5000);
		return packet_number;
	}

	/**
	 * 将已经接受的行的向量转为字符串
	 * @param line_requested 已经接收的行的数组
	 * @return
	 */
	public static String requestedLine(Vector<String> line_requested) {
		// 该字符串的表述形式大致为[1:32:23]这一类
		String request_line = line_requested.toString();
		if (request_line != "[]")
			// 去掉两头的方括号
			return request_line.substring(1, request_line.length() - 1);
		return request_line;
	}

	/**
	 * 搜索文件行中代表该行字节数量或者行号的数子
	 * @param buf 待搜索的行
	 * @param key 文件行中的分隔字符，这里是:或者?
	 * @return 返回该关键字在行中的index
	 **/
	public static int search(byte[] buf, byte key) {
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == key)
				return i;
		}
		return -1;
	}
}
