package wg_gesucht;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OfferCrawler {

    public static final String file_path_cities = "./rsc/cities/";
    public static final String file_path_contacts = "./rsc/contacts";
    public static final String base_contact_filepath = "./rsc/base.html";
    public static final String last_contact_filepath = "./rsc/contacts/lastContact.txt";


    LinkedList<String> cityUrls;
    int lastContact = -1;
    int contactCounter = 0;

    public OfferCrawler() {
        File lastContFile = new File(last_contact_filepath);
        try {
            Scanner sc = new Scanner(lastContFile);
            lastContact = sc.nextInt();
            sc.close();
        } catch (Exception e1) {
            System.err.println("Could not find " + last_contact_filepath + ". Creating new one.");
            try {
                FileWriter file_writer = new FileWriter(last_contact_filepath);
                file_writer.write("-1");
                file_writer.close();
            } catch (Exception e2) {
                System.err.println("[ERROR] Failed creating new 'last contact' file.");
                System.exit(1);
            }
        }

        cityUrls = new LinkedList<String>();
        try {
            readURLsFromFileSystem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readURLsFromFileSystem() throws IOException {
        File folderCities = new File(file_path_cities);
        File[] listOfFiles = folderCities.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                Document doc = Jsoup.parse(file, "UTF-8", "");
                Elements links = doc.getElementsByTag("link");
                for (Element link : links) {
                    if (link.attr("rel").equals("canonical")) {
                        cityUrls.add(link.attr("href"));
                        break;
                    }
                }
                String name = file.getName();
                String[] splittedName = name.split("\\.");
                String[] splittedName2 = splittedName[0].split("_", 2);
                searchOffersInFile(doc, splittedName2[1], Integer.valueOf(splittedName2[0]), name);
            }
        }
    }

    public void searchOffersInFile(Document doc, String city, int city_id, String name) {
        // Print title
        System.out.println("-------" + doc.title() + " " + name + "-------");

        Elements headlines = doc.getElementsByTag("h3");
        for (Element headline : headlines) {
            if (headline.attr("class").equals("headline headline-list-view noprint truncate_title")) {
                //Element divcontainer =  headline.parent().parent();
                Element link = headline.selectFirst("a");
                String link_title = link.text();
                if (link_title.length() > 50)
                    link_title = link_title.substring(0, 50) + "...";
                System.out.println("[INFO] Crawled contact " + contactCounter + ": " + link_title);
                String url = link.attr("href");

                if (contactCounter <= lastContact) {
                    System.out.println("[INFO] Contact already extracted.");

                } else {
                    try {
                        saveContactInFile("https://www.wg-gesucht.de/" + url, "contact" + contactCounter + "_" + city + "_" + city_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                contactCounter++;
            }

        }
    }


    public void saveContactInFile(String url, String contactname) throws IOException, InterruptedException {
        Document readDoc = URLconnector.connect(url).parse();
        Element contactDiv = readDoc.selectFirst("div[class=\"panel panel-rhs-default rhs_contact_information hidden-sm\"]");
        Elements headlineElements = readDoc.select("h3:matchesOwn(Kosten|WG-Details)");
        Element title = readDoc.selectFirst("title");

        File basefile = new File(base_contact_filepath);
        Document writeDoc = Jsoup.parse(basefile, "UTF-8", url);
        Element body = writeDoc.body();
        Element head = writeDoc.head();
        title.appendTo(head);
        for (Element h : headlineElements) {
            Element div = h.parent().parent();
            div.appendTo(body);
        }
        contactDiv.appendTo(body);

        FileWriter file_writer = new FileWriter(file_path_contacts + "/" + contactname + ".html");
        file_writer.write(writeDoc.outerHtml());
        file_writer.close();

        FileWriter fileWriter2 = new FileWriter(last_contact_filepath);
        fileWriter2.write("" + contactCounter);
        fileWriter2.close();

        lastContact = contactCounter;
    }


}
