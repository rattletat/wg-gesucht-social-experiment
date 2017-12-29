package main.java.wg_gesucht;

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

    private Properties[] personas;

    public MessageWriter() throws InterruptedException, IOException {

        personas = new Properties[2];

        // read persona
        FileReader reader = new FileReader(filePathPersona1);
        personas[0] = new Properties();
        personas[0].load(reader);
        reader = new FileReader(filePathPersona2);
        personas[1] = new Properties();
        personas[1].load(reader);

        reader.close();
    }

    public void writeMsg(Document offerDoc, Document contactForm, int groupNr) throws IOException {

        if (groupNr > 2 || groupNr < 1) {
            System.err.println("invalid group number");
            return;
        }
        Properties personaProps = personas[groupNr - 1];
        String folderName = "group" + groupNr;

        char contactGender;
        String bgImgStyle = offerDoc.selectFirst("div[class=\"profile_image_dav\"]").attr("style");
        if (bgImgStyle.contains("/female.png")) {
            contactGender = 'f';
        } else if (bgImgStyle.contains("/male.png")) {
            contactGender = 'm';
        } else {
            contactGender = 'n';
        }

        String contactUrl = offerDoc.selectFirst("a[class=\"btn btn-block btn-md btn-orange\"]").attr("href");

        Element contactNameContainer = contactForm.getElementsContainingOwnText("Nachricht an").first();
        String[] contactNameTextSplit = contactNameContainer.text().split(" ");

        StringBuilder sb = new StringBuilder();
        LinkedList<String> nameComponents = new LinkedList<String>();
        int i = 2;
        while (!contactNameTextSplit[i].equals("senden:")) {
            nameComponents.add(contactNameTextSplit[i]);
            sb.append(contactNameTextSplit[i]);
            sb.append(" ");
            i++;
        }
        sb.deleteCharAt(sb.length() - 1);
        String contactName = sb.toString();

        String salutation;
        boolean informal;
        System.out.print(contactName + ": ");
        switch (contactGender) {
        case 'f':
            // informal
            if (nameComponents.size() == 1) {
                informal = true;
                salutation = "Liebe " + nameComponents.get(0) + ", ";
            }
            // formal
            else {
                informal = false;
                salutation = "Sehr geehrte Frau " + nameComponents.get(nameComponents.size() - 1) + ", ";
            }
            break;

        case 'm':
            // informal
            if (nameComponents.size() == 1) {
                informal = true;
                salutation = "Lieber " + nameComponents.get(0) + ", ";
            }
            // formal
            else {
                informal = false;
                salutation = "Sehr geehrter Herr " + nameComponents.get(nameComponents.size() - 1) + ", ";
            }
            break;
        default:
            informal = true;
            salutation = "Hallo " + contactName + ", ";
            break;
        }
        System.out.print(salutation);
        System.out.println();

        Properties msgProps = new Properties();
        msgProps.setProperty("url", contactUrl);
        if (informal)
            msgProps.setProperty("msg", salutation + personaProps.getProperty("textInformal"));
        else
            msgProps.setProperty("msg", salutation + personaProps.getProperty("textFormal"));

        // Full name property
        String fullname = String.join(" ", nameComponents);
        msgProps.setProperty("fullname", fullname);

        // Offer id property
        String[] urlprobs = contactUrl.split("=");
        String id = urlprobs[urlprobs.length - 1].trim();
        msgProps.setProperty("id", id);

        String folderToSave = filePathMessages + folderName + "/" + contactName + "/";
        File dir = new File(folderToSave);
        dir.mkdirs();
        msgProps.store(new FileWriter(folderToSave + contactName + ".properties"), "");
        FileWriter fileWriter = new FileWriter(folderToSave + contactName + ".html");
        fileWriter.write(contactForm.outerHtml());
        fileWriter.close();

    }
}
