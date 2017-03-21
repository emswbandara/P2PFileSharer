package org.uomcse.cs4262;


public interface Node {

    public abstract void startNode();
    public abstract void directedBFS(int queryId, String host, int port, String query, int hops);
    public abstract void joinNetwork();
    public abstract void leaveNetwork();

}
