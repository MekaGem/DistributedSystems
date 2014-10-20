package ru.yandex.restapi;

import org.json.JSONArray;
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
    private static String resourcesURL = "https://cloud-api.yandex.net/v1/disk/resources";

    public static void main(String[] args) throws Exception {
        final String token = args[0];
        final DiskOperation operation = DiskOperation.getDiskOperation(args[1]);
        if (operation == null) {
            throw new IllegalArgumentException("There is no command: " + args[1]);
        }

        final List<String> params = new ArrayList<String>();
        if (args.length < 2 + operation.getNumberOfArguments()) {
            throw new IllegalArgumentException("Command \"" + operation.getCmd() + "\" takes " + operation.getNumberOfArguments()
                                                + " but only " + (args.length - 2) + " passed");
        }

        for (int index = 0; index < operation.getNumberOfArguments(); ++index) {
            params.add(args[2 + index]);
        }

        String url = operation.getURL(resourcesURL, params);
        URL requestURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
        connection.setRequestMethod(operation.getMethod());
        connection.setRequestProperty("Authorization", "OAuth " + token);

        int code = connection.getResponseCode();
        if (200 > code || code >= 300) {
            throw new IllegalStateException("Connection error: code=" + code);
        }

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String reply = reader.readLine();
        operation.processReply(reply);
    }
}
