package main.java.wg_gesucht;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Properties;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.FormElement;

public class MessageSender {

    public static final String filePathGroup1 = "./rsc/messages/group1/";
    public static final String filePathGroup2 = "./rsc/messages/group2/";

    // public MessageSender() throws IOException {
    //     personas = new Properties[2];
    //     // read persona
    //     FileReader reader = new FileReader(MessageWriter.filePathPersona1);
    //     personas[0] = new Properties();
    //     personas[0].load(reader);
    //     reader = new FileReader(MessageWriter.filePathPersona2);
    //     personas[1] = new Properties();
    //     personas[1].load(reader);
    //     reader.close();
    //
    //     String filePath = filePathGroup1;
    //     for (int i = 0; i < 2; i++) {
    //         File dirGroup = new File(filePath);
    //         for (File dir : dirGroup.listFiles()) {
    //             sendMessage(dir, personas[i]);
    //         }
    //     }
    // }


    public boolean sendMessage(File dir, Properties persona) {
        // Load persona data
        String properties_path = dir.getAbsolutePath()
                                 + dir.getName() + ".properties";
        Properties msg_probs = new Properties();

        try(FileReader reader = new FileReader(properties_path)) {
            msg_probs.load(reader);
        } catch (IOException io) {
            System.out.println("[ERROR] Loading offer data from memory failed.");
            return false;
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
            return prompt(dir, persona);
        }

        FormElement form = (FormElement) doc.selectFirst("#send_message_form");
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
            MemoryManager.saveDocument(doc);
            System.out.println((form == null)); // TODO: Retry bei Fehler
            System.exit(1);
        }

        // Fill form elements
        char gender = persona.getProperty("gender").charAt(0);
        Elements options = salutation_form.getElementsByTag("option");
        for (Element opt : options) {
            System.out.println(opt.attributes());
            if (opt.attr("value").equals("1") && gender == 'm') opt.attr("selected", "");
            if (opt.attr("value").equals("2") && gender == 'f') opt.attr("selected", "");
        }


        String forename = persona.getProperty("forename");
        forename_form.val(forename);

        String surname = persona.getProperty("surname");
        surname_form.val(surname);

        String email = persona.getProperty("email");
        email_form.val(email);

        String msg = persona.getProperty("msg");
        msg_form.val(msg);

        agb_form.attr("checked", true);

        copy_form.attr("checked", true);


        Document result;
        String title;
        try {
            result = form.submit().cookies(response.cookies()).post();
            title = result.title();
        } catch (Exception e) {
            System.out.println("[ERROR] Submitting form failed.");
            return prompt(dir, persona);
        }
        if (title != null && title.equals("Vielen Dank. Ihre Nachricht wurde gesendet.")) {
            String stakeholder = msg_probs.getProperty("fullname");
            System.out.println("[INFO] Message sent: " + stakeholder);
            return true;
        } else {
            System.out.println("[WARNING] Send process failed.");
            System.out.println("Title: " + title);
            MemoryManager.saveDocument(doc);
            System.out.println("[INFO] HTML saved under 'rsc/debug' for debugging.");
            return prompt(dir, persona);
        }
    }

    private boolean prompt(File dir, Properties persona) {
        while (true) {
            System.out.println("[PROMPT] Retry? (y/n)");
            Scanner reader = new Scanner(System.in);
            String input = reader.next();
            reader.close();
            if (input != null && input.length() == 1) {
                char c = input.charAt(0);
                if (c == 'n') return false;
                if (c == 'y') return sendMessage(dir, persona);
                else System.out.println("[PROMPT] Invalid input: '" + c + "'");
            }
        }
    }
}
