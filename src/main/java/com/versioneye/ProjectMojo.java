package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.PropertiesUtils;
import java.util.*;

/**
 * Methods required to deal with projects resource
 */
public abstract class ProjectMojo extends SuperMojo {
    //todo properties
    protected void writeProperties(ProjectJsonResponse response) throws Exception {
        Properties properties = fetchProjectProperties();
        if (response.getId() != null) {
            properties.setProperty("project_id", response.getId());
        }
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, getPropertiesPath());
    }

}
