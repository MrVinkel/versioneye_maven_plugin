package com.versioneye.utils.log;

import com.versioneye.dto.ProjectDependency;
import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class LogUtil {

    private static final Logger LOGGER = Logger.getLogger();

    private LogUtil() {
        // Util methods for producing output
    }

    public static void logVersionEyeBanner() {
        LOGGER.info("");
        LOGGER.info("************* \\_/ VersionEye \\_/ *************");
        LOGGER.info("");
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

    public static void logCreateProject() {
        LOGGER.info("");
        LOGGER.info("Creating project...");
        LOGGER.info("");
    }

    public static void logStartUploadDependencies() {
        LOGGER.info("");
        LOGGER.info("Starting to upload dependencies. This can take a couple seconds...");
        LOGGER.info("");
    }

    public static void logJsonResponse(ProjectJsonResponse response, String baseUrl, String projectID) throws Exception {
        LOGGER.info("");
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
        LOGGER.info("You can find your updated project here: " + baseUrl + "/user/projects/" + projectID);
        LOGGER.info("");
    }

    public static void logStartDeleteProject(String projectName) {
        LOGGER.info("");
        LOGGER.info("Starting to delete " + projectName + " from the VersionEye server...");
        LOGGER.info("");
    }

    public static void logProjectDeleted() {
        LOGGER.info("");
        LOGGER.info("Project deleted");
        LOGGER.info("");
    }

    public static void logJsonLocation(String pathToJson) {
        LOGGER.info("");
        LOGGER.info("You find your json file here: " + pathToJson);
        LOGGER.info("");
    }
}
