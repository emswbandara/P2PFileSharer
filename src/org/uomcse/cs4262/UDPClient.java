package org.uomcse.cs4262;


import java.io.IOException;
import java.net.*;

/**
 * Created by sathya on 12/26/16.
 */
public class UDPClient {


    public static String sendMessage(String ip, String portStr, String message, boolean receiveResponse){

        String response = null;
        DatagramSocket datagramSocket = null;

        try {
            datagramSocket = new DatagramSocket();
            InetAddress ipaddress = InetAddress.getByName(ip);
            int port = Integer.parseInt(portStr);
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipaddress, port);
            datagramSocket.send(sendPacket);
            if(receiveResponse){
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.receive(receivePacket);
                response = new String(receivePacket.getData());
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        finally {
            datagramSocket.close();
        }

        return response;

    }

}
