package wg_gesucht;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MessageSender {
	
	public static final String filePathGroup1 = "./rsc/messages/group1/";
	public static final String filePathGroup2 = "./rsc/messages/group2/";
	
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

}
