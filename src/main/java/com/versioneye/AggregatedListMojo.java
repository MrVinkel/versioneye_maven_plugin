package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.utils.log.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.util.Set;

import static com.versioneye.dependency.DependencyResolver.asSortedList;
import static com.versioneye.dependency.DependencyResolver.mergeArtifactsWithStrongestScope;
import static com.versioneye.utils.log.LogUtil.logArtifactsList;
import static com.versioneye.utils.log.LogUtil.logDependencySummary;

@Mojo(name = "aggregated-list", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class AggregatedListMojo extends AbstractAggregatedMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    public void doFinalExecute() throws Exception {
        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();
        Set<Artifact> transitiveDependencies = dependencyResolver.getTransitiveDependencies();

        for (MavenProject project : reactorProjects) {
            LOGGER.debug("---------------------------------------------");
            LOGGER.debug(" --- Project: " + project.getArtifactId());
            DependencyResolver reactorProjectDependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);
            LOGGER.debug(" --- Direct: ");
            directArtifacts = mergeArtifactsWithStrongestScope(directArtifacts, reactorProjectDependencyResolver.getDirectDependencies());
            LOGGER.debug(" --- Transitive: ");
            transitiveDependencies = mergeArtifactsWithStrongestScope(transitiveDependencies, reactorProjectDependencyResolver.getTransitiveDependencies());
        }

        transitiveDependencies.removeAll(directArtifacts);
        logArtifactsList("Direct", asSortedList(directArtifacts));
        logArtifactsList("Transitive", asSortedList(transitiveDependencies));
        logDependencySummary(directArtifacts.size(), transitiveDependencies.size());
    }
}
