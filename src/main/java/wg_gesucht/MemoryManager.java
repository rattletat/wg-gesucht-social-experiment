package wg_gesucht;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;

public class MemoryManager {

    private final static String URL_FILENAME = System.getProperty("user.dir") + "/rsc/city_urls.txt";


    public static void saveURLs(HashMap<Integer, String> users) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(URL_FILENAME))) {
            os.writeObject(users);
        } catch (Exception e) {}
    }

    public static HashMap<Integer, String> readURLs() throws Exception {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(URL_FILENAME))) {
            return (HashMap<Integer, String>) is.readObject();
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

}
