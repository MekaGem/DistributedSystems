import reliable.ReliablePacket;
import reliable.ReliableSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        int port = 50000;
        int maxLength = 1000;
        byte[] buffer = new byte[maxLength];
        int length = System.in.read(buffer);

        InetAddress address = InetAddress.getLocalHost();
        ReliablePacket packet = new ReliablePacket(buffer, length, address, port);
        ReliableSocket socket = new ReliableSocket();

        if (socket.send(packet)) {
            System.out.println("send successful");
        } else {
            System.out.println("send error");
        }
    }
}
