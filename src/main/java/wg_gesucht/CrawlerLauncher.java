package main.java.wg_gesucht;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class CrawlerLauncher {

    final static boolean startCityCrawler = false;
    final static boolean startOfferCrawler = true;
    final static boolean startOfferFilter = true;
    final static boolean startMessageWriter = true;
    final static boolean startFormFiller = false;

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

        ArrayList<Integer> cities = new ArrayList<Integer>();
        cities.add(1);
        //load persona
        FileReader reader = new FileReader(MessageWriter.filePathPersona1);
        Properties persona = new Properties();
        persona.load(reader);

        reader.close();

        // Fetch city HTML pages
        if (startCityCrawler) {
            System.out.println("[MODULE] CityCrawler activated.");
            CityCrawler crawler = new CityCrawler();
            crawler.updateCityList(cities);//TODO: Only specific cities
            System.out.println("[MODULE] CityCrawler deactivated.");
        }


        // Extract offers
        if (startOfferCrawler) {
            System.out.println("[MODULE] OfferCrawler activated.");
            new OfferCrawler();
            System.out.println("[MODULE] OfferCrawler deactivated.");
        }

        // Filter offers
        OfferFilter of = null;
        if (startOfferFilter) {
            System.out.println("[MODULE] OfferFilter activated.");
            boolean male;
            if (persona.getProperty("gender").equals("m")) male = true;
            else male = false;
            int age = Integer.parseInt(persona.getProperty("age"));
            of = new OfferFilter(male, !male, age);
            System.out.println("[MODULE] OfferFilter deactivated.");
        }

        // Write appropiate messages
        if (startMessageWriter && of != null) {
            System.out.println("[MODULE] MessageWriter activated.");
            of.writeMsgs();
            System.out.println("[MODULE] MessageWriter deactivated.");
        }


        // Send messages
        //if (startFormFiller) {
        //   System.out.println("[MODULE] FormFiller activated.");
        //  FormFiller filler = new FormFiller();
        // String[] string = {"Max", "Mustermann", "Lustig", "Hehe"};
        //filler.fill_data("https://www.wg-gesucht.de/nachricht-senden.html?message_ad_id=6476597", string);
        // System.out.println("[MODULE] FormFiller deactivated.");
        //}
    }
}
