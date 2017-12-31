package main.java.wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;

import org.jsoup.nodes.Document;

public class MemoryManager {

    private final static String URL_FILENAME = System.getProperty("user.dir") + "/rsc/city_urls.txt";


    public static void saveURLs(HashMap<Integer, String> users) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(URL_FILENAME))) {
            os.writeObject(users);
        } catch (Exception e) {}
    }

    /* Loads the URL List from 'URL_FILENAME' file. */
    @SuppressWarnings("unchecked")
    public static HashMap<Integer, String> readURLs() throws Exception {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(URL_FILENAME))) {
            return (HashMap<Integer, String>) is.readObject();
        } catch (Exception e) {
            System.out.println("[WARNING] Error while reading URL list.");
            return new HashMap<Integer, String>();
        }
    }

    public static boolean saveCityPage(int city_id, String city_name, int page_number, String content) {
        String base_path = System.getProperty("user.dir");
        String city_id_str = String.valueOf(city_id);
        String page_number_str = String.valueOf(page_number);

        String path = base_path + "/rsc/cities/" + city_id_str + "_"
                      + city_name + "_p" + page_number_str + ".html";

        File text_file = new File(path);

        try(FileWriter file_writer = new FileWriter(text_file)) {
            file_writer.write(content);
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }

    public static boolean saveDocument(Document doc) {
        String base_path = System.getProperty("user.dir");

        String path = base_path + "/rsc/debug/" + new Date().toString() + ".html";

        File text_file = new File(path);

        try(FileWriter file_writer = new FileWriter(text_file)) {
            file_writer.write(doc.html());
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }
}
