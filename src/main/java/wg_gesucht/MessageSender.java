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

    private static final String filePathGroup1 = "./rsc/messages/group1/";
    private static final String filePathGroup2 = "./rsc/messages/group2/";

    private Properties persona;

    public MessageSender(Properties persona) {
        this.persona = persona;
    }

    /**
     * Initiate send process for each stakeholder group,
     * by loading the files and iteratively calling 'sendMessage()'.
     * @see #sendMessage(File, Properties)
     */
    public void startSending() {
        // Load messages into RAM
        File[] prop_files1;
        File[] prop_files2;
        try {
            File group_dir1 = new File(filePathGroup1);
            File group_dir2 = new File(filePathGroup2);

            prop_files1 = group_dir1.listFiles();
            prop_files2 = group_dir2.listFiles();
        } catch (Exception e) {
            System.err.println("[ERROR] Loading property files failed.");
            return;
        }

        // Send messages for both groups
        int counter1 = 0;
        int counter2 = 0;
        try {
            for (File file : prop_files1) {
                boolean result = sendMessage(file, persona);
                if (result) counter1++;
            }
            for (File file : prop_files2) {
                boolean result = sendMessage(file, persona);
                if (result) counter2++;
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Sending messages failed.");
        }
        System.out.println("[Group1] Messages sent so far: " + counter1 + "/" + prop_files1.length);
        System.out.println("[Group2] Messages sent so far: " + counter2 + "/" + prop_files2.length);
        return;
    }


    /**
     * Fills the form and sends it. In the case of a connection error,
     * it prompts the user whether to try again.
     * @param dir file that contains stakeholder information
     * @param persona profile properties which should be used for filling out the form
     * @see #prompt(File, Properties)
     */
    private static boolean sendMessage(File dir, Properties persona) {
        // Load persona data
        String properties_path = dir.getAbsolutePath()
                                 + dir.getName() + ".properties";
        Properties msg_props = new Properties();

        try(FileReader reader = new FileReader(properties_path)) {
            msg_props.load(reader);
        } catch (IOException io) {
            System.out.println("[ERROR] Loading offer data from memory failed.");
            return false;
        }

        // Extract form elements
        String url = msg_props.getProperty("url");
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

        String group_id = msg_props.getProperty("group_id");

        String forename = persona.getProperty("forename" + group_id);
        forename_form.val(forename);

        String surname = persona.getProperty("surname" + group_id);
        surname_form.val(surname);

        String city_id = msg_props.getProperty("city_id");
        String email_provider = msg_props.getProperty("email_provider");
        String email = forename + "." + surname + "." + city_id + "."
                       + group_id + "@" + email_provider;
        email_form.val(email);

        String msg = persona.getProperty("msg");
        msg_form.val(msg);

        agb_form.attr("checked", true);

        copy_form.attr("checked", true);

        // Send form
        Document result;
        String title;
        try {
            result = form.submit().cookies(response.cookies()).post();
            title = result.title();
        } catch (Exception e) {
            System.out.println("[WARNING] Submitting form failed.");
            return prompt(dir, persona);
        }
        if (title != null && title.equals("Vielen Dank. Ihre Nachricht wurde gesendet.")) {
            String stakeholder = msg_props.getProperty("fullname");
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


    /**
     * Prompts the user wheter to retry the sending process.
     * If so, it calls 'sendMessage()' again, otherwise it returns false.
     * @param dir a file of stakeholder information
     * @param persona profile properties to use
     * @see #sendMessage(File, Properties)
     */
    private static boolean prompt(File dir, Properties persona) {
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
