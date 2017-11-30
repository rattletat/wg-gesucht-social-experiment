package wg_gesucht;

public class Starter {
	
	final static boolean startCityCrawler = false;
	final static boolean startOfferCrawler = true;

	public static void main(String[] args) {

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
