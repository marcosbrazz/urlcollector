package br.ibm.marcos.urlcollector.dto;

public class Url {
	
	private String _rev;
    private String _id;	
	private String url;
	
	public Url() {
	}
	
	public Url(String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
	
}
