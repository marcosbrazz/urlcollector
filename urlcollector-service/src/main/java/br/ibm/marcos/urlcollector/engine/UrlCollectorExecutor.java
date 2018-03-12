package br.ibm.marcos.urlcollector.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UrlCollectorExecutor {
	
	@Autowired
	private BeanFactory beanFactory;
	
	private Map<String, Future<?>> futures;
	private Map<String, UrlCollectorEngine> runnables;
	
	private static final ExecutorService threadpool = Executors.newCachedThreadPool();
	
	public UrlCollectorExecutor() {
		this.futures = new HashMap<String, Future<?>>();
		this.runnables = new HashMap<String, UrlCollectorEngine>();
	}

	public void startCollector(String url, Integer depth) {
		UrlCollectorEngine engine = beanFactory.getBean(UrlCollectorEngine.class);
		engine.setBaseUrlStr(url);
		engine.setDepth(depth);
		Future<?> future = threadpool.submit(engine);
		this.futures.put(url, future);
		this.runnables.put(url, engine);
	}
	
	public Map<String, Boolean> checkCollectors() {
		Map<String, Boolean> situation = new HashMap<String, Boolean>();
		this.futures.forEach((url, future) -> {
			situation.put(url, future.isDone());
		});
		return situation;
	}
	
	public void stopExecutor(String url) {
		UrlCollectorEngine runnable = this.runnables.get(url);
		if(runnable != null)  {
			runnable.stop();
		}
		
	}

}
