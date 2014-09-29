package reliable;

import java.io.IOException;
import java.net.*;

public class ReliableSocket {
    private static final int TIMEOUT = 5000;

    private int port;

    private final DatagramSocket socket;
    private final byte[] buffer = new byte[10000];

    public ReliableSocket() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT);
    }

    public ReliableSocket(int port) throws SocketException {
        this.port = port;
        socket = new DatagramSocket(port);
    }

    public boolean send(ReliablePacket packet) throws IOException {
        try {
            DatagramPacket message = packet.createDatagramPacket();
            final InetAddress address = InetAddress.getLocalHost();
            DatagramPacket confirmation = new DatagramPacket(buffer, ReliablePacket.HEADER_LENGTH, address, port);

            socket.send(message);
            socket.receive(confirmation);

            for (int index = 1; index < ReliablePacket.HEADER_LENGTH; ++index) {
                if (message.getData()[message.getOffset() + index] != buffer[index]) {
                    return false;
                }
            }
            return buffer[0] == ReliablePacket.TYPE_CONFIRMATION;
        } catch (IOException e){
            System.err.println(e);
            return false;
        }
    }

    public void receive(ReliablePacket packet) throws IOException {
        DatagramPacket message = new DatagramPacket(buffer, packet.getLength() + ReliablePacket.HEADER_LENGTH);
        socket.receive(message);
        System.arraycopy(buffer, ReliablePacket.HEADER_LENGTH, packet.getBuffer(), 0, packet.getLength());
        packet.setLength(Math.min(packet.getLength(), message.getLength()));
        packet.setAddress(message.getAddress());
        packet.setPort(message.getPort());
        DatagramPacket confirmation = new DatagramPacket(buffer, ReliablePacket.HEADER_LENGTH, message.getAddress(), message.getPort());
        buffer[0] = ReliablePacket.TYPE_CONFIRMATION;
        socket.send(confirmation);
    }
}
