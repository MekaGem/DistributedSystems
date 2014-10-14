package ru.yandex.udp.reliable;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class ReliablePacket {
    public static final int TYPE_LENGTH = 1;
    public static final int ID_LENGTH = 8;
    public static final int HEADER_LENGTH = TYPE_LENGTH + ID_LENGTH;

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_CONFIRMATION = 1;

    private final Random random = new Random();

    private byte[] buffer;
    private int length;
    private InetAddress address;
    private int port;

    private byte[] datagram;

    public ReliablePacket(byte[] buffer, int length, InetAddress address, int port) {
        this.buffer = buffer;
        this.length = length;
        this.address = address;
        this.port = port;

        datagram = new byte[buffer.length + HEADER_LENGTH];
    }

    public ReliablePacket(byte[] buffer, int length) throws UnknownHostException {
        this(buffer, length, InetAddress.getLocalHost(), 0);
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    DatagramPacket createDatagramPacket() {
        updateDatagram();
        return new DatagramPacket(datagram, length + HEADER_LENGTH, address, port);
    }

    private void updateDatagram() {
        System.arraycopy(buffer, 0, datagram, HEADER_LENGTH, length);
        datagram[0] = TYPE_MESSAGE;
        for (int index = 0; index < 8; ++index) {
            datagram[1 + index] = (byte) random.nextInt(256);
        }
    }
}
