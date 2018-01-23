package ftp;

/**
 * 协议指令集
 * @author antipro
 */
public class Instructions {
	
	// 服务端指令集

	/**
	 * 服务器准备就绪
	 */
	public static final String READY = "[INSTRUCTION] Ready";
	/**
	 * 要求提交文件名
	 */
	public static final String REQUEST_FILE_NAME = "[INSTRUCTION] Request file name";
	/**
	 * 文件存在开始传输
	 */
	public static final String FILE_EXISTED = "[INSTRUCTION] File existed";
	/**
	 * 文件不存在要求重新提交文件名
	 */
	public static final String NO_SUCH_FILE = "[INSTRUCTION] No such file";
	

	// 客户端指令集
	
	/**
	 * 请求列出目录下全部文件
	 */
	public static final String REQUEST_FILE_LIST = "[INSTRUCTION] Request file list";
	/**
	 * 提交文件名称
	 */
	public static final String POST_FILE_NAME = "[INSTRUCTION] Post file name";
	/**
	 * 文件传输完毕要求关闭链接
	 */
	public static final String DONE = "[INSTRUCTION] Done";

}