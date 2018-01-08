package main.java.wg_gesucht;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PersonaCreator {

    // Setup filePath
    public final static String filePath = "./rsc/personas/persona1.properties";

    // Setup age & gender
    public final static int age = 22;
    public final static char gender = 'm';

    // Setup first name
    public final static String forename1 = "Sebastian";
    public final static String surname1 = "Winkler";
    // Setup second name
    public final static String forename2 = "Achmet";
    public final static String surname2 = "Bukhari";

    // Setup formal and informal text
    public final static String text_formal1 = "mein Name ist Sebastian Winkler und ich studiere im 3. Semester Maschinenbau. Zurzeit bin ich 22 Jahre alt (werde aber bald 23) und suche im Moment nach einer netten Wohngemeinschaft. Ich bin Nichtraucher und habe keinerlei (mir bekannten) Allergien. In meiner Freizeit mache ich gerne Sport (im Winter Skilaufen, sonst Volleyball, 'Fitnessstudio') und spiele Gitarre (keine Sorge, nicht daheim, nur mit meiner Band im Bandproberaum). Außerdem koche ich ab und zu gerne (meine Freunde empfehlen mich weiter (: ). Ich würde mir gerne die Wohnung mal anschauen, aber ich kann Ihnen auch erst mehr Informationen zu mir senden wenn gewollt. (Facebook, Telefonnummer). Ich würde mich freuen von Ihnen zur hören! Beste Grüße, Sebastian.";
    public final static String text_informal1 = "mein Name ist Sebastian Winkler und ich studiere im 3. Semester Maschinenbau. Zurzeit bin ich 22 Jahre alt (werde aber bald 23) und suche im Moment nach einer netten Wohngemeinschaft. Ich bin Nichtraucher und habe keinerlei (mir bekannten) Allergien. In meiner Freizeit mache ich gerne Sport (im Winter Skilaufen, sonst Volleyball, 'Fitnessstudio') und spiele Gitarre (keine Sorge, nicht daheim, nur mit meiner Band im Bandproberaum). Außerdem koche ich ab und zu gerne (meine Freunde empfehlen mich weiter (: ). Ich würde mir gerne die Wohnung mal anschauen, aber ich kann dir auch erst mehr Informationen zu mir senden wenn gewollt. (Facebook, Telefonnummer). Ich würde mich freuen von dir zur hören! Beste Grüße, Sebastian.";

    public final static String text_formal2 = "mein Name ist Achmet Bukhari und ich studiere im 3. Semester Maschinenbau. Zurzeit bin ich 22 Jahre alt (werde aber bald 23) und suche im Moment nach einer netten Wohngemeinschaft. Ich bin Nichtraucher und habe keinerlei (mir bekannten) Allergien. In meiner Freizeit mache ich gerne Sport (im Winter Skilaufen, sonst Volleyball, 'Fitnessstudio') und spiele Gitarre (keine Sorge, nicht daheim, nur mit meiner Band im Bandproberaum). Außerdem koche ich ab und zu gerne (meine Freunde empfehlen mich weiter (: ). Ich würde mir gerne die Wohnung mal anschauen, aber ich kann Ihnen auch erst mehr Informationen zu mir senden wenn gewollt. (Facebook, Telefonnummer). Ich würde mich freuen von Ihnen zur hören! Beste Grüße, Achmet.";
    public final static String text_informal2 = "mein Name ist Achmet Bukhari und ich studiere im 3. Semester Maschinenbau. Zurzeit bin ich 22 Jahre alt (werde aber bald 23) und suche im Moment nach einer netten Wohngemeinschaft. Ich bin Nichtraucher und habe keinerlei (mir bekannten) Allergien. In meiner Freizeit mache ich gerne Sport (im Winter Skilaufen, sonst Volleyball, 'Fitnessstudio') und spiele Gitarre (keine Sorge, nicht daheim, nur mit meiner Band im Bandproberaum). Außerdem koche ich ab und zu gerne (meine Freunde empfehlen mich weiter (: ). Ich würde mir gerne die Wohnung mal anschauen, aber ich kann dir auch erst mehr Informationen zu mir senden wenn gewollt. (Facebook, Telefonnummer). Ich würde mich freuen von dir zur hören! Beste Grüße, Achmet.";

    // Setup email provider
    // Existing emails >need< to have the following format:
    // forename.surname.cityID@email_provider
    public final static String email_provider = "gmx.de";

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
