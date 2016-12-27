package org.uomcse.cs4262;

/**
 * Created by sathya on 12/26/16.
 */
public interface Node {

    public abstract void startNode();
    public abstract void forwardQuery(int queryId, String ip, int port, String input, int i);
    public abstract void joinNetwork();
    public abstract void leaveNetwork();

}
