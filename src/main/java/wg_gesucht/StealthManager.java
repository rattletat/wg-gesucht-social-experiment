package wg_gesucht;

import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;

public class StealthManager {

    private HashMap<String, String> cookies_memory;


    public StealthManager() {
        this.cookies_memory = new HashMap<String, String>();
    }


    public Response hide(Connection connection) throws IOException {
        connection.cookies(cookies_memory);
        connection.userAgent(getRandomUserAgent());
        connection.timeout(10000);
        Response response = connection.execute();
        this.cookies_memory.putAll(response.cookies());
        return response;
    }

    private static String getRandomUserAgent() {
        String base_path = System.getProperty("user.dir");
        ArrayList<String> lines = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader(base_path + "/rsc/user_agents/user_agents.txt"));) {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Something went wrong while parsing the user agent list.");
            return "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
        }

        Random r = new Random();
        String random_agent = lines.get(r.nextInt(lines.size()));
        System.out.println("[INFO] Random user agent used: " + random_agent);
        return random_agent;
    }
}
