package GBN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
    private static int portNum;
    private static String fileName;
    private static double probability;
    private static int currentSeqNumber = 0;
    private static File file;
    private static Map<String, Short> packetTypes = null;

    static DatagramSocket serverSocket;
    static
    {
        packetTypes = new HashMap<String, Short>();
        packetTypes.put("Data", (short)21845);
        packetTypes.put("Ack", (short)-21846);
    }

    public static void main(String args[]) throws Exception {
        portNum = Integer.parseInt(args[0]);
        fileName = args[1];
        probability = Double.parseDouble(args[2]);
        serverSocket = new DatagramSocket(portNum);
        file = new File(fileName);
        System.out.println("Server listening on port: "+serverSocket.getLocalPort());
        while (true) {
            byte[] buffer = new byte[204800];
            DatagramPacket receivePacket =new DatagramPacket(buffer, buffer.length);
            serverSocket.receive(receivePacket);
            byte[] receivedData = new byte[receivePacket.getLength()];

            System.arraycopy(receivePacket.getData(), 0, receivedData, 0, receivedData.length);


            processPacket(receivedData, receivePacket);
        }
        //serverSocket.close();
    }

    public static void processPacket(byte[] packet, DatagramPacket receivePacket){
        byte[] headerBytes = new byte[8];
        byte[] dataBytes = new byte[packet.length-8];

        System.arraycopy(packet, 0, headerBytes, 0, 8);
        System.arraycopy(packet, 8, dataBytes, 0, packet.length-8);

        byte[] seqNum = new byte[4];
        byte[] checkSum = new byte[2];
        byte[] packetType = new byte[2];
        System.arraycopy(packet, 0, seqNum, 0, 4);
        System.arraycopy(packet, 4, checkSum, 0, 2);
        System.arraycopy(packet, 6, packetType, 0, 2);

        int seqNumInInt = Header.byteArrayToInt(seqNum);
        short checkSumInShort = Header.byteArrayToShort(checkSum);
        short packTypeInShort = Header.byteArrayToShort(packetType);

        short dataCheckSum =  Header.calculateCheckSum(dataBytes);
        //**System.out.println("Received "+seqNumInInt);*8
        boolean isValid = validateData(seqNumInInt, checkSumInShort, packTypeInShort, dataCheckSum);
        if(isValid) {

            Random rand = new Random();
            int randomNumber = rand.nextInt(100); // 0-9.
            double randomProb = (double)randomNumber/100;
            if(randomProb >= probability) {
                currentSeqNumber++;
                //Add to file
                appendToFile(dataBytes);
                //Send Response
                sendAcknowledgement(receivePacket);
                //**System.out.println("Sent "+seqNumInInt+" "+currentSeqNumber);**
            } else {
                System.out.println("Packet loss, sequence number = "+seqNumInInt);
                //**System.out.println("Packet dropped: "+seqNumInInt+" "+randomProb+" "+currentSeqNumber);**
            }
        }
    }

    public static boolean validateData(int seqNumInInt, short checkSumInShort, short packTypeInShort, short dataCheckSum) {
        boolean isValid = true;
        if(checkSumInShort != dataCheckSum) {
            isValid = false;
        }
        if(seqNumInInt != currentSeqNumber) {
            isValid = false;
        }
        if(packTypeInShort != packetTypes.get("Data")) {
            isValid = false;
        }
        return isValid;
    }

    public static void sendAcknowledgement(DatagramPacket receivePacket) {
        byte[] seqNumByteStream = Header.intToByteArray(currentSeqNumber);
        byte[] zeroByteStream = Header.shortToByteArray((short)0);
        byte[] packetTypeStream = Header.shortToByteArray(packetTypes.get("Ack"));

        byte[] acknowledgement = new byte[8];
        System.arraycopy(seqNumByteStream, 0, acknowledgement, 0, 4);
        System.arraycopy(zeroByteStream, 0, acknowledgement, 4, 2);
        System.arraycopy(packetTypeStream, 0, acknowledgement, 6, 2);

        DatagramPacket sendPacket = new DatagramPacket(acknowledgement, acknowledgement.length, receivePacket.getAddress(),receivePacket.getPort());
        try {
            //if(currentSeqNumber == 2) {
            serverSocket.send(sendPacket);
            //}
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void appendToFile(byte[] dataBytes) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(new String(dataBytes));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

}
