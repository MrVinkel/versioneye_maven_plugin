package com.versioneye.dependency;

import com.versioneye.utils.JsonUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyToJsonConverter {

    private static final String SCOPE_DIRECT = "direct";
    private static final String SCOPE_TRANSITIVE = "transitive";

    private MavenProject project;
    private DependencyGraphBuilder dependencyGraphBuilder;

    public DependencyToJsonConverter(MavenProject project, DependencyGraphBuilder dependencyGraphBuilder) {
        this.project = project;
        this.dependencyGraphBuilder = dependencyGraphBuilder;
    }

    //todo refactor name strategy
    public ByteArrayOutputStream getDependenciesAsJsonStream(String nameStrategy, boolean includeTransitiveDependencies, List<String> excludeScopes) throws Exception {
        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);
        Set<Artifact> dependencies = new HashSet<Artifact>();

        Set<Artifact> directDependencies = dependencyResolver.getDirectDependencies();
        dependencies.addAll(directDependencies);

        if(includeTransitiveDependencies) {
            Set<Artifact> transitiveDependencies = dependencyResolver.getTransitiveDependencies();
            dependencies.addAll(transitiveDependencies);
        }

        List<Dependency> dependenciesList = new ArrayList<Dependency>();
        for (Artifact artifact : dependencies) {
            Dependency dependency = new Dependency();
            dependency.setGroupId(artifact.getGroupId());
            dependency.setArtifactId(artifact.getArtifactId());
            dependency.setVersion(artifact.getVersion());
            if (directDependencies.contains(artifact)) {
                dependency.setScope(SCOPE_DIRECT);
            } else {
                dependency.setScope(SCOPE_TRANSITIVE);
            }
            dependenciesList.add(dependency);
        }
        return JsonUtils.dependenciesToJson(project, dependenciesList, nameStrategy);
    }
}
