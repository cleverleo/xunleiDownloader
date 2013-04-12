

public class xunlei {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Account at = Account.getInstance();
		//登陆
		at.login();
		//打印迅雷离线项目列表
		at.printList();
		//启动后台守护进程
		new Thread(new Thread_server()).start();
	}

}
