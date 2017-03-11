package com.versioneye.utils.properties;

import java.io.File;
import java.util.Properties;

import static com.versioneye.utils.properties.PropertiesUtil.readPropertyFile;

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

    private Properties homeDirectoryProperties;
    private Properties projectQADirectoryProperties;
    private Properties projectResourcesDirectoryProperties;

    public VersionEyeProperties(File homeDirectory, File projectDirectory, String propertyFileName) throws Exception {
        String projectQADirectoryPropertiesPath = projectDirectory.getCanonicalPath() + File.separator + "src" + File.separator + "qa" + File.separator + "resources" + File.separator + propertyFileName;
        String projectResourcesDirectoryPropertiesPath = projectDirectory.getCanonicalPath() +  File.separator +"src" + File.separator + "main" + File.separator + "resources" + File.separator + propertyFileName;
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

    public void deleteProjectPropertiesFile() {

    }

    public void updateProjectProperties() {

    }
}
