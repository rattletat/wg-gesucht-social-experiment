package wg_gesucht;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class CityCrawler {

    private final static int delay = 10;
    private final static int max_pages = 1;
    private final static int max_cities = 144; // Last City on wg-gesucht 'Zwickau' with id 144

    private HashMap<Integer, String> cities;
    private HashMap<Integer, String> city_urls;

    public CityCrawler() {
        this.cities = new HashMap<Integer, String>();
        try {
            this.city_urls = MemoryManager.readURLs();


        } catch (Exception e) {
            System.out.println("[WARNING] Failed to load URLs.");
            this.city_urls = new HashMap<>();
        }
    }

    // Returns city list
    public HashMap<Integer, String>  getCityList() {
        return this.cities;
    }

    // Updates all 144 cities
    public void updateAll() {
        ArrayList<Integer> array_list = new ArrayList<Integer>();
        for (int i = 1; i <= max_cities; i++) {
            array_list.add(i);
        }
        updateCityList(array_list);
    }

    /**
      Updates and saves the various appartment-offer sites classfied by site in /rsc/cities.
      @param list Updates only citys with IDs handed in list.
     **/
    public void updateCityList(ArrayList<Integer> array_list) {
        String fst_url = "https://www.wg-gesucht.de/wg-zimmer-in-Aachen.";
        String snd_url = ".0.1.0.html";
        Collections.shuffle(array_list);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                MemoryManager.saveURLs(city_urls);
                System.out.println("[INFO] Saved URL List.");
            }
        });

        for (int city_id : array_list) {

            for (int page_number = 0; page_number < max_pages; page_number++) {
                Random rand = new Random();
                float random_percentage = rand.nextFloat();

                String url = "";
                if (!this.city_urls.containsKey(city_id)) {
                    //assemble url
                    String[] url_back = snd_url.split("\\.");
                    url_back[url_back.length - 2] = String.valueOf(page_number);
                    snd_url = String.join(".", url_back);
                    url = fst_url + String.valueOf(city_id) + snd_url;
                } else url = this.city_urls.get(city_id) + ".0.1." + String.valueOf(page_number) + ".html";
                System.out.println(url);

                Document doc = null;
                String redirected_url = "";
                try {
                    Response res = URLconnector.connect(url);
                    redirected_url = res.url().toString();
                    doc = res.parse();
                } catch (Exception e) {
                    System.out.println("[ERROR] Fehler beim connecten.");
                    System.out.println(url);
                    System.exit(1);
                }

                String content = doc.html();
                String title = doc.title();

                Pattern p = Pattern.compile("(?<=Angebote in ).*$");
                Matcher m = p.matcher(title);

                // City name found matching 'Angebote in ...' and not empty
                if (m.find() && !m.group(0).matches("")) {
                    String city = m.group(0).replaceAll(" ", "-");
                    System.out.printf("[%d-%d] %s\n", city_id, page_number, city);

                    String cleaned_url = String.join(".", Arrays.copyOfRange(redirected_url.split("\\."), 0, 4));
                    // Save URL and city name
                    this.cities.put(city_id, city);
                    this.city_urls.put(city_id, cleaned_url);
                    if (!MemoryManager.saveCityPage(city_id, city, page_number, content)) {
                        System.out.print("[ERROR] Speichern fehlgeschlagen.");
                    }

                }
                // Captcha site starts with "Überprüfung..."
                else if (title.equals("Überprüfung")) {
                    System.out.println("[HINT] Captcha gefunden.");
                    System.out.println("Bitte folgenden Link aufrufen und Captcha lösen:");
                    System.out.println("https://www.wg-gesucht.de/cuba.html");
                }

                // Unknown site found
                else {
                    this.cities.put(city_id, "None");
                    System.out.println("[WARNING] Kein Stadtname gefunden:");
                    System.out.println("ID: " + city_id + "Title: " + title);
                }
                try {
                    Thread.sleep(10000 + (int) (random_percentage * delay * 1000));

                } catch (InterruptedException ie) {}
            }
        }
    }
}
