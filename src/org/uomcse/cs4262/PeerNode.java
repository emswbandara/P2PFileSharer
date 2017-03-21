package org.uomcse.cs4262;


public class PeerNode {

    private String peerHost;
    private String peerPort;
    private int queryResultsReturned;
    private int hopCount;
    private int messageCount;
    private int messageQueueSize;

    public PeerNode(String peerHost, String peerPort, int queryResultsReturned, int messageCount, int hopCount) {
        this.peerHost = peerHost;
        this.peerPort = peerPort;
        this.queryResultsReturned = queryResultsReturned;
        this.messageCount = messageCount;
        this.hopCount = hopCount;

    }

    public String getPeerHost() {
        return peerHost;
    }

    public void setPeerHost(String peerHost) {
        this.peerHost = peerHost;
    }

    public String getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(String peerPort) {
        this.peerPort = peerPort;
    }

    public int getQueryResultsReturned() {
        return queryResultsReturned;
    }

    public void setQueryResultsReturned() {
        queryResultsReturned++;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount() {
        messageCount++;
    }

    public int getMessageQueueSize() {
        return messageQueueSize;
    }

    public void setMessageQueueSize(int messageQueueSize) {
        this.messageQueueSize = messageQueueSize;
    }

    public void incrementQueryResultsReturned(){
        this.queryResultsReturned++;
    }

    public int getRank(){

        int rank = queryResultsReturned/ (messageCount * hopCount);
        return rank;
    }


}
