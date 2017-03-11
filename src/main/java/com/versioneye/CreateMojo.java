package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.LogUtil.*;

/**
 * Creates a project at VersionEye based on the dependencies from the current project.
 */
@Mojo(name = "create", aggregator = true)
public class CreateMojo extends ProjectMojo {

    @Override
    public void doExecute() throws Exception {
        //todo proxy
        setProxy();
        logCreateProject();

        ByteArrayOutputStream jsonDependenciesStream;
        if (transitiveDependencies) {
            jsonDependenciesStream = getTransitiveDependenciesJsonStream(nameStrategy);
        } else {
            jsonDependenciesStream = getDirectDependenciesJsonStream(nameStrategy);
        }

        ProjectJsonResponse response = api.createProject(jsonDependenciesStream, visibility, name, organisation, team);

        if (updatePropertiesAfterCreate) {
            writeProperties(response);
        }
        logJsonResponse(response);
    }
}
