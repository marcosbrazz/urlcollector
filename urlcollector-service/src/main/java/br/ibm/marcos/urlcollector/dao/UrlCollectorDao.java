package br.ibm.marcos.urlcollector.dao;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.Response;

import br.ibm.marcos.urlcollector.dto.Url;
import br.ibm.marcos.urlcollector.exception.UrlCollectorBadRequest;
import br.ibm.marcos.urlcollector.exception.UrlCollectorException;

@Component
public class UrlCollectorDao {

	@Autowired
	private Database db;

	public void saveUrl(List<String> urlPaths) {
		List<Url> url = urlPaths.stream()
			.map(path -> new Url(path))
			.collect(Collectors.toList());
		List<Response> resps = db.bulk(url);
		assertOk("saveUrl", resps.toArray(new Response[resps.size()]));
	}
	

	public void saveUrl(String url) {		
		Response resp = db.post(new Url(url));
		assertOk("saveUrl", resp);
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
	
//	public List<Url> getLinks(String baseUrlStr) throws UrlCollectorException {		
//		if(baseUrl == null) {
//			throw new UrlCollectorException("baseUrl cannot be null");
//		}		
//		db.createIndex("queryByParentId", null, "json",
//				new IndexField[] { new IndexField("parentId", SortOrder.asc) });
//		return db.findByIndex("{\"parentId\" : \"" + baseUrl.get_id() + "\"}", Url.class);
//	}

	
	private void assertOk(String method, Response... resp) {
		Stream<Response> rStream = Arrays.stream(resp);
		Predicate<Response> p =  r -> HttpStatus.OK.value() != r.getStatusCode();
		if(rStream.anyMatch(p)) {
			String error = rStream.filter(p)
					.map(r -> String.format("ERR_CODE: %s - ERROR: %s - REASON: %s", r.getStatusCode(), r.getError(), r.getReason()))
					.collect(Collectors.joining("\n"));
			//TODO LOG Error
			throw new UrlCollectorException(error);
		}
	}
}
