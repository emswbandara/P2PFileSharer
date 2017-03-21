package org.uomcse.cs4262;


public class BootstrapClient {

    private String node_ip, node_port, username, bs_ip, bs_port;
    private static BootstrapClient bootstrapClient;

    public static BootstrapClient getInstance(){

        if(bootstrapClient == null){
            bootstrapClient = new BootstrapClient();
        }
        return bootstrapClient;

    }
    private BootstrapClient() {

        Config config = Config.getInstance();
        node_ip = config.getProperty(UDPProtocol.LOCAL_NODE_HOST);
        node_port = config.getProperty(UDPProtocol.LOCAL_NODE_PORT);
        username = config.getProperty(UDPProtocol.LOCAL_NODE_USERNAME);
        bs_ip = config.getProperty(UDPProtocol.BOOTSTRAPSERVER_IP);
        bs_port = config.getProperty(UDPProtocol.BOOTSTRAPSERVER_PORT);

    }

    public String[] register(){

        String message = MessageProcessor.createMessage(node_ip, node_port, username, UDPProtocol.REGISTER_COMMAND);


            String response = UDPClient.sendMessage(bs_ip, bs_port, message, true);
                System.out.println("Sent message :"+ message);
                System.out.println("Received Response :" + response);

            return MessageProcessor.parseResponse(response);


    }


    public boolean unregister() {

        String message = MessageProcessor.createMessage(node_ip, node_port, username, UDPProtocol.UNREGISTER_COMMAND);

            String response = UDPClient.sendMessage(bs_ip, bs_port, message, true);
                System.out.println("Sent Command: "+ message);
                System.out.println("Received Response: " + response);
            return true;


    }

}
