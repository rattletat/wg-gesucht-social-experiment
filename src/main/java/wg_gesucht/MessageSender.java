package main.java.wg_gesucht;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MessageSender {
	
	public static final String filePathGroup1 = "./rsc/messages/group1/";
	public static final String filePathGroup2 = "./rsc/messages/group2/";
	
    private static String MSG_URL = "https://www.wg-gesucht.de/nachricht-senden.html?id=";

	private Properties[] personas;
	
	public MessageSender() throws IOException
	{
		personas = new Properties[2];
		// read persona
		FileReader reader = new FileReader(MessageWriter.filePathPersona1);
		personas[0] = new Properties();
		personas[0].load(reader);
		reader = new FileReader(MessageWriter.filePathPersona2);
		personas[1] = new Properties();
		personas[1].load(reader);
		reader.close();
		
		String filePath = filePathGroup1;
		for (int i = 0; i<2; i++)
		{	
			File dirGroup = new File(filePath);
			for (File dir : dirGroup.listFiles())
			{
				sendMessage(dir, personas[i]);
			}
		}
	}
	
	public void sendMessage(File dir, Properties persona) throws IOException
	{
		String propertiesPath = dir.getAbsolutePath() + dir.getName() + ".porperties";
		Properties msgProps = new Properties();
		FileReader reader = new FileReader(propertiesPath);
		msgProps.load(reader);
		reader.close();
		
		//TODO connect, fill in form
	}


        public boolean fill_data(String url, String[] data) {

        Document doc = null;
        try {
            doc = URLconnector.connect(url).parse();
        } catch (Exception e) {
            System.out.println("[ERROR] Connection could not be established.");
        }
        Elements forms = doc.select(".form-control");
        Elements needed = forms.select("[name='u_anrede'], [name='vorname'], [name='nachname'], #email_input, [name='telefon'], [name='agb'], [name='kopieanmich']");
        for (Element element : needed) {
            System.out.println(element.toString());
        }
        return true;
    }



}
