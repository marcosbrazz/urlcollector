package br.ibm.marcos.urlcollector.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;
import br.ibm.marcos.urlcollector.dto.Url;
import br.ibm.marcos.urlcollector.engine.UrlCollectorExecutor;
import br.ibm.marcos.urlcollector.exception.LinksNotFoundException;

@RestController
public class UrlCollectorService {
	
	@Autowired
	private UrlCollectorExecutor executor;
	
	@Autowired
	private UrlCollectorDao dao;
	
	@RequestMapping(method=POST, path="/collect", produces="text/plain")
	public @ResponseBody String collectUrls(@RequestParam(value="url") String urlPath, @RequestParam(value="depth", required=false) Integer depth) {
		executor.startCollector(urlPath, depth);
		return HttpStatus.OK.name();
	}
	
	@RequestMapping(method=GET,  path="/status")
	public @ResponseBody Map<String, Boolean> checkUrlProcess() {
		return executor.checkCollectors();
	}
	
	@RequestMapping(method=PUT,  path="/stop")
	public void checkUrlProcess(@RequestParam(value="url") String urlPath) {
		executor.stopExecutor(urlPath);
	}
	
	@RequestMapping(method=GET, path="/links")
	public @ResponseBody List<String> getLinks(@RequestParam(value="first", required=false) Integer first) {
		List<Url> links = dao.getLinks(first);
		if(links == null || links.isEmpty()) {
			throw new LinksNotFoundException();
		}
		return links.parallelStream()
			.map(l -> l.getUrl())
			.collect(Collectors.toList());
	}

}
