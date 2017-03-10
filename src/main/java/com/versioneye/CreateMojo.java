package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.LogUtil.logJsonResponse;
import static com.versioneye.utils.LogUtil.logNoDependenciesFound;
import static com.versioneye.utils.LogUtil.logStartUploadDependencies;

/**
 * Creates a project at VersionEye based on the dependencies from the current project.
 */
@Mojo(name = "create", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class CreateMojo extends ProjectMojo {

    //todo api
    @Parameter(property = "resource", defaultValue = "/projects?api_key=")
    private String resource;

    @Override
    public void doExecute() throws Exception {
        //todo proxy
        setProxy();
        logStartUploadDependencies();

        ByteArrayOutputStream jsonDependenciesStream;
        if (transitiveDependencies) {
            jsonDependenciesStream = getTransitiveDependenciesJsonStream(nameStrategy);
        } else {
            jsonDependenciesStream = getDirectDependenciesJsonStream(nameStrategy);
        }

        if (jsonDependenciesStream == null) {
            logNoDependenciesFound(project);
            return;
        }

        ProjectJsonResponse response = createNewProject(resource, jsonDependenciesStream);

        if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())) {
            mavenSession.getTopLevelProject().setContextValue("veye_project_id", response.getId());
        }

        merge(response.getId());
        if (updatePropertiesAfterCreate) {
            writeProperties(response);
        }
        logJsonResponse(response);
    }

}
