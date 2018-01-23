/*
 * @author antipro
 * ������ 2009-11-3
 * ��ص��ļ�������
 * 
 */
package ftp;

import java.io.*;
import java.util.Vector;

/**
 * 2009-11-3 �ļ������� һ���ļ�����ָһ�������ļ�ĳ�����ݵ��ֽ����飬��ʽһ��Ϊ<�к�:�ֽ���?ʵ������>�� ������������
 */
public final class FileProcess {
	/**
	 * �����ܵ�������contents�����Դ����к���ӽ���ά����filebuf��������Ҫ���͵��ļ� �е�״����line_requested��ʽ����
	 * @param line_requested ��ȻҪ���͵��е�����
	 * @param contents ���ܵ��Ľ���ǰ���ֽ�����
	 * @param truecontents ������Ķ�ά���� ˵����contents�ĸ�ʽ �к�:�ֽ���?ʵ������ ���磺
	 *          25:5000:xxxxxxx...xxxxxx
	 */
	public static void parseContent(Vector<String> line_requested, byte[] contents, byte[][] filebuf) {
		// ����к�
		int index_length = search(contents, (byte) ':');
		byte[] index_byte = new byte[index_length];
		System.arraycopy(contents, 0, index_byte, 0, index_length);
		int index = Integer.parseInt(new String(index_byte));
		// ������ݳ���
		int number_length = search(contents, (byte) '?') - index_length - 1;
		byte[] number_byte = new byte[number_length];
		System.arraycopy(contents, index_length + 1, number_byte, 0, number_length);
		int line_length = Integer.parseInt(new String(number_byte));
		// System.out.println(line_length);
		// �������������
		filebuf[index] = new byte[line_length];
		System.arraycopy(contents, search(contents, (byte) '?') + 1, filebuf[index], 0, line_length);
		// ɾ����һ��
		line_requested.remove(Integer.toString(index));
	}

	/**
	 * ���ļ��򿪲�����Ϊ��ά����
	 * @param file Ҫ�򿪵��ļ�
	 * @return �����ļ����ݵĶ�ά������ʽΪ �к�:�ֽ���?����
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
	 * ���յ��Ķ�ά�������±���Ϊ�ļ�
	 * @param filename Ҫ������ļ���
	 * @param filebuf �����ļ����ݵ��ַ�������
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
	 * ���ش������ļ��İ�������
	 * @param file Ҫ������������ļ�
	 * @return ��������
	 */
	public static int packetNumber(File file) {
		// ��ȡ�ļ�����
		long file_length = file.length();
		// �趨��������
		int packet_number = 0;
		if (file_length % 5000 != 0)
			packet_number = (int) (file_length / 5000 + 1);
		else
			packet_number = (int) (file_length / 5000);
		return packet_number;
	}

	/**
	 * ���Ѿ����ܵ��е�����תΪ�ַ���
	 * @param line_requested �Ѿ����յ��е�����
	 * @return
	 */
	public static String requestedLine(Vector<String> line_requested) {
		// ���ַ����ı�����ʽ����Ϊ[1:32:23]��һ��
		String request_line = line_requested.toString();
		if (request_line != "[]")
			// ȥ����ͷ�ķ�����
			return request_line.substring(1, request_line.length() - 1);
		return request_line;
	}

	/**
	 * �����ļ����д�������ֽ����������кŵ�����
	 * @param buf ����������
	 * @param key �ļ����еķָ��ַ���������:����?
	 * @return ���ظùؼ��������е�index
	 **/
	public static int search(byte[] buf, byte key) {
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == key)
				return i;
		}
		return -1;
	}
}
