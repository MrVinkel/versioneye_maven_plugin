package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.utils.log.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.Set;

import static com.versioneye.dependency.DependencyResolver.mergeArtifactsWithStrongestScope;

public abstract class AbstractAggregatedMojo extends AbstractSuperMojo {
    private static final Logger LOGGER = Logger.getLogger();

    protected Set<Artifact> directDependencies = null;
    protected Set<Artifact> transitiveDependencies = null;

    @Override
    public void doExecute() throws Exception {
        final int size = reactorProjects.size();
        MavenProject lastProject = reactorProjects.get(size - 1);
        if (lastProject != project) {
            // Skip all projects except the last, to make sure all dependencies in all reactor project have been initialized
            LOGGER.info("Skipping");
            return;
        }

        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        directDependencies = dependencyResolver.getDirectDependencies();
        transitiveDependencies = dependencyResolver.getTransitiveDependencies();

        for (MavenProject project : reactorProjects) {
            LOGGER.debug("---------------------------------------------");
            LOGGER.debug(" --- Project: " + project.getArtifactId());
            DependencyResolver reactorProjectDependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);
            LOGGER.debug(" --- Direct: ");
            directDependencies = mergeArtifactsWithStrongestScope(directDependencies, reactorProjectDependencyResolver.getDirectDependencies());
            LOGGER.debug(" --- Transitive: ");
            transitiveDependencies = mergeArtifactsWithStrongestScope(transitiveDependencies, reactorProjectDependencyResolver.getTransitiveDependencies());
        }

        transitiveDependencies.removeAll(directDependencies);

        doFinalExecute();
    }

    abstract void doFinalExecute() throws Exception;
}