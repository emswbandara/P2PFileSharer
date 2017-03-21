package org.uomcse.cs4262;


import java.io.IOException;
import java.net.*;


public class UDPClient {


    public static String sendMessage(String ip, String portStr, String message, boolean receiveResponse){

        String response = null;
        DatagramSocket datagramSocket = null;

        try {
            datagramSocket = new DatagramSocket();
            InetAddress ipaddress = InetAddress.getByName(ip);
            int port = Integer.parseInt(portStr.trim());
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[65536];
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipaddress, port);
            datagramSocket.send(sendPacket);
            if(receiveResponse){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.receive(receivePacket);
                response = new String(receivePacket.getData());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(datagramSocket != null)
                datagramSocket.close();
        }

        return response;

    }

}
