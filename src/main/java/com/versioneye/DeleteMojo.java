package com.versioneye;

import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

import static com.versioneye.utils.log.LogUtil.logProjectDeleted;
import static com.versioneye.utils.log.LogUtil.logStartDeleteProject;

@Mojo(name = "delete", aggregator = true)
public class DeleteMojo extends ProjectMojo {

    @Override
    public void doExecute() throws Exception {
        //todo proxy
        setProxy();
        logStartDeleteProject(name);
        api.deleteProject(projectId);
        deletePropertiesFile();
        logProjectDeleted();
    }

    //todo properties
    private void deletePropertiesFile() throws Exception {
        String propertiesPath = getPropertiesPath();
        File file = new File(propertiesPath);
        file.delete();
    }
}
