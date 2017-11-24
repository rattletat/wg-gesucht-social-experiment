package wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OfferCrawler {

	public static final String filePathCities = "./rsc/cities/";
	
	
	LinkedList<String> cityUrls;
	
	
	public OfferCrawler()
	{
		cityUrls = new LinkedList<String>();
		try {
			readURLsFromFileSystem();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readURLsFromFileSystem() throws IOException
	{
		File folderCities = new File(filePathCities);
		File[] listOfFiles = folderCities.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	Document doc = Jsoup.parse(file, "UTF-8", "");
		    	Elements links = doc.getElementsByTag("link");
		    	for (Element link : links)
		    	{
		    		if (link.attr("rel").equals("canonical"))
		    		{
		    			cityUrls.add(link.attr("href"));
		    			break;
		    		}
		    	}
		    	
		    	searchOffersInFile(doc);
		    }
		}
	}
	
	public void searchOffersInFile(Document doc)
	{
		//print title
		System.out.println("-------" + doc.title() + "-------");
		
		Elements headlines = doc.getElementsByTag("h3");
		for (Element headline : headlines)
		{
			if (headline.attr("class").equals("headline headline-list-view noprint"))
			{
				//Element divcontainer =  headline.parent().parent();
				String linktext = "NONE";
				Elements links = headline.getElementsByTag("a");
				for (Element l : links) //there  should only be one link
				{
					linktext = l.text();
				}
				System.out.println(linktext);
			}
			
		}
	}
	
	
}
