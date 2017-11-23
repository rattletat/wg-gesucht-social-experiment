package wg_gesucht;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;

public class CityCrawler {

    private HashMap<Integer, String> cities;

    public CityCrawler() {
        cities = new HashMap<Integer, String>();
    }

    public HashMap<Integer, String>  getCityList() {
        return this.cities;
    }

    public void updateCityList() {
        String fst_url = "https://www.wg-gesucht.de/wg-zimmer-in-Aachen.";
        String snd_url = ".0.1.0.html";
        int counter = 1;

        while (true) {
            FileWriter fileWriter = null;
            Random rand = new Random();
            int n = rand.nextInt(10);
            String url = fst_url + String.valueOf(counter) + snd_url;
            try {
                Document doc = Jsoup.connect(url).get();
                String content = doc.html();
                String path = System.getProperty("user.dir");
                String title = doc.title();
                //log(title);

                Pattern p = Pattern.compile("(?<=Angebote in ).*$");
                Matcher m = p.matcher(title);
                if (m.find() && !m.group(0).matches("")) {
                    String city = m.group(0);
                    System.out.printf("[%d] %s\n", counter, city);
                    this.cities.put(counter, city);
                    File newTextFile = new File(path + "/rsc/cities/" + String.valueOf(counter) + "_" + city + ".html");

                    fileWriter = new FileWriter(newTextFile);
                    fileWriter.write(content);
                    fileWriter.close();
                } else if (title.equals("Überprüfung")) {
                    System.out.println("[WARNING] Captcha gefunden.");
                    System.out.println("Bitte folgenden Link aufrufen und Captcha lösen:");
                    System.out.println(url);

                    Thread.sleep(10000);
                    continue;
                }

                else {
                    this.cities.put(counter, "None");
                    System.out.println("[ERROR] Kein Stadtname gefunden:");
                    System.out.println(title);
                }
                counter++;

                Thread.sleep(1000+(n*1000));
            } catch (Exception e) {
                System.out.println("[ERROR] Fehler beim connecten.");
            }

        }
    }

    public String getURLforCity(String city) {

        return "TODO";

    }

    public String getURLforNumber(int number) {

        return "TODO";
    }


    public static void main(String args[]) {
        CityCrawler crawler = new CityCrawler();
        crawler.updateCityList();
    }

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
}
