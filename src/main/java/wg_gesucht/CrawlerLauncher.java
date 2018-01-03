package main.java.wg_gesucht;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

public class CrawlerLauncher {

    final static boolean START_CITY_CRAWLER = true;
    final static boolean START_OFFER_CRAWLER = true;
    final static boolean START_OFFER_FILTER = true;
    final static boolean START_MESSAGE_WRITER = true;
    final static boolean START_MESSAGE_SENDER = false;

    public static void main(String[] args) {
        // Setup cities
        ArrayList<Integer> cities = new ArrayList<Integer>();
        cities.add(1);
        cities.add(2); //...

        // Setup persona path
        String persona_path = "./rsc/personas/persona1.properties";

        // Load personas
        Properties persona = new Properties();
        try {
            FileReader reader = new FileReader(persona_path);
            persona.load(reader);
            reader.close();
        } catch (Exception e) {
            System.out.println("[ERROR] Could not load persona file. Check the file path.");
            System.exit(1);
        }

        // Fetch city HTML pages
        if (START_CITY_CRAWLER) {
            System.out.println("[MODULE] CityCrawler activated.");
            CityCrawler crawler = new CityCrawler();
            crawler.updateCityList(cities);
            System.out.println("[MODULE] CityCrawler deactivated.");
        }

        // Extract offers
        if (START_OFFER_CRAWLER) {
            System.out.println("[MODULE] OfferCrawler activated.");
            new OfferCrawler();
            System.out.println("[MODULE] OfferCrawler deactivated.");
        }

        // Filter offers
        DocBundle[] group1 = null;
        DocBundle[] group2 = null;
        if (START_OFFER_FILTER) {
            System.out.println("[MODULE] OfferFilter activated.");
            OfferFilter of = new OfferFilter(persona);
            group1 = of.getDocBundle1();
            group2 = of.getDocBundle2();
            System.out.println("[MODULE] OfferFilter deactivated.");
        }

        // Write appropiate messages
        if (START_MESSAGE_WRITER && group1 != null && group2 != null) {
            System.out.println("[MODULE] MessageWriter activated.");
            MessageWriter msg_writer = new MessageWriter(persona);
            msg_writer.writeMsgs(group1, group2);
            System.out.println("[MODULE] MessageWriter deactivated.");
        }

        // Send messages
        if (START_MESSAGE_SENDER) {
            System.out.println("[MODULE] MessageSender activated.");
            MessageSender msg_sender = new MessageSender(persona);
            msg_sender.startSending();
            System.out.println("[MODULE] MessageSender deactivated.");
        }
    }
}
