package wg_gesucht;

import java.util.Random;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.LinkedList;

public class CityCrawler {
	
	private final int maxPages = 3;
	private final int maxCities = 10;

    private HashMap<Integer, String> cities;
    private LinkedList<String> cityUrls;

    public CityCrawler() {
        cities = new HashMap<Integer, String>();
        cityUrls = new LinkedList<String>();
    }

    public HashMap<Integer, String>  getCityList() {
        return this.cities;
    }
    

    public void updateCityList() {
        String fst_url = "https://www.wg-gesucht.de/wg-zimmer-in-Aachen.";
        String snd_url = ".0.1.0.html";
        int counter = 1;

        while (counter <= maxCities) {
        	
        	for (int pageNr = 0; pageNr < maxPages; pageNr++)
        	{
        		FileWriter fileWriter = null;
                Random rand = new Random();
                int n = rand.nextInt(10);
                
                //assemble url
                String[] urlBack = snd_url.split("\\.");
                urlBack[urlBack.length - 2] = String.valueOf(pageNr);
                snd_url = String.join(".", urlBack);
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
                        System.out.printf("[%d] %s page %d\n", counter, city, pageNr);
                        this.cities.put(counter, city);
                        cityUrls.add(url);
                        File newTextFile = new File(path + "/rsc/cities/" + String.valueOf(counter) + "_" + city + "_p" + pageNr + ".html");

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

                    Thread.sleep(1000+(n*1000));
                } catch (Exception e) {
                    System.out.println("[ERROR] Fehler beim connecten.");
                }
        	}
        	
        	counter++;
        	
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
    	//OfferCrawler oc = new OfferCrawler();


    }

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }

	public LinkedList<String> getCityUrls() {
		return cityUrls;
	}

	public void setCityUrls(LinkedList<String> cityUrls) {
		this.cityUrls = cityUrls;
	}
}
