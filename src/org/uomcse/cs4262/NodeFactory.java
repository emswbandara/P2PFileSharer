package org.uomcse.cs4262;

/**
 * Created by sathya on 12/26/16.
 */
public class NodeFactory {

    public static Node getNode(String criteria)
    {
        if ( criteria.equals("udp") )
            return UDPNode.getInstance();
        else if ( criteria.equals("rpc") )
            return null;

        return null;
    }
}
