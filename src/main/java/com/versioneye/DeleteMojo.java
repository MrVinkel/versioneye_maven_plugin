package com.versioneye;


import com.versioneye.utils.HttpUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import static com.versioneye.utils.LogUtil.logStartDeleteProject;

@Mojo(name = "delete", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DeleteMojo extends ProjectMojo {

    //todo api
    @Parameter(property = "resource", defaultValue = "/projects")
    private String resource;

    @Override
    public void doExecute() throws Exception {
        setProxy();
        logStartDeleteProject();
        deleteProject();
        deletePropertiesFile();
    }

    private void deleteProject() throws Exception {
        //todo api
        String apiKey = fetchApiKey();
        String projectId = fetchProjectId();
        String url = fetchBaseUrl() + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;

        HttpUtils.delete(url);
    }

    private void deletePropertiesFile() throws Exception {
        String propertiesPath = getPropertiesPath();
        File file = new File(propertiesPath);
        file.delete();
    }
}
