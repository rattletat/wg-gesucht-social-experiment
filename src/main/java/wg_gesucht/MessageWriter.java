package main.java.wg_gesucht;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MessageWriter {

    public static final String filePathMessages = "./rsc/messages/";

    private Properties persona;

    public MessageWriter(Properties persona) {
        this.persona = persona;
    }


    public void writeMsgs(DocBundle[] group1, DocBundle[] group2) {
        if (group1.length == 0)
            System.out.println("[INFO] No elements in group 1. Nothing to do here.");
        for (DocBundle db : group1) {
            writeMsg(db.getOfferDoc(), db.getContactForm(), 1, db.getCityID());
        }
        if (group1.length == 0)
            System.out.println("[INFO] No elements in group 2. Nothing to do here.");
        for (DocBundle db : group2) {
            writeMsg(db.getOfferDoc(), db.getContactForm(), 2, db.getCityID());
        }
    }


    private void writeMsg(
        Document offer_doc,
        Document contact_form,
        int group_nr,
        int city_id
    ) {

        if (group_nr > 2 || group_nr < 1) {
            System.err.println("invalid group number");
            return;
        }
        String folder_name = "group" + group_nr;

        char contactGender;
        String bgImgStyle = offer_doc.selectFirst("div[class=\"profile_image_dav\"]").attr("style");
        if (bgImgStyle.contains("/female.png")) {
            contactGender = 'f';
        } else if (bgImgStyle.contains("/male.png")) {
            contactGender = 'm';
        } else {
            contactGender = 'n';
        }

        String contact_url = offer_doc.selectFirst("a[class=\"btn btn-block btn-md btn-orange\"]").attr("href");

        Element contactNameContainer = contact_form.getElementsContainingOwnText("Nachricht an").first();
        String[] contactNameTextSplit = contactNameContainer.text().split(" ");

        StringBuilder sb = new StringBuilder();
        LinkedList<String> name_components = new LinkedList<String>();
        int i = 2;
        while (!contactNameTextSplit[i].equals("senden:")) {
            name_components.add(contactNameTextSplit[i]);
            sb.append(contactNameTextSplit[i]);
            sb.append(" ");
            i++;
        }
        sb.deleteCharAt(sb.length() - 1);
        String contact_name = sb.toString();

        String salutation;
        boolean informal;
        System.out.print(contact_name + ": ");
        switch (contactGender) {
        case 'f':
            // informal
            if (name_components.size() == 1) {
                informal = true;
                salutation = "Liebe " + name_components.get(0) + ", ";
            }
            // formal
            else {
                informal = false;
                salutation = "Sehr geehrte Frau " + name_components.get(name_components.size() - 1) + ", ";
            }
            break;

        case 'm':
            // informal
            if (name_components.size() == 1) {
                informal = true;
                salutation = "Lieber " + name_components.get(0) + ", ";
            }
            // formal
            else {
                informal = false;
                salutation = "Sehr geehrter Herr " + name_components.get(name_components.size() - 1) + ", ";
            }
            break;
        default:
            informal = true;
            salutation = "Hallo " + contact_name + ", ";
            break;
        }
        System.out.print(salutation);
        System.out.println();

        Properties msg_props = new Properties();
        msg_props.setProperty("url", contact_url);
        if (informal)
            msg_props.setProperty("msg", salutation + persona.getProperty("text_informal"));
        else
            msg_props.setProperty("msg", salutation + persona.getProperty("text_formal"));

        // Full name property
        String full_name = String.join(" ", name_components);
        msg_props.setProperty("full_name", full_name);

        // City property
        msg_props.setProperty("city_id", String.valueOf(city_id));

        // Group properties
        msg_props.setProperty("group", String.valueOf(group_nr));

        String folder_to_save = filePathMessages + folder_name + "/" + contact_name + "/";
        File dir = new File(folder_to_save);
        dir.mkdirs();
        try {
            msg_props.store(new FileWriter(folder_to_save + contact_name + ".properties"), "");
            FileWriter file_writer = new FileWriter(folder_to_save + contact_name + ".html");
            file_writer.write(contact_form.outerHtml());
            file_writer.close();
            System.out.println("[INFO] Successfully written message: " + contact_name);
        } catch (Exception e) {
            System.err.println("[ERROR] Could not save message (Properties file).");
        }

    }
}
