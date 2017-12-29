package main.java.wg_gesucht;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.FormElement;

public class MessageSender {

    public static final String filePathGroup1 = "./rsc/messages/group1/";
    public static final String filePathGroup2 = "./rsc/messages/group2/";

    private static String MSG_URL = "https://www.wg-gesucht.de/nachricht-senden.html?id=";

    private Properties[] personas;

    public MessageSender() throws IOException {
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
        for (int i = 0; i < 2; i++) {
            File dirGroup = new File(filePath);
            for (File dir : dirGroup.listFiles()) {
                sendMessage(dir, personas[i]);
            }
        }
    }


    public boolean sendMessage(File dir, Properties persona) throws IOException {
        // Load persona data
        String properties_path = dir.getAbsolutePath()
                                 + dir.getName() + ".properties";
        Properties msg_probs = new Properties();

        try(FileReader reader = new FileReader(properties_path)) {
            msg_probs.load(reader);
        } catch (IOException io) {
            throw io;
        }

        // Extract form elements
        String url = msg_probs.getProperty("url");
        Document doc = null;
        Response response = null;
        try {
            response = URLconnector.connect(url);
            doc = response.parse();
        } catch (Exception e) {
            System.out.println("[ERROR] Connection could not be established.");
            return false;
        }

        FormElement form = (FormElement) doc.selectFirst("#panel panel-form");
        Element salutation_form = doc.selectFirst("[name='u_anrede']");
        Element surname_form = doc.selectFirst("[name='nachname']");
        Element forename_form = doc.selectFirst("[name='vorname']");
        Element email_form = doc.selectFirst("#email_input");
        Element msg_form = doc.selectFirst("#nachricht-text");
        Element agb_form = doc.selectFirst("#agb");
        Element copy_form = doc.selectFirst("#kopieanmich");

        if (form == null || salutation_form == null || forename_form == null
                || surname_form == null || email_form == null || msg_form == null
                || agb_form == null || copy_form == null) {
            System.out.println("[WARNING] Could not find send form.");
            System.exit(1); // click button needed
        }

        // Fill form elements
        char gender = persona.getProperty("gender").charAt(0);
        if (gender == 'm') salutation_form.val("Herr");
        else salutation_form.val("Frau");

        String forename = persona.getProperty("forename");
        forename_form.val(forename);

        String surname = persona.getProperty("surname");
        surname_form.val(surname);

        String email = persona.getProperty("email");
        email_form.val(email);

        String msg = persona.getProperty("msg");
        msg_form.val(msg);

        agb_form.attr("checked");

        copy_form.attr("checked");

        Document result = form.submit().cookies(response.cookies()).post();
        String title = result.title();
        if (title.equals("Vielen Dank. Ihre Nachricht wurde gesendet.")) {
            String stakeholder = msg_probs.getProperty("fullname");
            System.out.println("[INFO] Message sent: " + stakeholder);
            return true;
        } else {
            System.out.println("[WARNING] Send process failed.");
            System.out.println("Title: " + title);
            return false;
        }
    }



}
