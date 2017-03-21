package org.uomcse.cs4262;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileRepository {

    private static FileRepository instance;

    private HashSet<String> localFiles;

    public static FileRepository getInstance(){
        if(instance==null){
            instance = new FileRepository();
        }
        return instance;
    }

    private FileRepository(){

        BufferedReader br = null;
        FileReader fr = null;
        localFiles = new HashSet<>();

        try {
            //Initialise node's file repository with 3 - 5 random files from fileNames file
            fr = new FileReader("resources/FileNames.txt");
            br = new BufferedReader(fr);
            ArrayList<String> fileNames = new ArrayList<String>();
            String currentLine;
            while((currentLine = br.readLine()) != null){
                fileNames.add(currentLine.trim());
            }

            int numOfFiles = ThreadLocalRandom.current().nextInt(3, 5 + 1);

            //removing 15 random files to get 5 remaining random files.
            int count =0;
            while (count < numOfFiles){
                int randomFileIndex = ThreadLocalRandom.current().nextInt(0, fileNames.size());
                String selectedFile = fileNames.get(randomFileIndex);

                if(!localFiles.contains(selectedFile)){
                    localFiles.add(selectedFile);
                    count++;
                }
            }

            fileNames.clear();

        } catch (FileNotFoundException ex) {
                Logger.getLogger(FileRepository.class.getName()).log(Level.SEVERE, null, ex);
            //System.exit(-1);
        } catch (IOException ex) {
                Logger.getLogger(FileRepository.class.getName()).log(Level.SEVERE, null, ex);
            //  System.exit(-1);
        }
        finally {
            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

    public List<String> search(String query){

        ArrayList<String> matchingFiles =  new ArrayList<String>();
        List<String> queryTerms = Arrays.asList(query.toLowerCase().split("\\s+"));
        List<String> fileTerms;

        for(String fileName : localFiles){

            fileTerms = Arrays.asList(fileName.toLowerCase().split("\\s+"));

            if(fileTerms.containsAll(queryTerms)){
                matchingFiles.add(fileName);
            }
        }

        return matchingFiles;
    }

    public void addFile(String fileName){
        this.localFiles.add(fileName);
    }



    public void print(){

        System.out.println("***************Files in the local node***************");
        int count = 1 ;
        for(String fileName : localFiles){
            System.out.println("File"+ count + ": " +fileName);
            count++;
        }
        System.out.println("*****************************************************");
    }

}
