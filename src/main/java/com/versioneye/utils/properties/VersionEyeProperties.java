package com.versioneye.utils.properties;

import com.versioneye.utils.log.Logger;

import java.io.*;
import java.util.Properties;

import static com.versioneye.utils.properties.VersionEyeProperties.PropertyKey.PROJECT_ID;

/**
 * General Version Eye properties
 */
public class VersionEyeProperties {


    public enum PropertyKey {
        API_KEY("api_key", "VERSIONEYE_API_KEY"),
        BASE_URL("base_url", "VERSIONEYE_BASE_URL"),
        PROJECT_ID("project_id", "VERSIONEYE_PROJECT_ID"),

        PROXY_HOST("proxyHost", "VERSIONEYE_PROXY_HOST"),
        PROXY_PORT("proxyPort", "VERSIONEYE_PROXY_PORT"),
        PROXY_USER("proxyUser", "VERSIONEYE_PROXY_USER"),
        PROXY_PASSWORD("proxyPassword", "VERSIONEYE_PROXY_PASSWORD");

        private String key;
        private String systemPropertyKey;

        PropertyKey(String key, String systemPropertyKey) {
            this.key = key;
            this.systemPropertyKey = systemPropertyKey;
        }

        public String getKey() {
            return key;
        }

        public String getSystemPropertyKey() {
            return systemPropertyKey;
        }
    }
    private static final Logger LOGGER = Logger.getLogger();

    private final String projectResourcesDirectoryPropertiesPath;

    private Properties homeDirectoryProperties;
    private Properties projectQADirectoryProperties;
    private Properties projectResourcesDirectoryProperties;

    public VersionEyeProperties(File homeDirectory, File projectDirectory, String propertyFileName) throws Exception {
        String projectQADirectoryPropertiesPath = projectDirectory.getCanonicalPath() + File.separator + "src" + File.separator + "qa" + File.separator + "resources" + File.separator + propertyFileName;
        projectResourcesDirectoryPropertiesPath = projectDirectory.getCanonicalPath() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + propertyFileName;
        String homeDirectoryPropertiesPath = homeDirectory.getCanonicalPath() + File.separator + ".m2" + File.separator + propertyFileName;

        projectQADirectoryProperties = readPropertyFile(projectQADirectoryPropertiesPath);
        projectResourcesDirectoryProperties = readPropertyFile(projectResourcesDirectoryPropertiesPath);
        homeDirectoryProperties = readPropertyFile(homeDirectoryPropertiesPath);
    }

    public String resolveProperty(PropertyKey key, String value) {
        if (value != null && !value.isEmpty()) {
            return value;
        }

        value = System.getProperty(key.getSystemPropertyKey());
        if (value != null && !value.isEmpty()) {
            return value;
        }

        if ((value == null || value.isEmpty()) && homeDirectoryProperties.contains(key.getKey())) {
            value = homeDirectoryProperties.getProperty(key.getKey());
        }

        if ((value == null || value.isEmpty()) && projectResourcesDirectoryProperties.contains(key.getKey())) {
            value = projectResourcesDirectoryProperties.getProperty(key.getKey());
        }

        if ((value == null || value.isEmpty()) && projectQADirectoryProperties.contains(key.getKey())) {
            value = projectQADirectoryProperties.getProperty(key.getKey());
        }
        return value;
    }

    public void setProjectId(String projectId) {
        projectResourcesDirectoryProperties.setProperty(PROJECT_ID.getKey(), projectId);
    }

    private static Properties readPropertyFile(String filePath) throws Exception {
        Properties properties = new Properties();
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch(Exception e) {
            LOGGER.debug("Failed to load properties from filePath", e);
        }
        return properties;
    }

    public void deleteProjectPropertiesFile() {
        File file = new File(projectResourcesDirectoryPropertiesPath);
        file.delete();
    }

    public void updateProjectProperties() throws Exception {
        File file = new File(projectResourcesDirectoryPropertiesPath);
        file.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(file);
        projectResourcesDirectoryProperties.store(out, "Properties for https://www.VersionEye.com Maven Plugin");
    }
}
