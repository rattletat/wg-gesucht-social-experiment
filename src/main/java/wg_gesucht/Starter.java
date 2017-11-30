package wg_gesucht;

import java.io.IOException;

public class Starter {
	
	final static boolean startCityCrawler = false;
	final static boolean startOfferCrawler = true;

	public static void main(String[] args) throws IOException {

		if (startCityCrawler)
		{
			CityCrawler crawler = new CityCrawler();
	        crawler.updateCityList();
		}
		
		
		if (startOfferCrawler) 
		{
			new OfferCrawler();
		}
	}

}
