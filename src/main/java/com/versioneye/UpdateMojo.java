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
 * Updates an existing project at VersionEye with the dependencies from the current project.
 */
@Mojo(name = "update", defaultPhase = LifecyclePhase.PACKAGE)
public class UpdateMojo extends ProjectMojo {

    //todo api
    @Parameter(property = "resource", defaultValue = "/projects")
    private String resource;


    @Override
    public void doExecute() throws Exception {

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

        ProjectJsonResponse response = uploadDependencies(jsonDependenciesStream);
        logJsonResponse(response);
    }


    protected ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
        try {
            String projectId = fetchProjectId();
            if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())) {
                mavenSession.getTopLevelProject().setContextValue("veye_project_id", projectId);
            }
            return updateExistingProject(resource, projectId, outStream);
        } catch (Exception ex) {
            getLog().error("Error in UpdateMojo.uploadDependencies " + ex.getMessage());
            ProjectJsonResponse response = createNewProject("/projects?api_key=", outStream);
            if (updatePropertiesAfterCreate) {
                writeProperties(response);
            }
            merge(response.getId());
            return response;
        }
    }


}
