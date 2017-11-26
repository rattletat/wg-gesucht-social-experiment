package wg_gesucht;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


public class CityCrawler {

    private final int delay = 30;
    private final int max_pages = 3;
    private final int max_cities = 10;

    private HashMap<Integer, String> cities;
    private LinkedList<String> city_urls;
    private StealthManager stealth_manager;

    public CityCrawler() {
        cities = new HashMap<Integer, String>();
        city_urls = new LinkedList<String>();
        this.stealth_manager = new StealthManager();
    }

    public HashMap<Integer, String>  getCityList() {
        return this.cities;
    }

    /**
      Updates and saves the various appartment-offer sites classfied by site in /rsc/cities.
      Quantity depends on 'max_cities' and 'max_pages'.
     **/
    public void updateCityList() {
        String fst_url = "https://www.wg-gesucht.de/wg-zimmer-in-Aachen.";
        String snd_url = ".0.1.0.html";
        int counter = 1;

        while (counter <= max_cities) {

            for (int page_number = 0; page_number < max_pages; page_number++) {
                Random rand = new Random();
                float random_percentage = rand.nextFloat();

                //assemble url
                String[] urlBack = snd_url.split("\\.");
                urlBack[urlBack.length - 2] = String.valueOf(page_number);
                snd_url = String.join(".", urlBack);
                String url = fst_url + String.valueOf(counter) + snd_url;

                Document doc = null;
                try {
                    Response res = stealth_manager.hide(Jsoup.connect(url));
                    doc = res.parse();
                } catch (IOException io) {
                    System.out.println("[ERROR] Fehler beim connecten.");
                    System.exit(1);
                }

                String content = doc.html();
                String title = doc.title();

                Pattern p = Pattern.compile("(?<=Angebote in ).*$");
                Matcher m = p.matcher(title);

                // City name found matching 'Angebote in ...' and not empty
                if (m.find() && !m.group(0).matches("")) {
                    String city = m.group(0).replaceAll(" ", "-");
                    System.out.printf("[%d-%d] %s\n", counter, page_number, city);

                    this.cities.put(counter, city);
                    city_urls.add(url);

                    if (!savePage(counter, city, page_number, content)) {
                        System.out.print("[ERROR] Speichern fehlgeschlagen.");
                    }


                }
                // Captcha site starts with "Überprüfung..."
                else if (title.equals("Überprüfung")) {
                    System.out.println("[HINT] Captcha gefunden.");
                    System.out.println("Bitte folgenden Link aufrufen und Captcha lösen:");
                    System.out.println(url);
                }

                // Unknown site found
                else {
                    this.cities.put(counter, "None");
                    System.out.println("[WARNING] Kein Stadtname gefunden:");
                    System.out.println(title);
                }
                try {
                    Thread.sleep(30000 + (int) (random_percentage * delay * 1000));

                } catch (InterruptedException ie) {}
            }
        }
        counter++;
    }


    /** Saves HTML-content to /rsc/cities with '[cityID]_[city_name]_p[page].html'.
     * @param city_id       City ID at wg-gesucht.de
     * @param city_name     Name of the city
     * @param page_number   Number of the page *
     * @param content       HTML content which will be saved
     * @return              boolean: 'true' means that the saving was successful
     *                               'false' means that an error occured
    **/
    private boolean savePage(int city_id, String city_name, int page_number, String content) {
        String base_path = System.getProperty("user.dir");
        String city_id_str = String.valueOf(city_id);
        String page_number_str = String.valueOf(page_number);

        String path = base_path + "/rsc/cities/" + city_id_str + "_"
                      + city_name + "_p" + page_number_str + ".html";

        File text_file = new File(path);

        try(FileWriter file_writer = new FileWriter(text_file)) {
            file_writer.write(content);
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }


    private String getCityID(int number) {

        return "TODO";
    }


    public static void main(String args[]) {
        CityCrawler crawler = new CityCrawler();
        crawler.updateCityList();
        //OfferCrawler oc = new OfferCrawler();


    }

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }

    public LinkedList<String> getCityUrls() {
        return city_urls;
    }

    public void setCityUrls(LinkedList<String> city_urls) {
        this.city_urls = city_urls;
    }
}
