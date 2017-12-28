package main.java.wg_gesucht;

import java.io.IOException; import java.util.Random;
import org.jsoup.Connection.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class URLconnector {
	
	private static int min_waiting_sec = 5;
	private static int variance = 5;
	
	private static StealthManager stealth_manager = new StealthManager();
	
	/** 
		Delayed connection to a site and uses random useragents and cookies to act human.
	**/
	public static Response connect(String url) throws InterruptedException, IOException
	{
		boolean connection_successful = false;
        Response response = null;
		while (!connection_successful)
		{
			// Randomized waiting time
			Random rand = new Random();
			float percentage = rand.nextFloat();
			Thread.sleep(min_waiting_sec*1000 + (int)(percentage*1000*variance));
			
			// Establish connection using stealth techniques
			response = stealth_manager.hide(Jsoup.connect(url));

            // Enable multiparsing
            response.bufferUp();

			Document doc = response.parse();
			
			// Captcha found
			if (doc.title().equals("Überprüfung"))
			{
				System.out.println("[WARNING] Captcha gefunden.");
	            		System.out.println("Bitte folgenden Link aufrufen und Captcha lösen:");
	            		System.out.println("http://www.wg-gesucht.de/cuba.html");
				Thread.sleep(10000);
			}
			else
			{
				connection_successful = true;
			}
		}
		return response;
	}

}
