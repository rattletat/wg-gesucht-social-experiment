package main.java.wg_gesucht;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PersonaCreator {

    // Setup filePath
    public final static String filePath = "./rsc/personas/persona1.properties";

    // Setup age & gender
    public final static int age = 23;
    public final static char gender = 'm';

    // Setup first name
    // public final static String forename1 = "Sebastian";
    // public final static String surname1 = "Winkler";
    public final static String forename1 = "Olaf";
    public final static String surname1 = "Gutenberg";
    // Setup second name
    // public final static String forename2 = "Achmet";
    // public final static String surname2 = "Bukhari";
    public final static String forename2 = "Brolaf";
    public final static String surname2 = "Gustini";

    // Setup formal and informal text
    public final static String text_formal1 = "mein Name ist Olaf Gutenberg und ich suche zurzeit eine WG-Unterkunft. Ist bei Ihnen noch was frei?";
    public final static String text_informal1 = "ich bin der Olaf und suche zurzeit eine WG-Unterkunft. Ist bei dir noch was frei?";

    public final static String text_formal2 = "mein Name ist Brolaf Gustini und ich suche zurzeit eine WG-Unterkunft. Ist bei Ihnen noch was frei?";
    public final static String text_informal2 = "ich bin der Brolaf und suche zurzeit eine WG-Unterkunft. Ist bei dir noch was frei?";

    // Setup email provider
    // Existing emails >need< to have the following format:
    // forename.surname.cityID@email_provider
    public final static String email_provider = "20mail.eu";

    public static void refreshPersona() throws IOException {

        Properties props = new Properties();
        props.setProperty("age", age + "");
        props.setProperty("gender", gender + "");
        props.setProperty("surname1", surname1);
        props.setProperty("forename1", forename1);
        props.setProperty("surname2", surname2);
        props.setProperty("forename2", forename2);
        props.setProperty("text_formal1", text_formal1);
        props.setProperty("text_informal1", text_informal1);
        props.setProperty("text_formal2", text_formal2);
        props.setProperty("text_informal2", text_informal2);
        props.setProperty("email_provider", email_provider);

        FileWriter writer = new FileWriter(filePath);
        props.store(writer, "");
    }

}
