package main.java.wg_gesucht;

import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;

public class StealthManager {

    private HashMap<String, HashMap<String, String>> cookie_database;


    public StealthManager() {
        this.cookie_database = new HashMap<String, HashMap<String, String>>();
    }


    public Response hide(Connection connection) throws IOException {
        String random_agent = getRandomUserAgent();
        HashMap<String, String> cookies_memory = getUserCookies(random_agent);
        connection.cookies(cookies_memory);
        connection.userAgent(random_agent);
        connection.timeout(10000);
        Response response = connection.execute();
        this.cookie_database.get(random_agent).putAll(response.cookies());
        return response;
    }

    private HashMap<String, String> getUserCookies(String user_agent) {
        if (this.cookie_database.containsKey(user_agent)) {
            return this.cookie_database.get(user_agent);
        }
        HashMap<String, String> new_cookies = new HashMap<String, String>();
        this.cookie_database.put(user_agent, new_cookies);
        return new_cookies;
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
        //System.out.println("[INFO] Random user agent used: " + random_agent);
        return random_agent;
    }
}
