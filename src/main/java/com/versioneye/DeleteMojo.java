package com.versioneye;

import org.apache.maven.plugins.annotations.Mojo;

import static com.versioneye.utils.log.LogUtil.logProjectDeleted;
import static com.versioneye.utils.log.LogUtil.logStartDeleteProject;

@Mojo(name = "delete", aggregator = true)
public class DeleteMojo extends AbstractSuperMojo {

    @Override
    public void doExecute() throws Exception {
        logStartDeleteProject(name);
        api.deleteProject(projectId);
        properties.deleteProjectPropertiesFile();
        logProjectDeleted();
    }


}
