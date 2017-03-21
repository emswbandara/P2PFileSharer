package org.uomcse.cs4262;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Console extends Thread {

    private UDPNode node;

    public Console(UDPNode node) {
        this.node = node;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {

            System.out.println("******************P2P File Sharing System******************");
            System.out.println(" Content Search: ");
            System.out.println("Type any keyword of the file you want to search\n");
            System.out.println(" List of other commands available: ");
            System.out.println("peers : print local node routing table details");
            System.out.println("list  : print local node file list");
            System.out.println("test  : run the performance test");
            System.out.println("quit  : gracefully shut down the node");
            System.out.println("***********************************************************");


            while (true) {
                String input = in.readLine();
                if (input.trim().equals("quit")) {
                    System.out.println("Shutting down the node...");
                    System.exit(0);
                } else if (input.trim().equals("peers")) {
                    node.printRoutingTable();
                } else if (input.trim().equals("list")) {
                    node.printFileRepository();
                } else if (input.trim().equals("test")){
                    executeQueries(node);

                } else if (input.length() > 1) {
                    //  Main.queryStartedTime = System.currentTimeMillis();

                    List<String> queryHits = node.getFileRepository().search(input.trim());
                    if(queryHits.size() > 0){
                        System.out.println("************Local node contains matching files************");
                        for (String s : queryHits) {
                            System.out.println(s);
                        }
                        System.out.println("**********************************************************");
                    }
                    else {
                        System.out.println("No matches found in Local Node"
                                + "\nPassing the message to peers...");

                        //int hashCode = (node.getIP()+node.getPort()).hashCode();
                       // String queryId = hashCode + "" +(int)(10000 * Math.random());
                        int queryId = (int)(10000 * Math.random());
                        node.getQueryStatistics().addQuery(queryId, input.trim(), System.currentTimeMillis(), Main.hopCount);
                        node.directedBFS(queryId, node.getIP(), Integer.parseInt(node.getPort()), input.trim().replaceAll(" ", "_"), Main.hopCount);

                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void executeQueries(UDPNode node) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("resources/queries.csv"));
            String temp = reader.readLine();
            temp = reader.readLine();
            while (temp != null && !temp.isEmpty()) {
                String queryIdStr = (Config.getIteration().trim()).concat(temp.split(",")[0].trim());
                int queryId = Integer.parseInt(queryIdStr);
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

                Thread.sleep(1000);
                temp = reader.readLine();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
