package org.uomcse.cs4262;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by sathya on 1/21/17.
 */
public class QueryStatistics {

    class Query{
        String queryTerm;
        long queryStartTime = 0;
        long queryEndTime = 0;
        long elapsedTime = 0;
        int hopCount = 0;
        int totalMessagesPassed = 0;
        int nodeDegree = 0;
    }

    HashMap<Integer, Query> stats = null;

    public QueryStatistics(){
        stats = new HashMap<>();
    }

    public void addQuery(int queryID, String query, long startTime, int hopCount){
        Query q = new Query();
        q.queryTerm = query;
        q.queryStartTime = startTime;
        q.hopCount = hopCount;
        stats.put(queryID, q);
    }

    public void updateQuery(int queryID, long endTime, int hops){

        Query q = stats.get(queryID);
        if(q != null){
            long elapsedTime = endTime - q.queryStartTime;
            System.out.println("Elapsed Time: " + elapsedTime+" ms");
            if(q.elapsedTime==0|| q.elapsedTime>elapsedTime){
                q.queryEndTime = endTime;
                q.elapsedTime = elapsedTime;
                q.hopCount = (q.hopCount - hops);
                stats.put(queryID, q);
            }
        }

    }

    public void incrementMessageCount(int queryID){

        Query q = stats.get(queryID);
        if(q != null){
            q.totalMessagesPassed++;
            stats.put(queryID, q);
        }
        else{
            Query query = new Query();
            query.totalMessagesPassed++;
            stats.put(queryID, query);
        }

    }

    public void updatetNodeDegree(int queryID, int degree){

        Query q = stats.get(queryID);
        if(q != null){
            q.nodeDegree = degree;
            stats.put(queryID, q);
        }
        else{
            Query query = new Query();
            query.nodeDegree = degree;
            stats.put(queryID, query);
        }

    }


    public void printStats(){

        String file = Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_HOST)+"_"+ Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_PORT)+"Performancestats.csv";
        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            out.println(Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_HOST)+": "+ Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_PORT));
            out.println("queryID,messagesPassed,NodeDegree");
            for(Integer i : stats.keySet()){
                Query q = stats.get(i);
                out.println(i + "," + q.totalMessagesPassed + "," + q.nodeDegree);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printQueryStats(){
        
        try(FileWriter fw = new FileWriter("querysearchresults.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {

            out.println(Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_HOST)+": "+ Config.getInstance().getProperty(UDPProtocol.LOCAL_NODE_PORT));
            out.println("queryID,ElapsedTime,HopCount");
            for(Integer i : stats.keySet()){
                Query q = stats.get(i);
                out.println(i + "," + q.elapsedTime + "," + q.hopCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
