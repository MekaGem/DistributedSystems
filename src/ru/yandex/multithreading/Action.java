package ru.yandex.multithreading;

import java.io.*;
import java.net.Socket;

public class Action implements Runnable {
    private final Socket socket;

    public Action(Socket socket) {
        this.socket = socket;
    }

    public String process(String message) throws Exception {
        Thread.sleep(10000);
        return message + "!";
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            OutputStream os = socket.getOutputStream();
            Writer writer = new BufferedWriter(new OutputStreamWriter(os));

            String message = reader.readLine();
            writer.write(process(message) + "\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
