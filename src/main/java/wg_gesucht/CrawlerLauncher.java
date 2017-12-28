package wg_gesucht;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CrawlerLauncher {

    final static boolean startCityCrawler = false;
    final static boolean startOfferCrawler = false;
    final static boolean startOfferFilter = true;
    final static boolean startMessageWriter = true;

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	//load personas
    	/*
    	Properties[] personas = new Properties[2];
    	FileReader reader = new FileReader(MessageWriter.filePathPersona1);
		personas[0] = new Properties();
		personas[0].load(reader);
		
		reader = new FileReader(MessageWriter.filePathPersona2);
		personas[1] = new Properties();
		personas[1].load(reader);
		*/
    	
    	//load persona
    	FileReader reader = new FileReader(MessageWriter.filePathPersona1);
		Properties persona = new Properties();
		persona.load(reader);
    	
		reader.close();

        if (startCityCrawler) {
            CityCrawler crawler = new CityCrawler();
            crawler.updateAll();
        }


        if (startOfferCrawler) {
            new OfferCrawler();
        }

        if (startOfferFilter) {
        	boolean male;
        	if (persona.getProperty("gender").equals("m")) male = true;
        	else male = false;
        	int age = Integer.parseInt(persona.getProperty("age"));
            OfferFilter of = new OfferFilter(male, !male, age);

            if (startMessageWriter)
            {
            	System.out.println();
            	of.writeMsgs();
            }
        }
    }

}
