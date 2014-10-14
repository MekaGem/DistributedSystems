package ru.yandex.udp;

import ru.yandex.udp.reliable.ReliablePacket;
import ru.yandex.udp.reliable.ReliableSocket;

import java.io.IOException;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        int port = 50000;
        int length = 1000;
        byte[] buffer = new byte[length];

        ReliablePacket packet = new ReliablePacket(buffer, length);
        ReliableSocket socket = new ReliableSocket(port);

        socket.receive(packet);
        String result = new String(packet.getBuffer(), 0, packet.getLength());

        System.out.println("received: " + result);
    }
}
