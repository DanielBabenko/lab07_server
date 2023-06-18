package server.manager;

import server.object.LabWork;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

    private final static int PORT = 50028;
    DatagramSocket serverSocket;

    private InetAddress senderAddress;
    private int senderPort;

    public Server() throws IOException {
        this.serverSocket = new DatagramSocket(PORT);
    }

    public void sentToClient(String data) throws IOException {
        byte[] sendingDataBuffer;

        //  sent to client result
        sendingDataBuffer = data.getBytes();


        // create a new udp packet
        DatagramPacket outputPacket = new DatagramPacket(
                sendingDataBuffer, sendingDataBuffer.length,
                getSenderAddress(), getSenderPort());

        // send packet to client
        serverSocket.send(outputPacket);

    }


    public void sentToClient(byte[] data) throws IOException {
        byte[] sendingDataBuffer;

        //  sent client result
        sendingDataBuffer = data;


        // create a new udp packet
        DatagramPacket outputPacket = new DatagramPacket(
                sendingDataBuffer, sendingDataBuffer.length,
                getSenderAddress(), getSenderPort());

        // send packet to client
        serverSocket.send(outputPacket);

    }

    public String dataFromClient() throws IOException {

        boolean flag = false;
      //  System.out.println("waiting for a client to connect: ");
        while (!flag) {
            byte[] receivingDataBuffer = new byte[1024];
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            // give information from client
            serverSocket.receive(inputPacket);

            String receivedData = new String(inputPacket.getData()).trim();

            if (!receivedData.isEmpty()) {

                setSenderAddress(inputPacket.getAddress());
                setSenderPort(inputPacket.getPort());


                System.out.println("Sent from client: " + receivedData);
                return receivedData;
            }
        }

        return "";
    }

    public LabWork getObjectFromClient() throws IOException, ClassNotFoundException {
      //  System.out.println("waiting for a client to get OBJECT LABWORK: ");
            byte[] receivingDataBuffer = new byte[1024];
            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
            // give information from client
            serverSocket.receive(inputPacket);


            setSenderAddress(inputPacket.getAddress());
            setSenderPort(inputPacket.getPort());


            return  SerializationManager.deserializeObject(inputPacket.getData());

    }




    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public InetAddress getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(InetAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    public static int getPORT() {
        return PORT;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public int getSenderPort() {
        return senderPort;
    }
}