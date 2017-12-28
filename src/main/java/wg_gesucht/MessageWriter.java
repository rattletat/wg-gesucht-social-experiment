package wg_gesucht;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MessageWriter {

	public static final String filePathMessages = "./rsc/messages/";
	public static final String filePathPersona1 = "./rsc/personas/persona1.properties";
	public static final String filePathPersona2 = "./rsc/personas/persona2.properties";
	
	public MessageWriter(DocSplit ds) throws InterruptedException, IOException {
		
		//read persona
		FileReader reader = new FileReader(filePathPersona1);
		Properties personaProps1 = new Properties();
		personaProps1.load(reader);
		reader = new FileReader(filePathPersona2);
		Properties personaProps2 = new Properties();
		personaProps2.load(reader);
		
		writeMsgs(ds.d1, personaProps1, "group1");
		System.out.println();
		writeMsgs(ds.d2, personaProps2, "group2");
		reader.close();
	}
	
	public void writeMsgs(Document[] docs, Properties personaProps, String folderName) throws InterruptedException, IOException
	{
		for (Document doc : docs)
		{
			char contactGender;
			String bgImgStyle = doc.selectFirst("div[class=\"profile_image_dav\"]").attr("style");
			if (bgImgStyle.contains("/female.png"))
			{
				contactGender = 'f';
			}
			else if (bgImgStyle.contains("/male.png"))
			{
				contactGender = 'm';
			}
			else
			{
				contactGender = 'n';
			}
			
			String url = doc.selectFirst("a[class=\"btn btn-block btn-md btn-orange\"]").attr("href");
			Document contactForm = URLconnector.connect(url).parse();
			Element contactNameContainer = contactForm.getElementsContainingOwnText("Nachricht an").first();
			if (contactNameContainer == null)
			{
				System.out.println("no contact link found");
			}
			else
			{
				String[] contactNameText = contactNameContainer.text().split(" ");
				
				StringBuilder sb = new StringBuilder();
				LinkedList<String> nameComponents = new LinkedList<String>();
				int i = 2;
				while (!contactNameText[i].equals("senden:"))
				{
					nameComponents.add(contactNameText[i]);
					sb.append(contactNameText[i]);
					sb.append(" ");
					i++;
				}
				sb.deleteCharAt(sb.length()-1);
				String contactName = sb.toString();
				
				String salutation;
				boolean informal;
				System.out.print(contactName+": ");
				switch(contactGender)
				{
				case 'f':
					//informal
					if (nameComponents.size() == 1) {
						informal = true;
						salutation = "Liebe "+nameComponents.get(0)+", ";
					}
					//formal
					else {
						informal = false;
						salutation = "Sehr geehrte Frau "+nameComponents.get(nameComponents.size() - 1)+", ";
					}
					break;
				case 'm':
					//informal
					if (nameComponents.size() == 1) {
						informal = true;
						salutation = "Lieber "+nameComponents.get(0)+", ";
					}
					//formal
					else {
						informal = false;
						salutation = "Sehr geehrter Herr "+nameComponents.get(nameComponents.size() - 1)+", ";
					}
					break;
				default:
					informal = true;
					salutation = "Hallo "+contactName+", ";
					break;
				}
				System.out.print(salutation);
				System.out.println();
				
				Properties msgProps = new Properties();
				msgProps.setProperty("url", url);
				if (informal) msgProps.setProperty("msg", salutation+personaProps.getProperty("textInformal"));
				else msgProps.setProperty("msg", salutation+personaProps.getProperty("textFormal"));
				
				String folderToSave = filePathMessages+folderName+"/"+contactName+"/";
				File dir = new File(folderToSave);
				dir.mkdirs();
				msgProps.store(new FileWriter(folderToSave+contactName+".properties"), "");
				FileWriter fileWriter = new FileWriter(folderToSave+contactName+".html");
		        fileWriter.write(contactForm.outerHtml());
		        fileWriter.close();
			}
			
		}
	}
}
