package com.versioneye.utils;

import com.versioneye.dto.ProjectDependency;
import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.log.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class LogUtil {

    private static final Logger LOGGER = Logger.getLogger();

    private LogUtil() {
        // Util methods for producing nice output
    }

    public static void versionEyeOutput() {
        LOGGER.info("");
        LOGGER.info("************* \\_/ VersionEye \\_/ *************");
        LOGGER.info("");
    }

    public static void logNoDependenciesFound(MavenProject project) throws Exception {
        LOGGER.info(".");
        LOGGER.info("There are no dependencies in this project! - " + project.getId());
        LOGGER.info(".");
    }

    public static void logArtifactsList(String type, List<Artifact> artifacts) {
        LOGGER.info("");
        LOGGER.info(artifacts.size() + " " + type + " dependencies:");
        LOGGER.info("--------------------");
        for (Artifact artifact : artifacts) {
            LOGGER.info(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + ":" + artifact.getScope());
        }
        LOGGER.info("");
    }

    public static void logDependencySummary(int directCount, int transitiveCount) {
        int total = directCount + transitiveCount;
        LOGGER.info("");
        LOGGER.info(directCount + " Direct dependencies and " + transitiveCount + " transitive dependencies. This project has " + total + " dependencies.");
        LOGGER.info("");
    }

    public static void logStartUploadDependencies() {
        LOGGER.info(".");
        LOGGER.info("Starting to upload dependencies. This can take a couple seconds ... ");
        LOGGER.info(".");
    }

    public static void logJsonResponse(ProjectJsonResponse response) throws Exception {
        LOGGER.info(".");
        LOGGER.info("Project name: " + response.getName());
        LOGGER.info("Project id: " + response.getId());
        LOGGER.info("Dependencies: " + response.getDep_number());
        LOGGER.info("Outdated: " + response.getOut_number());
        for (ProjectDependency dependency : response.getDependencies()) {
            if (dependency.getOutdated() == false) {
                continue;
            }
            LOGGER.info(" - " + dependency.getProd_key() + ":" + dependency.getVersion_requested() + " -> " + dependency.getVersion_current());
        }
        LOGGER.info("");
        // seems like a extreme retarded way to build up the project URL
//        String projectID = (String) mavenSession.getTopLevelProject().getContextValue("veye_project_id");
//        LOGGER.info("You can find your updated project here: " + fetchBaseUrl() + "/user/projects/" + projectID);
        LOGGER.info("");
    }


    public static void logStartDeleteProject() {
        LOGGER.info(".");
        LOGGER.info("Starting to delete this project from the VersionEye server. This can take a couple seconds ... ");
        LOGGER.info(".");
    }

    public static void logJsonLocation(String pathToJson) {
        LOGGER.info("");
        LOGGER.info("You find your json file here: " + pathToJson);
        LOGGER.info("");
    }
}
