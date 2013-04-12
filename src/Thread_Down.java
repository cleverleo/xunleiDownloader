import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class Thread_Down extends XunleiThread {
	//下载线程
	private String url = null;
	private String name = "";
	private String path = null;
	private static final int timeout = 5000;//连接最大等待响应时间
	private static final int BUFF = 5000000;//下载缓冲,默认5M
	private String cookie = "";
	
	
	private String id = "";
	
	private ListItem listItem = null;

	
	public Thread_Down(ListItem item, Map<String, String> cookit) {
		S s = S.getInstance();
		this.path = s.get("path") + item.getName();
		this.url = item.getURL();
		this.id = item.getId();
		this.name = item.getName();
		this.listItem = item;
		Iterator<String> it = cookit.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			this.cookie += name + "=" + cookit.get(name)
					+ (it.hasNext() ? "; " : "");
		}
	}

	public Thread_Down(String url, String path, String cookit) {
		this.path = path;
		this.url = url;
		this.cookie = cookit;
	}

	public int getFileSize() {
		return this.listItem.filesize;
	}

	private String cookit() {
		S s = S.getInstance();
		String cookie = s.get("cookie");
		if (cookie.isEmpty()) {
			return this.cookie;
		}
		return cookie;
	}

	@Override
	public void runing() {
		// TODO 自动生成的方法存根
		this.print("Create new connection to Download " + this.name);
		RandomAccessFile file = null;
		HttpURLConnection conn = null;
		try {
			URL ourl = new URL(url);
			conn = (HttpURLConnection) ourl.openConnection();
			conn.setConnectTimeout(Thread_Down.timeout);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Cookie", this.cookit());
			
			this.listItem.filesize = conn.getContentLength();
			file = new RandomAccessFile(this.path, "rw");
			file.setLength(this.listItem.filesize);//获取文件大小
			if(!this.load())this.save();//创建或加载下载文件信息
			file.seek(this.listItem.hasdown);//文件定位
			InputStream inStream = conn.getInputStream();
			inStream.skip(this.listItem.hasdown);//下载定位
			byte[] buffer = new byte[Thread_Down.BUFF];
			int hasRead = 0;
			// long time = 1;
			//下载开始
			while ((this.listItem.getStatus()==ListItem.STATUS_DOWNLOADING && this.listItem.hasdown < this.listItem.filesize)
					&& ((hasRead = inStream.read(buffer)) != -1)) {
				file.write(buffer, 0, hasRead);
				this.listItem.hasdown += hasRead;
			}
			//下载结束,检查状态
			//检查时候下载完成
			if(this.listItem.filesize==this.listItem.hasdown){
				this.listItem.setStatus(ListItem.STATUS_COMPLETE);
				this.print(this.name+" Download was complete "+this.name);
				this.delete();
			}else{
				if(this.listItem.getStatus()==ListItem.STATUS_DOWNLOADING){
					this.listItem.setStatus(ListItem.STATUS_ERROR);
					this.print("error Download "+this.name);
				}
			}
			
			if (this.listItem.getStatus()==ListItem.STATUS_DELETE) {
				File f = new File(this.path);
				f.delete();
				this.delete();
				this.print(this.name+"Download was Delete");
			}
			
			if(this.listItem.getStatus()==ListItem.STATUS_PAUSE){
				this.print(this.name+"Download was pause");
			}
			
		} catch (MalformedURLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} finally {
			try {
				file.close();
				conn.disconnect();
				Account.getInstance().save();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}

	public int getHasDown() {
		return this.listItem.hasdown;
	}


	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}


	public void save(){
		try {
			File f= new File(this.path+".info");
			if(!f.exists()){
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(this.listItem.hasdown+"\n");
			fw.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public boolean load(){
		try {
			File f= new File(this.path+".info");
			if(f.exists()){
				BufferedReader fr = new BufferedReader(new FileReader(f));
				if(fr.ready())this.listItem.hasdown=Integer.valueOf(fr.readLine());
				fr.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return false;
	}
	
	public void delete(){
		File f = new File(this.path +".info");
		if(f.exists())f.delete();
	}
	
	public float getSpeed(){
		return this.listItem.speed;
	}
}
