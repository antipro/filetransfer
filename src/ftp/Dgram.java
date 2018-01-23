/*
 * @author antipro
 * 创建于 2009-10-23
 * 功能类，因为编码问题，暂时没用上。
 */
package ftp;

import java.net.*;

/**
 * 2009-10-23 实现DatagramPacket与String的转换
 */
public class Dgram {
	/**
	 * 将字符串转换为DatagramPacket
	 * @param s 要转换的字符串
	 * @param destIA 目标地址
	 * @param destPort 目标端口
	 * @return UDP包
	 */
	public static DatagramPacket toDatagram(String s, InetAddress destIA, int destPort) {
		byte[] buf = new byte[s.length() + 1];
		buf = s.getBytes();
		return new DatagramPacket(buf, buf.length, destIA, destPort);
	}

	/**
	 * 将DatagramPacket转换为字符串
	 * @param p UDP包
	 * @return 字符串
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