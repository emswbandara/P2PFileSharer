package org.uomcse.cs4262;

import java.util.HashSet;

/**
 * Created by sathya on 12/26/16.
 */
public class UDPNode extends Thread implements Node {

    private final Config config;
    private final HashSet<String> peers;
    private static UDPNode instance;

    public static UDPNode getInstance() {
        if (instance == null) {
            instance = new UDPNode();
        }
        return instance;
    }

    private UDPNode(){

        config = Config.getInstance();
        peers = new HashSet<String>();

    }

    public void addNode(String node){
        peers.add(node);
    }

    public void removeNode(String node){
        peers.remove(node);
    }

    public String getIP(){
        return config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
    }

    public String getPort(){
        return config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
    }

    @Override
    public void startNode() {

    }

    @Override
    public void forwardQuery(int queryId, String ip, int port, String input, int i) {

    }

    @Override
    public void joinNetwork() {

        String host = config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
        String port = config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
        String username = config.getProperty(UDPProtocol.LOCAL_NODE_USERNAME);

        String message = MessageProcessor.createMessage(host, port, username, UDPProtocol.JOIN_COMMAND);

        for (String peer : peers) {

            String[] peerInfo = peer.split(" ");
            String peerIP =  peerInfo[0];
            String peerPort = peerInfo[1];
            UDPClient udpClient = new UDPClient();

            udpClient.sendMessage(peerIP, peerPort, message, false);
            System.out.println("JOIN request to host: "+ peerIP+" port: "+ peerPort);

        }

    }

    @Override
    public void leaveNetwork() {

        String host = config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
        String port = config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
        String username = config.getProperty(UDPProtocol.LOCAL_NODE_USERNAME);

        String message = MessageProcessor.createMessage(host, port, username, UDPProtocol.LEAVE_COMMAND);

        for (String peer : peers) {

            String[] peerInfo = peer.split(" ");
            String peerIP =  peerInfo[0];
            String peerPort = peerInfo[1];
            UDPClient udpClient = new UDPClient();

            udpClient.sendMessage(peerIP, peerPort, message, false);
            System.out.println("LEAVE request to host: "+ peerIP+" port: "+ peerPort);

        }

    }
}
