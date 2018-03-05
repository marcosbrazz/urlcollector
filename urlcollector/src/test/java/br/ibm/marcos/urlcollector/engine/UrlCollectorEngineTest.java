package br.ibm.marcos.urlcollector.engine;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import br.ibm.marcos.urlcollector.domain.Url;

public class UrlCollectorEngineTest {

//	@Test
//	public void testCollect() throws Exception {
//		String url = "http://mussumipsum.com/";
//		Url urlTree = UrlCollectorEngine.collect(url);
//		Assert.assertNotNull(urlTree);
//		Assert.assertNotNull(urlTree.getReferences());
//		Assert.assertEquals(9, urlTree.getReferences().size());
//		
//		urlTree.getReferences().stream().forEach(
//				childUrl -> System.out.println(childUrl.getUrl())
//		);
//	}
//	
//	@Test
//	public void printCollectionRunning() throws Exception {
//		String url = "http://mussumipsum.com/";
//		UrlCollectorEngine.collect(url);
//	}
	
//	@Test
	public void testRelativeLink() throws IOException {
		String base = "https://www.google.com.br/intl/en/policies/privacy/";
		Document doc = Jsoup.connect(base).get();
		Elements links = doc.select("a[href]");
		Elements imports = doc.select("link[href]");
//		links.addAll(imports);
		links.stream().forEach(
			l -> {
				Element l2 = l;
				System.out.println(l.absUrl("href"));
			
		});
	}
	
//	@Test
//	public void testChildrem()  throws Exception {
//		String url = "http://mussumipsum.com/";
//		Document doc = Jsoup.connect(url).get();
//		Elements links = doc.select("a[href]");
//		Elements imports = doc.select("link[href]");
//		List<Url> linkList = links.parallelStream().map(
//				link -> 	{
//					Url url2 = new Url(link.attr("href"));
//					System.out.println(link.children().size());
//					List<Url> url2childrem = link.children().parallelStream()
//						.filter(	l3 -> l3.hasAttr("href")	)
//						.map(l3 -> new Url(l3.attr("href")))
//						.collect(Collectors.toList());
//					url2.setReferences(url2childrem);
//					return url2;
//				}
//		).collect(Collectors.toList());
//		
////		linkList.stream()
////			.forEach(link -> {
////				System.out.println(link.getUrl() + " " + link.getReferences().size());
////			});
//	}
}
