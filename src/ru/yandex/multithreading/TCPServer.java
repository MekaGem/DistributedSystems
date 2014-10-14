package ru.yandex.multithreading;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private void connectNewClient(Socket socket) {
        executor.execute(new Action(socket));
    }

    public static void main(String[] args) throws Exception {
        final TCPServer server = new TCPServer();

        final int port = 50000;
        final ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connectionSocket = socket.accept();
            server.connectNewClient(connectionSocket);
        }
    }
}
