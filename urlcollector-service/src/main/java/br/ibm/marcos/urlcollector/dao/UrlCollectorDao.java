package br.ibm.marcos.urlcollector.dao;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.AllDocsRequestBuilder;

import br.ibm.marcos.urlcollector.dto.Url;
import br.ibm.marcos.urlcollector.exception.UrlCollectorException;

@Component
public class UrlCollectorDao {

	@Autowired
	private Database db;
	
	private final Logger logger = LoggerFactory.getLogger(UrlCollectorDao.class);

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

	public List<Url> getLinks(Integer first) throws UrlCollectorException {
		try {
			AllDocsRequestBuilder builder = db.getAllDocsRequestBuilder().includeDocs(true);
			if(first != null) {
				builder.limit(first);
			}
			return builder.build().getResponse().getDocsAs(Url.class);
		} catch (IOException e) {
			throw new UrlCollectorException(e);
		}
	}
	
	
	private void assertOk(String method, Response... resp) {
		Stream<Response> rStream = Arrays.stream(resp);
		Predicate<Response> p =  r -> OK.value() != r.getStatusCode() && CREATED.value() != r.getStatusCode();
		if(rStream.anyMatch(p)) {
			String error = rStream.filter(p)
					.map(r -> String.format("ERR_CODE: %s - ERROR: %s - REASON: %s", r.getStatusCode(), r.getError(), r.getReason()))
					.collect(Collectors.joining("\n"));
			logger.warn(error);
			throw new UrlCollectorException(error);
		}
	}
}
