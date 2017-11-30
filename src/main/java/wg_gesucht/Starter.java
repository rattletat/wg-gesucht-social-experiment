package wg_gesucht;

import java.io.IOException;

public class Starter {
	
	final static boolean startCityCrawler = false;
	final static boolean startOfferCrawler = false;
	final static boolean startOfferFilter = true;

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
		
		if (startOfferFilter) {
			OfferFilter of = new OfferFilter(true, false, 20, 30);
			of.printFilteredDocs();
		}
	}

}
