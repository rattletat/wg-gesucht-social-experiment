package main.java.wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OfferFilter {
	
	LinkedList<DocBundle> filteredDocs;
	boolean male;
	boolean female;
	int age;
	
	DocBundle[] group1;
	DocBundle[] group2;
	
	public OfferFilter (boolean male, boolean female, int age)
	{
		filteredDocs = new LinkedList<DocBundle>();
		
		this.male = male;
		this.female = female;
		this.age = age;
		
		
		File folderContacts = new File(OfferCrawler.filePathContacts);
		File[] listOfFiles = folderContacts.listFiles();
		for (File f : listOfFiles)
		{
			if (f.isFile() && f.getName().contains(".html"))
			{
				Document doc;
				try {
					doc = Jsoup.parse(f, "UTF-8", "");
					checkDocAndAdd(doc);
				} catch (IOException e) {
					e.printStackTrace();
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		randomSplitHalf();
	}
	
	public void writeMsgs() throws InterruptedException, IOException
	{
		MessageWriter msgWriter = new MessageWriter();
		for (DocBundle db : group1)
		{
			msgWriter.writeMsg(db.getOfferDoc(), db.getContactForm(), 1);
		}
		for (DocBundle db : group2)
		{
			msgWriter.writeMsg(db.getOfferDoc(), db.getContactForm(), 2);
		}
	}
	
	public boolean checkDocAndAdd(Document doc) throws IOException, InterruptedException
	{
		//check base.html
		if (doc.title().equals("")) return false;
		

		//check availability
		if (!doc.getElementsContainingOwnText("Kontaktaufnahme zur Zeit nicht möglich")
                .isEmpty()){
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
			if (minAgeParsed > age ||  maxAgeParsed < age) {
				System.out.println("not in age boundaries: "+doc.title());
				return false;
			}
			
		}catch (NumberFormatException e)
		{
			System.err.println("Could not read age boundaries in Document: "+ doc.title());
			return false;
		}
		
		//check availability of contactForm
		String url = doc.selectFirst("a[class=\"btn btn-block btn-md btn-orange\"]").attr("href");
		Document contactForm = URLconnector.connect(url).parse();
		Element contactNameContainer = contactForm.getElementsContainingOwnText("Nachricht an").first();
		if (contactNameContainer == null) {
			System.out.println("no contact link found: "+ doc.title());
			return false;
		} else {
			filteredDocs.add(new DocBundle(doc, contactForm));
		}
		
		return true;
	}
	
	public static void printDocArray(Document[] docs)
	{
		for (Document d: docs)
		{
			System.out.println(d.title());
		}
	}
	
	public void randomSplitHalf()
	{
		@SuppressWarnings("unchecked")
		LinkedList<DocBundle> docsClone = (LinkedList<DocBundle>)filteredDocs.clone();
		Collections.shuffle(docsClone);
		group1 = docsClone.subList(0, docsClone.size() / 2).toArray(new DocBundle[0]);
		group2 = docsClone.subList(docsClone.size() / 2, docsClone.size()).toArray(new DocBundle[0]);
	}
}

class DocBundle
{
	private Document offer_doc;
	private Document contact_form;
	
	public DocBundle(Document offer_doc, Document contact_form) {
		this.offer_doc = offer_doc;
		this.contact_form = contact_form;
	}

    public Document getOfferDoc() {
        return this.offer_doc;
    }
	
    public Document getContactForm() {
        return this.contact_form;
    }
	
}
