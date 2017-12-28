package wg_gesucht;

import java.io.IOException;

public class CrawlerLauncher {

    final static boolean startCityCrawler = false;
    final static boolean startOfferCrawler = false;
    final static boolean startOfferFilter = true;
    final static boolean startMessageWriter = true;

    public static void main(String[] args) throws IOException, InterruptedException {

        if (startCityCrawler) {
            CityCrawler crawler = new CityCrawler();
            crawler.updateAll();
        }


        if (startOfferCrawler) {
            new OfferCrawler();
        }

        if (startOfferFilter) {
            OfferFilter of = new OfferFilter(true, false, 25);

            if (startMessageWriter)
            {
            	System.out.println();
            	of.writeMsgs();
            }
        }
    }

}
