import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class C {
	//對jsoup進行打包,封裝管理cookie
	private Connection conn = null;
	private Document doc = null;
	private Map<String, String> cookie = null;
	private String UserAgent = "";

	private static final int MAX_RETRY = 10;
	
	public C(String url) {
		this.conn = Jsoup.connect(url);
		this.cookie = this.conn.response().cookies();
		S s = S.getInstance();
		this.UserAgent = s.get("UserAgent");
	}
	//更改網頁URL地址
	public C url(String url) {
		this.conn.url(url);
		return this;
	}
	//以pose方法,讀取網頁
	public Document post() throws IOException {
		int retry = 0;
		while (retry < C.MAX_RETRY) {
			try {
				this.doc = this.conn.cookies(this.cookie)
						.userAgent(this.UserAgent).post();
				this.cookie.putAll(this.conn.response().cookies());
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				retry++;
				if (retry < C.MAX_RETRY) {
					throw e;
				}
			}
		}
		return this.doc;
	}
	//設置提交的數據
	public C data(String key, String value) {
		this.conn.data(key, value);
		return this;
	}
	//以get方式提交數據
	public Document get() throws IOException {
		int retry = 0;
		while (retry < C.MAX_RETRY) {
			try {
				this.doc = this.conn.cookies(this.cookie)
						.userAgent(this.UserAgent).get();
				this.cookie.putAll(this.conn.response().cookies());
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				retry++;
				if (retry < C.MAX_RETRY) {
					throw e;
				}
			}
		}
		return this.doc;
	}
	//返回網頁內容
	public Document getDoc() {
		return this.doc;
	}
	//返回cookie
	public Map<String, String> getCookie() {
		return this.cookie;
	}
	//獲取某個cookie項的值
	public String getCookie(String key) {
		return this.cookie.get(key);
	}
}
