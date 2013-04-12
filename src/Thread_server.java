import java.util.HashMap;
import java.util.Iterator;
//后台守护进程
public class Thread_server extends XunleiThread {
	//刷新时间,默认10s,过快可能影响性能
	private static final int time = 10000;

	private int times = 0;

	public void runing() {
		while (true) {
			
			Account at = Account.getInstance();
			if (this.times++ > 6) {//每六倍的刷新时间,刷新一下迅雷离线下载列表,并保存程序状态
				at.refresh();
				at.save();
				T.save();
				this.times=0;
			}
			
			HashMap<String, ListItem> atlist = at.getList();
			Iterator<String> it = atlist.keySet().iterator();
			while (it.hasNext()) {
				//递归离线下载列表
				String key = it.next();
				ListItem next = atlist.get(key);
				if (next.getStatus() == ListItem.STATUS_READY) {
					//若有新加入的项目自动添加进下来列表
					T.addThread(new Thread_Down(next, at.getCookie()));
					next.setStatus(ListItem.STATUS_DOWNLOADING);
				}
				if(next.getStatus() == ListItem.STATUS_DOWNLOADING){
					//正在下载的项目,刷新统计下载速度
					next.speed = (next.hasdown - next.lasthasdown)/(Thread_server.time/1000);
					next.lasthasdown = next.hasdown;
				}
				
			}
			T.clean();//清理下载进程结束的列表
			T.printThreadStatus();//打印正在进行的下载进程列表
			//死循环延时
			try {
				Thread.sleep(Thread_server.time);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
}
