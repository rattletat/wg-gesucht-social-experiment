package wg_gesucht;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OfferCrawler {

	public static final String filePathCities = "./rsc/cities/";
	public static final String filePathContacts = "./rsc/contacts";
	public static final String baseContactFilepath = "./rsc/contacts/base.html";
	public static final String lastContactInfoFilepath = "./rsc/contacts/lastContact.txt";
	
	
	LinkedList<String> cityUrls;
	int lastContact = -1;
	int contactCounter = 0;
	
	public OfferCrawler() throws IOException
	{
		File lastContFile = new File(lastContactInfoFilepath);
		try {
			Scanner sc = new Scanner(lastContFile);
			lastContact = sc.nextInt();
			sc.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not find " + lastContactInfoFilepath+ ". Creating new one.");
			FileWriter fileWriter = new FileWriter(lastContactInfoFilepath);
	        fileWriter.write("-1");
	        fileWriter.close();
		}
		
		cityUrls = new LinkedList<String>();
		try {
			readURLsFromFileSystem();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readURLsFromFileSystem() throws IOException
	{
		File folderCities = new File(filePathCities);
		File[] listOfFiles = folderCities.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	Document doc = Jsoup.parse(file, "UTF-8", "");
		    	Elements links = doc.getElementsByTag("link");
		    	for (Element link : links)
		    	{
		    		if (link.attr("rel").equals("canonical"))
		    		{
		    			cityUrls.add(link.attr("href"));
		    			break;
		    		}
		    	}
		    	String name = file.getName();
		    	String[] splittedName = name.split("\\.");
		    	String[] splittedName2 = splittedName[0].split("_", 2);
		    	searchOffersInFile(doc, splittedName2[1]);
		    }
		}
	}
	
	public void searchOffersInFile(Document doc, String city)
	{
		//print title
		System.out.println("-------" + doc.title() + "-------");
		
		Elements headlines = doc.getElementsByTag("h3");
		for (Element headline : headlines)
		{
			if (headline.attr("class").equals("headline headline-list-view noprint"))
			{
				//Element divcontainer =  headline.parent().parent();
				Element link = headline.selectFirst("a");
				System.out.println(link.text());
				String url = link.attr("href");
				
				if (contactCounter <= lastContact)
				{
					System.out.println("(contact already extracted)");
					
				}
				else
				{
					try {
						saveContactInFile("https://www.wg-gesucht.de/"+url, "contact"+contactCounter+"_"+city);
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
	
	
	public void saveContactInFile(String url, String contactname) throws IOException, InterruptedException
	{		
		Document readDoc = URLconnector.connect(url);
		Element contactDiv = readDoc.selectFirst("div[class=\"panel panel-rhs-default rhs_contact_information hidden-sm\"]");
		Elements headlineElements = readDoc.select("h3:matchesOwn(Kosten|WG-Details)");
		Element title = readDoc.selectFirst("title");
		
		File basefile = new File(baseContactFilepath);
		Document writeDoc = Jsoup.parse(basefile, "UTF-8", url);
		Element body = writeDoc.body();
		Element head = writeDoc.head();
		title.appendTo(head);
		for (Element h: headlineElements)
		{
			Element div = h.parent().parent();
			div.appendTo(body);
		}
		contactDiv.appendTo(body);
		
		FileWriter fileWriter = new FileWriter(filePathContacts+"/"+contactname+".html");
        fileWriter.write(writeDoc.outerHtml());
        fileWriter.close();
        
        FileWriter fileWriter2 = new FileWriter(lastContactInfoFilepath);
        fileWriter2.write(""+contactCounter);
        fileWriter2.close();
        
        lastContact = contactCounter;
	}
	
	
}
