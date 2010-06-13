package sg.macbuntu.whs;


public class HeritageSite {
	
	String name;
	String url;
	String geo;
	
	public HeritageSite(String name, String url, String geo) {
		this.name = name;
		this.url = url;
		this.geo = geo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	

}
