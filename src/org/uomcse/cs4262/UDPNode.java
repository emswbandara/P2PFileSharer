package org.uomcse.cs4262;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;


public class UDPNode extends Thread implements Node {

    private final Config config;
    private final HashMap<String, PeerNode> routingTable;
    private static UDPNode instance;
    private FileRepository fileRepository;
    private QueryStatistics statistics;
    private ArrayList<Integer> queries;
    private int messageCount, forwardedMessageCount, answeredMessageCount;

    public static UDPNode getInstance() {
        if (instance == null) {
            instance = new UDPNode();
        }
        return instance;
    }

    private UDPNode(){

        config = Config.getInstance();
        routingTable = new HashMap<String, PeerNode>();
        fileRepository = FileRepository.getInstance();
        queries = new ArrayList<>();
        statistics = new QueryStatistics();
        messageCount = 0;
        forwardedMessageCount = 0;
        answeredMessageCount = 0;

    }

    public void addNodeToRoutingTable(String host, String port){
        routingTable.put(host+" "+port, new PeerNode(host, port, 0, 1, 1));
    }

    public void updateNodeToRoutingTable(PeerNode node) {
        routingTable.put(node.getPeerHost()+" "+node.getPeerPort(), node);
    }

    public void removeNodeFromRoutingTable(String host, String port){
        routingTable.remove(host+" "+port);
    }

    public String getIP(){
        return config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
    }

    public String getPort(){
        return config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
    }

