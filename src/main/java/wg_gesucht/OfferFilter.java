package main.java.wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Properties;

public class OfferFilter {

    LinkedList<DocBundle> filtered_docs = new LinkedList<DocBundle>();
    boolean male;
    boolean female;
    int age;

    DocBundle[] group1;
    DocBundle[] group2;

    public OfferFilter (Properties persona) {

        if (persona.getProperty("gender").equals("m")) this.male = true;
        else this.male = false;
        this.age = Integer.parseInt(persona.getProperty("age"));
        this.female = !this.male;

        File folder_contacts = new File(OfferCrawler.file_path_contacts);
        File[] listOfFiles = folder_contacts.listFiles();
        if (listOfFiles.length >= 1) {
            for (File f : listOfFiles) {
                if (f.isFile() && f.getName().contains(".html")) {
                    Document doc;
                    try {
                        doc = Jsoup.parse(f, "UTF-8", "");
                        // Add city ID
                        String[] parts = f.getName().split("_");
                        String id_str = parts[parts.length - 1].split("\\.")[0];
                        int city_id = Integer.valueOf(id_str);
                        checkDocAndAdd(doc, city_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.filtered_docs != null && this.filtered_docs.size() >= 1) {
                randomSplitHalf();
            } else {
                System.out.println("[WARNING] No offer after filtering found.");
            }
        }
    }

    private boolean checkDocAndAdd(Document doc, int city_id) throws IOException, InterruptedException {
        // Generate nice title
        String title = doc.title();
        if (title.length() > 50)
            title = title.substring(0, 50) + "...";

        // Check base.html
        if (doc.title().equals("")) return false;


        //check availability
        if (!doc.getElementsContainingOwnText("Kontaktaufnahme zur Zeit nicht möglich")
                .isEmpty()) {
            System.out.println("[INFO] Unavailable: " + title );
            return false;
        }

        //check gender
        Element headl_gesucht = doc.getElementsContainingOwnText("Gesucht wird").first();
        if (headl_gesucht == null) return true;
        Element div = headl_gesucht.parent();
        String query_string;
        if (male && female) query_string = "Frau|Mann";
        else if (male && !female) query_string = "Mann";
        else if (!male && female) query_string = "Frau";
        else query_string = "impossible";
        Elements els = div.select("*:matchesOwn(" + query_string + ")");
        if (els.isEmpty()) {
            System.out.println("[INFO] No matching gender found: " + title);
            return false;
        }
        Element genderAgeInfo = els.first();
        String genderAgeInfoString = genderAgeInfo.text();

        //check age
        if (!genderAgeInfoString.contains("zwischen")) return true;

        String[] splittedInfo = genderAgeInfoString.split(" ");
        String minAgeString = splittedInfo[splittedInfo.length - 4];
        String maxAgeString = splittedInfo[splittedInfo.length - 2];
        int minAgeParsed;
        int maxAgeParsed;
        try {
            minAgeParsed = Integer.parseInt(minAgeString);
            maxAgeParsed = Integer.parseInt(maxAgeString);
            if (minAgeParsed > age ||  maxAgeParsed < age) {
                System.out.println("[INFO] Not in age boundaries: " + title);
                return false;
            }

        } catch (NumberFormatException e) {
            System.err.println("[INFO] Could not read age boundaries in Document: " + title);
            return false;
        }

        // Check availability of contact_form
        String url = doc.selectFirst("a[class=\"btn btn-block btn-md btn-orange\"]").attr("href");
        Document contact_form = URLconnector.connect(url).parse();
        Element contact_name_container = contact_form.getElementsContainingOwnText("Nachricht an").first();
        if (contact_name_container == null) {
            System.out.println("[INFO] No contact link found: " + title);
            return false;
        } else {
            filtered_docs.add(new DocBundle(doc, contact_form, city_id));
            System.out.println("[INFO] Successfully added offer.");
        }

        return true;
    }

    public static void printDocArray(Document[] docs) {
        for (Document d : docs) {
            System.out.println(d.title());
        }
    }

    private void randomSplitHalf() {
        @SuppressWarnings("unchecked")
        LinkedList<DocBundle> docs_clone = (LinkedList<DocBundle>)filtered_docs.clone();
        Collections.shuffle(docs_clone);
        group1 = docs_clone.subList(0, docs_clone.size() / 2).toArray(new DocBundle[0]);
        group2 = docs_clone.subList(docs_clone.size() / 2, docs_clone.size()).toArray(new DocBundle[0]);
    }


    public DocBundle[] getDocBundle1() {
        return this.group1;
    }

    public DocBundle[] getDocBundle2() {
        return this.group1;
    }
}
