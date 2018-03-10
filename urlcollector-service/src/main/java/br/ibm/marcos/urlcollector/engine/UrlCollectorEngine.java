package br.ibm.marcos.urlcollector.engine;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;


@Component
public class UrlCollectorEngine {
	
	@Autowired
	private UrlCollectorDao dao;
	
	private Stack<String> urlStack;
	private Set<String> urlIndex;
	
	private void init() {
		this.urlStack = new Stack<String>();
		this.urlIndex = new HashSet<String>();
	}
	

	public void collect(String baseUrlStr) {
		this.init();
		Validate.notEmpty(baseUrlStr, "A URL must be supplied");
		this.pushUrl(baseUrlStr);
		dao.saveUrl(baseUrlStr);
		while (!urlStack.empty()) {
			String baseUrl = this.popUrl();
			System.out.println(baseUrl);
			Document doc;
			Elements links;
			Elements imports;
			try {
				doc = Jsoup.connect(baseUrl).get();
				links = doc.select("a[href]");
				imports = doc.select("link[href]");
			} catch (IllegalArgumentException | IOException e) {
				// TODO logar !
				System.err.println("Not a document: " + baseUrl);
				links = new Elements();
				imports = new Elements();
			}
			links.addAll(imports);
			List<String> childUrls = links.parallelStream().map(link -> {
				String child = link.absUrl("href");
				System.out.println(" - > " + child); // TODO logar
				this.pushUrl(child);
				return child;
			}).collect(Collectors.toList());
			dao.saveUrl(childUrls);
			
		}
		
	}
	
	/**
	 * This method prevents duplicate the mining of URLs 
	 * 
	 * @param url
	 */
	private void pushUrl(String url) {
		if(!this.urlIndex.contains(url)) {
			urlStack.push(url);
			urlIndex.add(url);
		}
	}
	
	private String popUrl() {
		return this.urlStack.pop();
	}
	

}
