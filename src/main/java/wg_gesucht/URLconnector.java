package wg_gesucht;

import java.io.IOException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class URLconnector {
	
	public static Document connect(String url) throws InterruptedException, IOException
	{
		boolean infoRead = false;
		Document readDoc = null;
		while (!infoRead)
		{
			//avoid captcha
			Random rand = new Random();
			float f = rand.nextFloat();
			Thread.sleep(1000+(int)(f*10000));
			
			readDoc = Jsoup.connect(url).get();
			if (readDoc.title().equals("Überprüfung"))
			{
				System.out.println("[WARNING] Captcha gefunden.");
	            System.out.println("Bitte folgenden Link aufrufen und Captcha lösen:");
	            System.out.println("http://www.wg-gesucht.de/cuba.html");

	            Thread.sleep(10000);
			}
			else
			{
				infoRead = true;
			}
		}
		return readDoc;
	}

}
