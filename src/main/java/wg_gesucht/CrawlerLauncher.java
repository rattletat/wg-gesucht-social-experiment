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
            OfferFilter of = new OfferFilter(true, false, 20, 30);
            of.printFilteredDocs();

            DocSplit ds = of.randomSplitHalf();
            System.out.println();
            System.out.println("Half 1");
            OfferFilter.printDocArray(ds.d1);
            System.out.println();
            System.out.println("Half 2");
            OfferFilter.printDocArray(ds.d2);
            
            if (startMessageWriter)
            {
            	System.out.println();
            	new MessageWriter(ds);
            }
        }
    }

}
