package br.ibm.marcos.urlcollector.dao;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.Response;

import br.ibm.marcos.urlcollector.domain.Url;
import br.ibm.marcos.urlcollector.exception.UrlCollectorBadRequest;
import br.ibm.marcos.urlcollector.exception.UrlCollectorException;

@Component
public class UrlCollectorDao {

	@Autowired
	private Database db;

	public void saveUrl(List<Url> url) {
		List<Response> resps = db.bulk(url);
		for (int i = 0; i < url.size(); i++) {
			url.get(i).set_id(resps.get(i).getId());
			url.get(i).set_rev(resps.get(i).getRev());
		}
	}

	public void saveUrl(Url url) {
		Response resp = db.post(url);
		url.set_id(resp.getId());
		url.set_rev(resp.getRev());
	}

	public Url getUrl(String urlPath) {
		db.createIndex("queryByUrl", null, "json", new IndexField[] { new IndexField("url", SortOrder.asc) });

		List<Url> listUrl = db.findByIndex("{\"url\" : \"" + urlPath + "\"}", Url.class);
		if (listUrl == null || listUrl.isEmpty())
			return null;
		return listUrl.get(0);
	}

	public List<Url> getLinks() throws UrlCollectorException {
		try {
			return db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Url.class);
		} catch (IOException e) {
			throw new UrlCollectorException(e);
		}
	}
	
	public List<Url> getLinks(String baseUrlStr) throws UrlCollectorException {		
		Url baseUrl = this.getUrl(baseUrlStr);
		if(baseUrl == null) {
			throw new UrlCollectorBadRequest("Base url not found");
		}
		return this.getLinks(baseUrl);
	}


	public List<Url> getLinks(Url baseUrl) throws UrlCollectorException  {
		if(baseUrl == null) {
			throw new UrlCollectorException("baseUrl cannot be null");
		}
		db.createIndex("queryByParentId", null, "json",
				new IndexField[] { new IndexField("parentId", SortOrder.asc) });
		return db.findByIndex("{\"parentId\" : " + baseUrl.get_id() + "}", Url.class);
		
	}
}
