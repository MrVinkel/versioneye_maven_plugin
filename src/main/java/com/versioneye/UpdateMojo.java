package com.versioneye;

import com.versioneye.dependency.DependencyToJsonConverter;
import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.log.LogUtil.*;
import static org.eclipse.aether.spi.log.NullLoggerFactory.LOGGER;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project.
 */
@Mojo(name = "update", defaultPhase = LifecyclePhase.PACKAGE)
public class UpdateMojo extends ProjectMojo {

    @Override
    public void doExecute() throws Exception {
        //todo proxy
        setProxy();
        logStartUploadDependencies();

        DependencyToJsonConverter dependencyToJsonConverter = new DependencyToJsonConverter(project, dependencyGraphBuilder);
        ByteArrayOutputStream jsonDependenciesStream = dependencyToJsonConverter.getDependenciesAsJsonStream(nameStrategy, transitiveDependencies, excludeScopes);

        //todo property stuff
        if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())) {
            mavenSession.getTopLevelProject().setContextValue("veye_project_id", projectId);
        }
        ProjectJsonResponse response;
        try {
            response = api.updateProject(jsonDependenciesStream, projectId);
        } catch (Exception e) {
            LOGGER.warn("Failed to update project with id " + projectId + " - creating new project...");
            response = api.createProject(jsonDependenciesStream, null, null, null, null);
            api.mergeProjects(projectId, response.getId());
        }

        if (updatePropertiesAfterCreate) {
            writeProperties(response);
        }

        logJsonResponse(response);

        validateResponse(response);
    }

    public void validateResponse(ProjectJsonResponse response) throws Exception {
        // Validate license, security etc.
    }

}
