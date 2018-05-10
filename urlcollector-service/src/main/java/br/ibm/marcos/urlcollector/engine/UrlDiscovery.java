package br.ibm.marcos.urlcollector.engine;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlDiscovery {
	
	private final Logger logger = LoggerFactory.getLogger(UrlDiscovery.class);
	
	@SuppressWarnings("unchecked")
	public Set<String> discoverUrl(String baseUrl) {
		try {
			Document doc = Jsoup.connect(baseUrl).get();
			Elements links = doc.select("a[href]");
			Elements imports = doc.select("link[href]");
			links.addAll(imports);		
			Set<String> childUrls = links.parallelStream().map(link -> {
				String child = link.absUrl("href");
				logger.debug(" - > " + child);
				return child;
			}).collect(Collectors.toSet());
			return childUrls;
		} catch (IOException e) {
			logger.debug("Not a document: " + baseUrl);
			return Collections.EMPTY_SET;
		}
	}

}
