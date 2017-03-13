package com.versioneye;

import com.versioneye.dependency.DependencyToJsonConverter;
import com.versioneye.utils.log.Logger;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

@Mojo(name = "aggregated-update", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class AggregatedUpdateMojo extends AbstractAggregatedMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    void doFinalExecute() throws Exception {
        LOGGER.info("Building dependency graph..");
        DependencyToJsonConverter jsonConverter = new DependencyToJsonConverter(mavenSession.getTopLevelProject(), dependencyGraphBuilder);
        ByteArrayOutputStream dependenciesAsJsonStream = jsonConverter.getDependenciesAsJsonStream(name, directDependencies, transitiveDependencies);

        LOGGER.info("Uploading result to https://versioneye.com");
        api.updateProject(dependenciesAsJsonStream, projectId);

        LOGGER.info("Done!");
    }
}
