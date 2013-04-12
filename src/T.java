import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class T {
	//下载线程管理
	private static final int maxThtead = 1;

	private static ExecutorService es = Executors
			.newFixedThreadPool(T.maxThtead);
	private static HashMap<String, Thread_Down> threadList = new HashMap<String, Thread_Down>();
	//把线程添加进下载队列
	public static void addThread(Thread_Down th) {
		T.es.submit(th);
		T.threadList.put(th.getId(), th);
	}
	//停止线程添加,并等待线程结束
	public static void shutdown() {
		T.es.shutdown();
	}
	
	public static HashMap<String, Thread_Down> getList() {
		return T.threadList;
	}
	//清理已完成线程
	public static boolean clean() {
		boolean out = false;
		Iterator<String> it = T.threadList.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Thread_Down next = T.threadList.get(key);
			if (next.isDone()) {
				it.remove();
				out = true;
			}
		}
		return out;
	}
	//获取线程大小
	public static int size() {
		T.clean();
		return T.threadList.size();
	}
	//打印线程状态
	public static void printThreadStatus() {
		Iterator<String> it = T.threadList.keySet().iterator();
		System.out.println(String.format("%s\t %s \t %s \t %s \t %s \t","Id", "Name","file size", "has download","speed"));
		while (it.hasNext()) {
			String key = it.next();
			Thread_Down next = T.threadList.get(key);
			System.out.println(String.format("%s\t %s \t %d \t %d \t %f \t",
					next.getId(), next.getName(), next.getFileSize(),
					next.getHasDown(),next.getSpeed()));
		}
		System.out.println();
	}
	
	//保存线程状态
	public static void save(){
		Iterator<String> it = T.threadList.keySet().iterator();
		Account at = Account.getInstance();
		while(it.hasNext()){
			String key = it.next();
			if(at.getList().get(key).getStatus()==ListItem.STATUS_DOWNLOADING){
				T.threadList.get(key).save();
			}
		}
	}
}
