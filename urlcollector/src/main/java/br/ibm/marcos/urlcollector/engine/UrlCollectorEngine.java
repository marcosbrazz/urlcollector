package br.ibm.marcos.urlcollector.engine;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;
import br.ibm.marcos.urlcollector.domain.Url;


@Component
public class UrlCollectorEngine {
	
	@Autowired
	private UrlCollectorDao dao;
	
	public Url collect(String baseUrlStr) {
		Validate.notEmpty(baseUrlStr, "A URL must be supplied");
		Stack<Url> urlStack = new Stack<Url>();
		Url url = new Url(baseUrlStr);
		urlStack.push(url);
		dao.saveUrl(url);
		while (!urlStack.empty()) {
			Url baseUrl = urlStack.pop();
			System.out.println(baseUrl.getUrl());
			Document doc;
			Elements links;
			Elements imports;
			try {
				doc = Jsoup.connect(baseUrl.getUrl()).get();
				links = doc.select("a[href]");
				imports = doc.select("link[href]");
			} catch (IllegalArgumentException | IOException e) {
				// TODO logar !
				System.err.println("Not a document: " + baseUrl.getUrl());
				links = new Elements();
				imports = new Elements();
			}
			links.addAll(imports);
			List<Url> childUrls = links.parallelStream().map(link -> {
				Url child = new Url(link.absUrl("href"));
				child.setParentId(baseUrl.get_id());
				System.out.println(" - > " + child.getUrl()); // TODO logar
				urlStack.push(child);
				return child;
			}).collect(Collectors.toList());
			dao.saveUrl(childUrls);
			
		}
		return url;
		
	}
	

}
