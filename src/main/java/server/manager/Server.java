package server.manager;

import server.object.LabWork;

import java.io.IOException;
import java.io.StreamCorruptedException;
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
        private ForkJoinPool forkJoinPool;

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

        public class ForkPooler extends RecursiveTask<String> {
            private String dataClient;
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
                        dataClient = receivedData;
                        return receivedData;
                    }
                }
                return "";
            }

        }

     public class ForkPoolerObject extends RecursiveTask<LabWork> {

         @Override
         protected LabWork compute() {
             byte[] receivingDataBuffer = new byte[1024];
             DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
             // give information from client
             try {
                 serverSocket.receive(inputPacket);
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }


             setSenderAddress(inputPacket.getAddress());
             setSenderPort(inputPacket.getPort());


             try {
                 return SerializationManager.deserializeObject(inputPacket.getData());
             } catch (IOException e) {
                 throw new RuntimeException(e);
             } catch (ClassNotFoundException e) {
                 throw new RuntimeException(e);
             }
         }
     }

        public String dataFromClient() throws IOException {
            ForkJoinPool forkJP = new ForkJoinPool();
            return forkJP.invoke(new ForkPooler());
//        // fork join pool
//            boolean flag = false;
//            while (!flag) {
//                byte[] receivingDataBuffer = new byte[1024];
//                DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
//                // give information from client
//                try {
//                    serverSocket.receive(inputPacket);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                String receivedData = new String(inputPacket.getData()).trim();
//
//                if (!receivedData.isEmpty()) {
//
//                    setSenderAddress(inputPacket.getAddress());
//                    setSenderPort(inputPacket.getPort());
//
//
//                    System.out.println("Sent from client: " + receivedData);
//                    return receivedData;
//                }
//            }
//            return "";
        }


        public LabWork getObjectFromClient() throws IOException, ClassNotFoundException {
            //  System.out.println("waiting for a client to get OBJECT LABWORK: ");
            ForkJoinPool forkJP = new ForkJoinPool();
            return forkJP.invoke(new ForkPoolerObject());
//            byte[] receivingDataBuffer = new byte[1024];
//            DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
//            // give information from client
//            serverSocket.receive(inputPacket);
//
//
//            setSenderAddress(inputPacket.getAddress());
//            setSenderPort(inputPacket.getPort());
//
//
//            return SerializationManager.deserializeObject(inputPacket.getData());

        }


        //


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