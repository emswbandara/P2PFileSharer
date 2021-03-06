package org.uomcse.cs4262;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Config {

    private static final String PROPERTY_FILE="/resources/config.properties";
    private Properties properties = null;
    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private Config() {
            properties = new Properties();
            InputStream propertiesStream = Config.class.getResourceAsStream(PROPERTY_FILE);
            if (propertiesStream != null) {
                try {
                    properties.load(propertiesStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("file not found");
            }

    }

    public String getProperty(String key) {

        if(this.properties.containsKey(key)){
            String value = this.properties.getProperty(key);
            return value;
        }
        else
            return null;

    }

    public static String getIteration(){
        RefreshingProperties p = null;
        try {
            p = new RefreshingProperties(new File("resources/config.properties"));
            String iteration = p.getProperty("node.iteration");
            return iteration;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }




}
