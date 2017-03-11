package com.versioneye.utils.properties;

import java.io.*;
import java.util.Properties;

/**
 * Methods to deal with properties files.
 */
public class PropertiesUtil {

    private PropertiesUtil() {
        // Util methods for properties
    }

    public static Properties readPropertyFile(String filePath) throws Exception {
        Properties properties = new Properties();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        properties.load(inputStream);
        return properties;
    }

}
