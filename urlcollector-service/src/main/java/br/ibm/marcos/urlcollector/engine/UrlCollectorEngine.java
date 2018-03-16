package br.ibm.marcos.urlcollector.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;


@Component
public class UrlCollectorEngine implements Runnable {
	
	@Autowired
	private UrlCollectorDao dao;
	
	@Autowired
	private UrlDiscovery discovery;
	
	private final Logger logger = LoggerFactory.getLogger(UrlCollectorEngine.class);
	
	private Stack<String> urlStack;
	private Set<String> urlIndex;
	private String baseUrlStr;
	private Integer depthLimit;
	private Stack<Integer> linksByDepth;
	private Boolean stopSignal;

	public UrlCollectorEngine() {
		this.urlStack = new Stack<String>();
		this.urlIndex = new HashSet<String>();
		this.linksByDepth = new Stack<Integer>();
		this.stopSignal = false;
	}
	
	private void collect() {
		Validate.notEmpty(this.baseUrlStr, "A URL must be supplied");
		this.pushUrls(Arrays.asList(this.baseUrlStr));
		while (!urlStack.empty() && !stopSignal) {
			String baseUrl = this.popUrl();
			logger.debug(baseUrl);
			Set<String> childUrls = discovery.discoverUrl(baseUrlStr);
			this.pushUrls(childUrls);			
		}
		
	}
	
	public void stop() {
		logger.info("Collector stop issued for " + this.baseUrlStr);
		this.stopSignal = true;
	}
	
	private Integer currentDepth() {
		return this.linksByDepth.size();
	}
	
	
	/**
	 * This method prevents duplicate the mining of URLs and control mining depth limit 
	 * 
	 * @param urls
	 */
	private void pushUrls(Collection<String> urls) {
		List<String> uniqueUrls = urls.stream()
			.filter(url -> !this.urlIndex.contains(url))
			.collect(Collectors.toList());
		if(uniqueUrls.size() > 0 && (this.depthLimit == null || this.currentDepth() < this.depthLimit)) {
			this.urlStack.addAll(uniqueUrls);
			this.linksByDepth.add(uniqueUrls.size());
			this.dao.saveUrl(uniqueUrls);			
		}
	}
	
	private String popUrl() {
		if(this.depthLimit != null) {
			Integer levelLinks = this.linksByDepth.pop();
			levelLinks--;
			if(levelLinks > 0) {
				this.linksByDepth.push(levelLinks);
			}
		}
		return this.urlStack.pop();
	}



	public String getBaseUrlStr() {
		return baseUrlStr;
	}


	public void setBaseUrlStr(String baseUrlStr) {
		this.baseUrlStr = baseUrlStr;
	}


	public Integer getDepth() {
		return depthLimit;
	}


	public void setDepth(Integer depth) {
		this.depthLimit = depth;
	}

	@Override
	public void run() {
		logger.info("Started collector process for " + this.baseUrlStr);
		this.collect();
		logger.info("Finished collector process for " + this.baseUrlStr);
	}
	

}
