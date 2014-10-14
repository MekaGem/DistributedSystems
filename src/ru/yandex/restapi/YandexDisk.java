package ru.yandex.restapi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class YandexDisk {
    private static enum DiskOperation {
        LS("ls", 1),
        CP("cp", 2),
        MV("mv", 2);

        private final String cmd;
        private final int numberOfArguments;

        DiskOperation(String cmd, int numberOfArguments) {
            this.cmd = cmd;
            this.numberOfArguments = numberOfArguments;
        }

        public String getCmd() {
            return cmd;
        }

        public int getNumberOfArgumnets() {
            return numberOfArguments;
        }

        public static DiskOperation getDiskOperation(String cmd) {
            try {
                return DiskOperation.valueOf(cmd);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static String token = "df566bba13b547cc9dd37766554cc187";
    private static String resourcesURL = "https://cloud-api.yandex.net/v1/disk/resources";

    public static void main(String[] args) throws Exception {
        DiskOperation operation = DiskOperation.getDiskOperation(args[0]);
        if (operation == null) {
            throw new IllegalArgumentException("There is no command: " + args[0]);
        }

//        List<String> params = new ArrayList<String>();
//        for ()

        String folderName = URLEncoder.encode(args[0], "UTF-8");
        URL requestURL = new URL(resourcesURL + "?path=" + folderName);
        HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "OAuth " + token);

        int code = connection.getResponseCode();
        InputStream inputStream = null;
        if (200 <= code && code < 300) {
            inputStream = connection.getInputStream();
        } else if (400 <= code && code < 600) {
            inputStream = connection.getErrorStream();
        }

        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
