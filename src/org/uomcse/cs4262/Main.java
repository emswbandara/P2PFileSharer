package org.uomcse.cs4262;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static int hopCount = 10;

    public static void main(String[] args) throws InterruptedException {
	// write your code here
        Main main = new Main();
        final BootstrapClient bootstrapClient = BootstrapClient.getInstance();
        final UDPNode node = (UDPNode)NodeFactory.getNode(Config.getInstance().getProperty("node.type"));

        String[] peers = bootstrapClient.register();
        if (peers != null) {
            for (int i = 0; i < peers.length; i++) {
                String[] nodeInfo = peers[i].split(" ");
                node.addNodeToRoutingTable(nodeInfo[0], nodeInfo[1]);

            }
        }


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                node.getQueryStatistics().printQueryStats();
                bootstrapClient.unregister();
                node.leaveNetwork();
            }
        });
        node.startNode();
       // node.join();
        final Console consoleReader = new Console(node);
        consoleReader.start();


       // main.executeQueries(node);

        node.join();
        consoleReader.join();


    }


    public void executeQueries(UDPNode node) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("resources/queries.csv"));
            String temp = reader.readLine();
            temp = reader.readLine();
            while (temp != null && !temp.isEmpty()) {
                String queryIdStr = Config.getInstance().getProperty("node.iteration").trim()+temp.split(",")[0].trim();
                int queryId = Integer.parseInt(queryIdStr);
                System.out.println(queryId);
                String query = temp.split(",")[1].trim();
                System.out.println("*********Executing query:" + queryId + ": " + query + "*********");
                //Query for a file
                node.getQueryStatistics().addQuery(queryId, query, System.currentTimeMillis(), Main.hopCount);
                List<String> queryHits = node.getFileRepository().search(query);

                if (!queryHits.isEmpty()) {
                    node.getQueryStatistics().updateQuery(queryId, System.currentTimeMillis(), Main.hopCount);

                } else {
                    String ip = node.getIP();
                    int port = Integer.parseInt(node.getPort());
                    node.directedBFS(queryId, ip, port, query.replaceAll(" ", "_"), Main.hopCount);
                }

                Thread.sleep(2000);
                temp = reader.readLine();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
