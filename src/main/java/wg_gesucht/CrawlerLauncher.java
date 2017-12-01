package wg_gesucht;

public class CrawlerLauncher{
    public static void main(String arg[]){
        CityCrawler city_crawler = new CityCrawler();
        city_crawler.updateAll();
    }
}
