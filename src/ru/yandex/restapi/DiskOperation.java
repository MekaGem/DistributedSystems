package ru.yandex.restapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

enum DiskOperation {
    LS("ls", 1, "GET") {
        @Override
        public String getURL(String resourcesURL, List<String> params) throws UnsupportedEncodingException {
            String folderName = URLEncoder.encode(params.get(0), "UTF-8");
            return resourcesURL + "?path=" + folderName;
        }

        @Override
        public void processReply(String reply) {
            JSONObject jsonObject = new JSONObject(reply);
            if (jsonObject.has("_embedded")) {
                // Directory
                JSONArray files = jsonObject.getJSONObject("_embedded").getJSONArray("items");
                for (int index = 0; index < files.length(); ++index) {
                    JSONObject file = files.getJSONObject(index);
                    System.out.println(file.getString("name"));
                }
            } else {
                // File
                System.out.println(jsonObject.getString("name"));
            }
        }
    },
    CP("cp", 2, "POST") {
        @Override
        public String getURL(String resourcesURL, List<String> params) throws UnsupportedEncodingException {
            String from = URLEncoder.encode(params.get(0), "UTF-8");
            String path = URLEncoder.encode(params.get(1), "UTF-8");
            return resourcesURL + "/copy?from=" + from + "&path=" + path;
        }

        @Override
        public void processReply(String reply) {
            try {
                JSONObject jsonObject = new JSONObject(reply);
                System.out.println("OK");
            } catch (JSONException e) {
                System.out.println("ERROR");
            }
        }
    },
    MV("mv", 2, "POST") {
        @Override
        public String getURL(String resourcesURL, List<String> params) throws UnsupportedEncodingException {
            String from = URLEncoder.encode(params.get(0), "UTF-8");
            String path = URLEncoder.encode(params.get(1), "UTF-8");
            return resourcesURL + "/move?from=" + from + "&path=" + path;
        }

        @Override
        public void processReply(String reply) {
            try {
                JSONObject jsonObject = new JSONObject(reply);
                System.out.println("OK");
            } catch (JSONException e) {
                System.out.println("ERROR");
            }
        }
    },
    RM("rm", 1, "DELETE") {
        @Override
        public String getURL(String resourcesURL, List<String> params) throws UnsupportedEncodingException {
            String path = URLEncoder.encode(params.get(0), "UTF-8");
            return resourcesURL + "?path=" + path;
        }

        @Override
        public void processReply(String reply) {
            if (reply == null) {
                System.out.println("OK");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(reply);
                System.out.println("OK");
            } catch (JSONException e) {
                System.out.println("ERROR");
            }
        }
    };

    private final String cmd;
    private final int numberOfArguments;
    private final String method;

    DiskOperation(String cmd, int numberOfArguments, String method) {
        this.cmd = cmd;
        this.numberOfArguments = numberOfArguments;
        this.method = method;
    }

    public String getCmd() {
        return cmd;
    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public String getMethod() {
        return method;
    }

    public static DiskOperation getDiskOperation(String cmd) {
        for (DiskOperation operation : DiskOperation.values()) {
            if (operation.getCmd().equals(cmd)) {
                return operation;
            }
        }
        return null;
    }

    public abstract String getURL(String resourcesURL, List<String> params) throws UnsupportedEncodingException;

    public abstract void processReply(String reply);
}
