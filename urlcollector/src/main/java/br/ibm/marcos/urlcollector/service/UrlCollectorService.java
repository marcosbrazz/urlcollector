package br.ibm.marcos.urlcollector.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;
import br.ibm.marcos.urlcollector.dto.Url;
import br.ibm.marcos.urlcollector.engine.UrlCollectorEngine;
import br.ibm.marcos.urlcollector.exception.LinksNotFoundException;

@RestController
public class UrlCollectorService {
	
	@Autowired
	private UrlCollectorEngine engine;
	
	@Autowired
	private UrlCollectorDao dao;
	
	@RequestMapping(method=PUT, path="/collect")
	public @ResponseBody String collectUrls(@RequestParam(value="url") String urlPath) {
		engine.collect(urlPath);
		return HttpStatus.OK.name();
	}
	
	@RequestMapping(method=GET, path="/links")
	public @ResponseBody List<String> getLinks() {
		List<Url> links = dao.getLinks();
		if(links == null || links.isEmpty()) {
			throw new LinksNotFoundException();
		}
		return links.parallelStream()
			.map(l -> l.getUrl())
			.collect(Collectors.toList());
	}

}
