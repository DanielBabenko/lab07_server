package server.manager;

import server.object.LabWork;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Server {

    private final static int PORT = 50097;
    DatagramSocket serverSocket;

    private InetAddress senderAddress;
    private int senderPort;

    public Server() throws IOException {
        this.serverSocket = new DatagramSocket(PORT);
    }

    public void sentToClient(String data) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                byte[] sendingDataBuffer;

                //  sent to client result
                sendingDataBuffer = data.getBytes();


                // create a new udp packet
                DatagramPacket outputPacket = new DatagramPacket(
                        sendingDataBuffer, sendingDataBuffer.length,
                        getSenderAddress(), getSenderPort());

                // send packet to client
                serverSocket.send(outputPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public void sentToClient(byte[] data) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                byte[] sendingDataBuffer;

                //  sent client result
                sendingDataBuffer = data;


                // create a new udp packet
                DatagramPacket outputPacket = new DatagramPacket(
                        sendingDataBuffer, sendingDataBuffer.length,
                        getSenderAddress(), getSenderPort());

                // send packet to client
                serverSocket.send(outputPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public String dataFromClient() throws IOException {
//        // fork join pool

        class GetDataViaForkJoinPool extends RecursiveTask<String> {
            @Override
            protected String compute() {
                boolean flag = false;
                while (!flag) {
                    byte[] receivingDataBuffer = new byte[1024];
                    DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                    // give information from client
                    try {
                        serverSocket.receive(inputPacket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

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
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        GetDataViaForkJoinPool getDataViaForkJoinPool = new GetDataViaForkJoinPool();

        return forkJoinPool.invoke(getDataViaForkJoinPool);

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