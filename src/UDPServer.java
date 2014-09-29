import reliable.ReliablePacket;
import reliable.ReliableSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
