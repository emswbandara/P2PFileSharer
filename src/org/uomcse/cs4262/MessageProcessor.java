package org.uomcse.cs4262;

import java.util.Arrays;


public class MessageProcessor {


    public static String createMessage(String node_ip, String node_port, String username, String type){

        StringBuilder command = new StringBuilder(type.toUpperCase()+" " + node_ip + " " + node_port + " " + username);
        int length = command.length();
        command.insert(0, String.format("%04d", length) + " ");

        return command.toString();
    }


    public static String[] parseResponse(String response) {

        //String dummy = "0051 REGOK 2 129.82.123.45 5001 64.12.123.190 34001";
        String[] oldArray = response.split(" ");
        String code = oldArray[2].trim();
        int length = Integer.parseInt(code);

        switch (length){
            case 0:
                System.out.println("request is successful, no nodes in the system. Status code: "+ length);
                return null;
            case 9999:
                System.out.println("failed, there is some error in the command. Status code: "+ length);
                return null;
            case 9998:
                System.out.println(" failed, already registered to you, unregister first. Status code: "+ length);
                return null;
            case 9997:
                System.out.println("failed, registered to another user, try a different IP and port. Status code: "+ length);
                return null;
            case 9996:
                System.out.println("failed, can’t register. BS full. Status code: "+ length);
                return null;
            case 1: {
                System.out.println("request is successful, 1 or 2 node contacts will be returned");
                String[] peers = new String[length];
                String[] newArray = Arrays.copyOfRange(oldArray, 3, oldArray.length);

                for(int i=0; i<length; i++){
                    peers[i] = newArray[i*2] + " " + newArray[i*2 + 1];
                }

                return peers;
            }
            case 2: {
                System.out.println("request is successful, 1 or 2 node contacts will be returned");
                String[] peers = new String[length];
                String[] newArray = Arrays.copyOfRange(oldArray, 3, oldArray.length);

                for(int i=0; i<length; i++){
                    peers[i] = newArray[i*2] + " " + newArray[i*2 + 1];
                }

                return peers;
            }
            default:
                return null;
        }


    }
}
