package com.versioneye;

import com.versioneye.dependency.DependencyToJsonConverter;
import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.log.LogUtil.*;

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

        DependencyToJsonConverter dependencyToJsonConverter = new DependencyToJsonConverter(project, dependencyGraphBuilder);
        ByteArrayOutputStream jsonDependenciesStream = dependencyToJsonConverter.getDependenciesAsJsonStream(nameStrategy, transitiveDependencies, excludeScopes);
        ProjectJsonResponse response = api.createProject(jsonDependenciesStream, visibility, name, organisation, team);

        if (updatePropertiesAfterCreate) {
            writeProperties(response);
        }
        logJsonResponse(response);
    }
}
