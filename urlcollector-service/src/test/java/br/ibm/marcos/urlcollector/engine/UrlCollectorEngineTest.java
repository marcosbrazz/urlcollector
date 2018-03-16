package br.ibm.marcos.urlcollector.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ibm.marcos.urlcollector.dao.UrlCollectorDao;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class UrlCollectorEngineTest {

	@Injectable
	private UrlCollectorDao dao ;
	
	@Injectable
	private UrlDiscovery discovery;
	
	@Tested
	private UrlCollectorEngine engine;
	
	private int iterations;
	
	private int itLimit;	
	
	@Test
	public void testCollector() {
		this.iterations = 0;
		this.itLimit = 3;
		String baseUrl = "http://mussumipsum.com";
		List<String> pResult = new ArrayList<String>();
		
		new MockUp<UrlCollectorDao>() {			
			@Mock
			void saveUrl(String url) {
				pResult.add(url);
			}
			@Mock
			void saveUrl(List<String> url) {
				pResult.addAll(url);
			}
		};
		
		new MockUp<UrlDiscovery>() {
			@SuppressWarnings("unchecked")
			@Mock
			Set<String> discoverUrl(String url) {
				if(iterations < itLimit) {
					iterations ++;
					return new HashSet<String>(getSavedUrls());
				}
				return Collections.EMPTY_SET;
			}
		};
				
		
		engine.setBaseUrlStr(baseUrl);
		engine.setDepth(3);
		engine.run();
		
		Assert.assertEquals(10, pResult.size());
		Assert.assertTrue(pResult.contains("http://mussumipsum.com"));
		Assert.assertTrue(pResult.contains("http://g1.globo.com/al/alagoas/noticia/2013/12/diario-oficial-de-al-surpreende-ao-publicar-texto-que-remete-ao-mussum.html"));
		Assert.assertTrue(pResult.contains("http://github.com/diegofelipece/"));
		
	}
	
	private List<String> getSavedUrls() {
			return Arrays.asList(
					"http://mussumipsum.com/assets/favicons/apple-icon-76x76.png",
					"http://github.com/diegofelipece/",
					"http://g1.globo.com/al/alagoas/noticia/2013/12/diario-oficial-de-al-surpreende-ao-publicar-texto-que-remete-ao-mussum.html"
			);
	}

}
