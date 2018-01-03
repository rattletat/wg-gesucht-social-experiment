package main.java.propertiesCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class personaCreator {

    // Setup filePath
    public final static String filePath = "./rsc/personas/persona1.properties";

    // Setup age & gender
    public final static int age = 23;
    public final static char gender = 'm';

    // Setup first name
    public final static String forename1 = "Sebastian";
    public final static String surname1 = "Winkler";

    // Setup second name
    public final static String forename2 = "Achmet";
    public final static String surname2 = "Bukhari";

    // Setup formal and informal text
    public final static String text_formal = "mein Name ist Max Mustermann und blub...";
    public final static String text_informal = "ich bin Max und blablabla...";

    // Setup email provider
    // Existing emails >need< to have the following format:
    // forename.surname.cityID.{1,2}@email_provder
    public final static String email_provider = "fastmail.de";

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        props.setProperty("age", age + "");
        props.setProperty("gender", gender + "");
        props.setProperty("surname1", surname1);
        props.setProperty("forename1", forename1);
        props.setProperty("surname2", surname2);
        props.setProperty("forename2", forename2);
        props.setProperty("text_formal", text_formal);
        props.setProperty("text_informal", text_informal);
        props.setProperty("email_provider", email_provider);

        FileWriter writer = new FileWriter(filePath);
        props.store(writer, "");
    }

}
