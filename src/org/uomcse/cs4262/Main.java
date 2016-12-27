package org.uomcse.cs4262;

public class Main {

    public static void main(String[] args) {
	// write your code here
        final BootstrapClient bootstrapClient = BootstrapClient.getInstance();
        String[] peers = bootstrapClient.register();
        if (peers != null) {
            for (int i = 0; i < peers.length; i++) {
                //routingTable.addNode(nodes[i]);
                System.out.println(peers[i]);
            }
        }


    }
}
