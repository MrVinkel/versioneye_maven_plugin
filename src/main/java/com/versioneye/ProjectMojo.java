package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.dto.ProjectDependency;
import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import com.versioneye.utils.JsonUtils;
import com.versioneye.utils.PropertiesUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.*;

/**
 * Methods required to deal with projects resource
 */
public abstract class ProjectMojo extends SuperMojo {

    //todo Json converter
    protected ByteArrayOutputStream getTransitiveDependenciesJsonStream(String nameStrategy) throws Exception {

        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();

        Set<Artifact> transitiveArtifacts = dependencyResolver.getTransitiveDependencies();

        Set<Artifact> artifacts = new HashSet<Artifact>();
        artifacts.addAll(directArtifacts);
        artifacts.addAll(transitiveArtifacts);

        List<Dependency> dependencies = new ArrayList<Dependency>();
        for (Artifact artifact : artifacts) {
            Dependency dep = new Dependency();
            dep.setGroupId(artifact.getGroupId());
            dep.setArtifactId(artifact.getArtifactId());
            dep.setVersion(artifact.getVersion());
            if (directArtifacts.contains(artifact)) {
                dep.setScope("direct");
            } else {
                dep.setScope("transitive");
            }
            dependencies.add(dep);
        }
        return JsonUtils.dependenciesToJson(project, dependencies, nameStrategy);
    }

    //todo Json converter
    protected ByteArrayOutputStream getDirectDependenciesJsonStream(String nameStrategy) throws Exception {
        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();

        List<Dependency> dependencies = new ArrayList<Dependency>();
        for (Artifact artifact : directArtifacts) {
            Dependency dep = new Dependency();
            dep.setGroupId(artifact.getGroupId());
            dep.setArtifactId(artifact.getArtifactId());
            dep.setVersion(artifact.getVersion());
            dep.setScope("direct");
            dependencies.add(dep);
        }

        return JsonUtils.dependenciesToJson(project, dependencies, nameStrategy);
    }


    //todo api
    protected ProjectJsonResponse updateExistingProject(String resource, String projectId, ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = fetchBaseUrl() + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "project_file", null, null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    //todo api
    protected ProjectJsonResponse createNewProject(String resource, ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = fetchBaseUrl() + apiPath + resource + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "upload", visibility, name, organisation, team);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    //todo api
    protected void merge(String childId) {
        if (mergeAfterCreate == false) {
            return ;
        }
        try {
            if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())){
                return ;
            }

            String parentProjectId = (String) mavenSession.getTopLevelProject().getContextValue("veye_project_id");
            getLog().debug("parentProjectId: " + parentProjectId);
            String url = fetchBaseUrl() + apiPath + "/projects/" + parentProjectId + "/merge/" + childId + "?api_key=" + fetchApiKey();

            String response = HttpUtils.get(url);
            getLog().debug("merge response: " + response);
        } catch (Exception ex) {
            getLog().error(ex);
        }
    }

    //todo properties
    protected void writeProperties(ProjectJsonResponse response) throws Exception {
        Properties properties = fetchProjectProperties();
        if (response.getId() != null) {
            properties.setProperty("project_id", response.getId());
        }
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, getPropertiesPath());
    }

}