    @Override
    public void run() {

        String host = getIP();
        String port = getPort();

        try {

            final DatagramSocket mySocket = new DatagramSocket(Integer.parseInt(port));



            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    //When shutting down close the UDP Socket and BufferedReader
                    printNodeStatistics();
                    mySocket.close();
                }
            });

            while (true) {

                byte[] data = new byte[256];
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                mySocket.receive(datagramPacket);
                byte[] receivedData = datagramPacket.getData();
                String receivedString = new String(receivedData);
                System.out.println("Received Message :" + receivedString);
                String[] messageElements = receivedString.split("\\s+");
                int length = Integer.parseInt(messageElements[0].trim());
                String command = messageElements[1].trim();

                messageElements = Arrays.copyOfRange(messageElements, 2, messageElements.length);


                switch (command) {
                    case "JOIN":
                        String peerIP = messageElements[0];
                        String peerPort = messageElements[1];
                        addNodeToRoutingTable(peerIP, peerPort);
                        StringBuilder joinResponse = new StringBuilder(UDPProtocol.JOIN_OK_COMMAND+" "+0);


                        int joinResponseLength = joinResponse.length();
                        joinResponse.insert(0, String.format("%04d", joinResponseLength) + " ");
                        System.out.println("Sending JOINOK Message: " + joinResponse);
                        UDPClient.sendMessage(peerIP, peerPort, joinResponse.toString(), false);
                        break;

                    case "JOINOK":
                        break;

                    case "LEAVE":
                        String leavepeerIP = messageElements[0];
                        String leavepeerPort = messageElements[1];
                        removeNodeFromRoutingTable(leavepeerIP, leavepeerPort);
                        break;

                    case "SER":
                        messageCount++;
                        try {
                            int queryId = Integer.parseInt(messageElements[0]);
                            int hopCount = Integer.parseInt(messageElements[4].trim());
                           // statistics.incrementMessageCount(queryId);
                           // statistics.updatetNodeDegree(queryId, routingTable.size());

                            if (!queries.contains(queryId) && hopCount<=10 && hopCount>=0) {
                                String queryHost = messageElements[1];
                                String queryPort = messageElements[2];
                                String fileName = messageElements[3];

                                List<String> queryHits = this.fileRepository.search(fileName.replaceAll("_", " "));

                                if (!queryHits.isEmpty()) {
                                    queries.add(queryId);
                                    answeredMessageCount++;
                                    System.out.println("************Local node contains matching files************");
                                    for (String s : queryHits) {
                                        System.out.println(s);
                                    }
                                    System.out.println("**********************************************************");

                                    String fileStr = "";
                                    for (String s : queryHits) {
                                        fileStr = fileStr + s.replaceAll("\\s+", "_") + " ";
                                    }


                                    StringBuilder responseCommand = new StringBuilder(UDPProtocol.SEARCH_OK_COMMAND + " " + queryId + " " +
                                            queryHits.size() + " " + host + " " + port + " " + (hopCount - 1) + " " + fileStr.trim());
                                    int responsecommandLength = responseCommand.length();
                                    responseCommand.insert(0, String.format("%04d", responsecommandLength) + " ");
                                    System.out.println("Sent SEROK Message: " + responseCommand);
                                    UDPClient.sendMessage(queryHost, queryPort, responseCommand.toString(), false);
                                } else {
                                    forwardedMessageCount++;
                                    System.out.println("No matches found in Local Node"
                                            + "\nPassing the message to peers...");
                                    directedBFS(queryId, queryHost, Integer.parseInt(queryPort), fileName, hopCount - 1);
                                }
                            }
                        }catch (NumberFormatException ex){
                            System.out.println("Invalid message. "+ ex);
                        }
                        break;

                    case "SEROK":
                        String[] temp = receivedString.split("\\s+");
                        String key = temp[4] + " " + temp[5];

                        if (routingTable.containsKey(key)) {
                            PeerNode node = routingTable.get(key);
                            node.setQueryResultsReturned();
                            updateNodeToRoutingTable(node);
                        } else {
                            addNodeToRoutingTable(temp[4], temp[5]);
                        }

                        System.out.println("*******************Search Results******************");

                        System.out.println("Query ID: "+ temp[2]);
                        System.out.println("Host: "+ temp[4]);
                        System.out.println("Port: "+ temp[5]);
                        System.out.println("Hop count: "+ temp[6]);

                        for (int i = 7; i < temp.length; i++) {
                            System.out.println(" * " + temp[i].replaceAll("_", " "));
                        }

                        statistics.updateQuery(Integer.parseInt(temp[2]), System.currentTimeMillis(), Integer.parseInt(temp[6]));
                        System.out.println();
                        System.out.println("****************************************************");




                         break;
                    default:
                            System.out.println("Invalid Message :"+ receivedString);

                        break;
                }

                }

        } catch (IOException ex) {
            //ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void startNode() {
        this.joinNetwork();
        this.start();
    }


    @Override
    public void directedBFS(int queryId, String host, int port, String query, int hops) {

        queries.add(queryId);
        statistics.incrementMessageCount(queryId);
        statistics.updatetNodeDegree(queryId, routingTable.size());
        StringBuilder command = new StringBuilder("SER " + queryId + " " + host + " " + port + " " + query + " " + hops);
        int length = command.length();
        command.insert(0, String.format("%04d", length) + " ");
        PeerNode[] peers = getBestNeighbors();
        if (hops > 0 && peers!=null) {

            try {
                for (PeerNode node : peers) {
                    System.out.println(node.getPeerHost());
                    String peerHost = node.getPeerHost();
                    String peerPort = node.getPeerPort();
                    String key = peerHost + " " + peerPort;
                    if (!key.equals(host + " " + port)) {
                        node.setMessageCount();
                        updateNodeToRoutingTable(node);
                        System.out.println("Sent SER message to Host: " + peerHost + " Port: " + peerPort + " Message: " + command.toString());
                        UDPClient.sendMessage(peerHost, peerPort, command.toString(), false);
                    }
                }
            }catch (NullPointerException ex){

            }
        }
    }


    @Override
    public void joinNetwork() {

        String host = config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
        String port = config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
        String username = config.getProperty(UDPProtocol.LOCAL_NODE_USERNAME);

        String message = MessageProcessor.createMessage(host, port, username, UDPProtocol.JOIN_COMMAND);

        for (PeerNode peer : routingTable.values()) {

            String peerIP =  peer.getPeerHost();
            String peerPort = peer.getPeerPort();

            UDPClient.sendMessage(peerIP, peerPort, message, false);
            System.out.println("JOIN request to host: "+ peerIP+" port: "+ peerPort);

        }

    }

    @Override
    public void leaveNetwork() {

        String host = config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
        String port = config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
        String username = config.getProperty(UDPProtocol.LOCAL_NODE_USERNAME);

        String message = MessageProcessor.createMessage(host, port, username, UDPProtocol.LEAVE_COMMAND);

        for (PeerNode peer : routingTable.values()) {

            String peerIP =  peer.getPeerHost();
            String peerPort = peer.getPeerPort();

            UDPClient.sendMessage(peerIP, peerPort, message, false);
            System.out.println("LEAVE request to host: "+ peerIP+" port: "+ peerPort);

        }

    }

    public PeerNode[] getBestNeighbors() {
        PeerNode[] neighbors;
        int rank1 = 0;
        int rank2 = 0;
        Iterator it = routingTable.values().iterator();
        if (routingTable.size() == 0) {
            System.out.println("No peers found");
            return null;
        } else if (routingTable.size() == 1) {
            neighbors = new PeerNode[1];
            neighbors[0] = (PeerNode) it.next();
            return neighbors;
        } else {
            neighbors = new PeerNode[2];
            while (it.hasNext()) {
                PeerNode peer = (PeerNode) it.next();

                if (rank1 <= peer.getRank()) {
                    rank2 = rank1;
                    neighbors[1] = neighbors[0];
                    rank1 = peer.getRank();
                    neighbors[0] = peer;
                } else if (rank2 < peer.getRank()) {
                    rank2 = peer.getRank();
                    neighbors[1] = peer;
                }

            }
        }
        return neighbors;
    }

    public FileRepository getFileRepository() {
        return fileRepository;
    }

    public HashMap<String, PeerNode> getRoutingTable() {
        return routingTable;
    }

    public QueryStatistics getQueryStatistics(){
        return this.statistics;
    }

    public void printRoutingTable() {

        if(routingTable.isEmpty()){
            System.out.println("No peers in the Routing Table");
        }
        else{
            System.out.println("*********************Peers*********************");
            int count = 1;
            for(String s : routingTable.keySet()){
                System.out.println("Peer "+ count +": "+ s);
                count++;
            }
            System.out.println("***********************************************");
        }
    }

    public void printFileRepository() {

        fileRepository.print();
    }

    public void printNodeStatistics(){
        System.out.println("*******************"+getIP()+": "+getPort()+"*******************");
        System.out.println("No of total queries received: "+ messageCount);
        System.out.println("No of queries answered: "+ answeredMessageCount);
        System.out.println("No of queries forwarded: "+forwardedMessageCount);
        System.out.println("No of entries in the routing table: " + this.routingTable.size());
        System.out.println("****************************************************************");

        statistics.printStats();


    }
}
