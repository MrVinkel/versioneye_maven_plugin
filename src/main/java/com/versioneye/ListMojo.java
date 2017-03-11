package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Set;

import static com.versioneye.dependency.DependencyResolver.asSortedList;
import static com.versioneye.utils.log.LogUtil.logArtifactsList;
import static com.versioneye.utils.log.LogUtil.logDependencySummary;

/**
 * Lists all direct and recursive dependencies.
 */
@Mojo(name = "list", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ListMojo extends ProjectMojo {

    @Override
    public void doExecute() throws Exception {
        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();
        Set<Artifact> transitiveArtifacts = dependencyResolver.getTransitiveDependencies();

        transitiveArtifacts.removeAll(directArtifacts);
        logArtifactsList("Direct", asSortedList(directArtifacts));
        logArtifactsList("Transitive", asSortedList(transitiveArtifacts));

        logDependencySummary(directArtifacts.size(), transitiveArtifacts.size());
    }
}
