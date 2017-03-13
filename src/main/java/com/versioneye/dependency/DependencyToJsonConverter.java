package com.versioneye.dependency;

import com.versioneye.utils.JsonUtil;
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

    public ByteArrayOutputStream getDependenciesAsJsonStream(String name, boolean includeTransitiveDependencies, List<String> excludeScopes) throws Exception {
        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directDependencies = dependencyResolver.getDirectDependencies();

        Set<Artifact> transitiveDependencies = null;
        if(includeTransitiveDependencies) {
            transitiveDependencies = dependencyResolver.getTransitiveDependencies();
        }
        return getDependenciesAsJsonStream(name, directDependencies, transitiveDependencies);
    }


    public ByteArrayOutputStream getDependenciesAsJsonStream(String name, Set<Artifact> directDependencies, Set<Artifact> transitiveDependencies) throws Exception {
        Set<Artifact> dependencies = new HashSet<Artifact>();
        dependencies.addAll(directDependencies);
        if(transitiveDependencies != null) {
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
        return JsonUtil.dependenciesToJson(project, dependenciesList, name);
    }

}
