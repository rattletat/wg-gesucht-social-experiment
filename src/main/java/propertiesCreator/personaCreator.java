package main.java.propertiesCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class personaCreator {

	public final static String filePath = "./rsc/personas/persona1.properties";
	
	public final static int age = 25;
	public final static char gender = 'm';
	public final static String textInformal = "ich bin Max und blablabla...";
	public final static String textFormal = "mein Name ist Max Mustermann und blub...";
	
	public static void main(String[] args) throws IOException {

		Properties props = new Properties();
		props.setProperty("age", age+"");
		props.setProperty("gender", gender+"");
		props.setProperty("textInformal", textInformal);
		props.setProperty("textFormal", textFormal);
		
		FileWriter writer = new FileWriter(filePath);
		props.store(writer, "");
	}

}
