import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class S {
	//储存和加载与程序有關設置的類
	private static S _object = new S();
	private static final String FILENAME = "setting.ini";
	private HashMap<String, String> list = new HashMap<String, String>();

	private S() {
		if (!this.load()) {
			this.initList();
			this.save();
		}
	}

	public static S getInstance() {
		return S._object;
	}
	//读取
	public String get(String key) {
		return this.list.get(key);
	}
	//添加或更改
	public void set(String key, String value) {
		this.list.put(key, value);
	}
	//保存
	public void save() {
		try {
			File f = new File("./" + S.FILENAME);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			Iterator<String> it = this.list.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = this.list.get(key);
				fw.write(String.format("%s=%s\n", key, value));
			}
			fw.close();
			System.out.println("Create a new Setting on ./" + S.FILENAME);
			System.exit(0);
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	//初始化
	public void initList() {
		this.list.put("cookie", "");
		this.list.put("path", "./");
		this.list.put("user", "");
		this.list.put("password", "");
		this.list.put("UserAgent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11");
	}
	//读取文件加载
	public boolean load() {
		try {
			File f = new File("./" + S.FILENAME);
			if (f.exists()) {
				BufferedReader fr = new BufferedReader(new FileReader(f));
				while (fr.ready()) {
					String in = fr.readLine();
					if (in.isEmpty() || in.startsWith("#")) {
						continue;
					} else {
						String[] in_a = in.split("=", 2);
						this.list.put(in_a[0].trim(), in_a[1].trim());
					}
				}
				fr.close();
				if (!(this.list.containsKey("user")
						&& this.list.containsKey("password")
						&& this.list.containsKey("cookie")
						&& !this.list.get("user").isEmpty()
						&& !this.list.get("password").isEmpty()
						&& !this.list.get("cookie").isEmpty())) {
					System.out.println("Setting error,please check the ./"
							+ S.FILENAME + " file!!");
					System.exit(0);
				}
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
	//打印设置列表
	public void printSetting() {
		Iterator<String> it = this.list.keySet().iterator();
		System.out.println("Setting list");
		while (it.hasNext()) {
			String key = it.next();
			String value = this.list.get(key);
			System.out.println(String.format("%s = %s", key, value));
		}
	}

}
