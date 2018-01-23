/*
 * @author antipro
 * ������ 2009-10-23
 * �����࣬��Ϊ�������⣬��ʱû���ϡ�
 */
package ftp;

import java.net.*;

/**
 * 2009-10-23 ʵ��DatagramPacket��String��ת��
 */
public class Dgram {
	/**
	 * ���ַ���ת��ΪDatagramPacket
	 * @param s Ҫת�����ַ���
	 * @param destIA Ŀ���ַ
	 * @param destPort Ŀ��˿�
	 * @return UDP��
	 */
	public static DatagramPacket toDatagram(String s, InetAddress destIA, int destPort) {
		byte[] buf = new byte[s.length() + 1];
		buf = s.getBytes();
		return new DatagramPacket(buf, buf.length, destIA, destPort);
	}

	/**
	 * ��DatagramPacketת��Ϊ�ַ���
	 * @param p UDP��
	 * @return �ַ���
	 */
	public static String toString(DatagramPacket p) {
		return new String(p.getData(), 0, p.getLength());
	}

	public static byte[] toBytes(DatagramPacket p) {
		return p.getData();
	}

	public static DatagramPacket toDatagram(byte[] buf, InetAddress destIA, int destPort) {
		byte[] buf2 = new byte[buf.length + 1];
		System.arraycopy(buf, 0, buf2, 0, buf.length);
		return new DatagramPacket(buf2, buf2.length, destIA, destPort);
	}
}
