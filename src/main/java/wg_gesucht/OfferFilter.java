package wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OfferFilter {
	
	LinkedList<Document> filteredDocs;
	boolean male;
	boolean female;
	int minAge;
	int maxAge;
	
	public OfferFilter (boolean male, boolean female, int minAge, int maxAge)
	{
		filteredDocs = new LinkedList<Document>();
		
		this.male = male;
		this.female = female;
		this.minAge = minAge;
		this.maxAge = maxAge;
		
		
		File folderContacts = new File(OfferCrawler.filePathContacts);
		File[] listOfFiles = folderContacts.listFiles();
		for (File f : listOfFiles)
		{
			if (f.isFile() && f.getName().contains(".html"))
			{
				Document doc;
				try {
					doc = Jsoup.parse(f, "UTF-8", "");
					if (checkDoc(doc)) filteredDocs.add(doc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public boolean checkDoc(Document doc)
	{
		//check base.html
		if (doc.title().equals("")) return false;
		
		//check availability
		if (!doc.getElementsContainingOwnText("Kontaktaufnahme zur Zeit nicht möglich").isEmpty()) {
			System.out.println("unavailable: "+doc.title() );
			return false;
		}
		
		//check gender
		Element headlGesucht = doc.getElementsContainingOwnText("Gesucht wird").first();
		if (headlGesucht == null) return true;
		Element div = headlGesucht.parent();
		String queryString;
		if (male && female) queryString = "Frau|Mann";
		else if (male && !female) queryString = "Mann";
		else if (!male && female) queryString = "Frau";
		else queryString = "impossible";
		Elements els = div.select("*:matchesOwn("+queryString+")");
		if (els.isEmpty()) {
			System.out.println("no matching gender found: "+doc.title());
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
		try
		{
			minAgeParsed = Integer.parseInt(minAgeString);
			maxAgeParsed = Integer.parseInt(maxAgeString);
			if (minAgeParsed < minAge ||  maxAgeParsed > maxAge) {
				System.out.println("not in age boundaries: "+doc.title());
				return false;
			}
			
		}catch (NumberFormatException e)
		{
			System.err.println("Could not read age boundaries in Document: "+ doc.title());
			return false;
		}
		
		
		return true;
	}
	
	
	public void printFilteredDocs()
	{
		for (Document d: filteredDocs)
		{
			System.out.println(d.title());
		}
	}
	
	public static void printDocArray(Document[] docs)
	{
		for (Document d: docs)
		{
			System.out.println(d.title());
		}
	}
	
	public DocSplit randomSplitHalf()
	{
		@SuppressWarnings("unchecked")
		LinkedList<Document> docsClone = (LinkedList<Document>)filteredDocs.clone();
		Collections.shuffle(docsClone);
		Document[] d1 = docsClone.subList(0, docsClone.size() / 2).toArray(new Document[0]);
		Document[] d2 = docsClone.subList(docsClone.size() / 2 + 1, docsClone.size()).toArray(new Document[0]);
		return new DocSplit(d1, d2);
	}

}


class DocSplit
{
	Document[] d1;
	Document[] d2;
	
	public DocSplit(Document[] d1, Document[] d2) {
		this.d1 = d1;
		this.d2 = d2;
	}
	
	
}
