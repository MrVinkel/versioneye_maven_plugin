package com.versioneye;

import com.versioneye.api.VersionEyeAPI;
import com.versioneye.utils.log.MavenLogger;
import com.versioneye.utils.properties.VersionEyeProperties;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;

import java.io.File;
import java.util.List;

import static com.versioneye.utils.log.LogUtil.logVersionEyeBanner;
import static com.versioneye.utils.properties.VersionEyeProperties.PropertyKey.*;

/**
 * The Mother of all Mojos!
 */
public abstract class AbstractSuperMojo extends AbstractMojo {

    @Parameter(defaultValue = "versioneye.properties")
    protected String propertiesFile;

    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession mavenSession;

    @Component(hint = "default")
    protected DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(defaultValue = "${reactorProjects}")
    protected List<MavenProject> reactorProjects;

    @Parameter(defaultValue = "${basedir}", property = "projectDirectory", required = true)
    protected File projectDirectory;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    protected File outputDirectory;

    @Parameter(defaultValue = "${user.home}")
    protected File homeDirectory;

    @Parameter(property = "baseUrl", defaultValue = "https://www.versioneye.com")
    protected String baseUrl;

    @Parameter(property = "apiVersion", defaultValue = "v2")
    protected String apiVersion;

    @Parameter(property = "projectId")
    protected String projectId;

    @Parameter(property = "apiKey")
    protected String apiKey;

    @Parameter(property = "proxyHost")
    protected String proxyHost = null;

    @Parameter(property = "proxyPort")
    protected String proxyPort = null;

    @Parameter(property = "proxyUser")
    protected String proxyUser = null;

    @Parameter(property = "proxyPassword")
    protected String proxyPassword = null;

    @Parameter(property = "updateProperties")
    protected boolean updateProperties = true;

    @Parameter(property = "nameStrategy")
    protected String nameStrategy = "name";

    @Parameter(property = "licenseCheckBreakByUnknown")
    protected boolean licenseCheckBreakByUnknown = false;

    @Parameter(property = "excludeScopes")
    protected List<String> excludeScopes = null;

    @Parameter(property = "organisation")
    protected String organisation = null;

    @Parameter(property = "team")
    protected String team = null;

    @Parameter(property = "visibility")
    protected String visibility = null;

    @Parameter(property = "name")
    protected String name = null;

    @Parameter(property = "includeTransitiveDependencies")
    protected boolean includeTransitiveDependencies = false;

    @Parameter(property = "licenseReportName", defaultValue = "LicenseReport-${project.version}.pdf")
    protected String licenseReportName;

    @Parameter(property = "securityIssuesReportName", defaultValue = "SecurityIssuesReport-${project.version}.pdf")
    protected String securityIssuesReportName;

    protected VersionEyeAPI api;
    protected VersionEyeProperties properties;


    public void execute() throws MojoExecutionException, MojoFailureException {
        new MavenLogger(getLog());
        try {
            logVersionEyeBanner();
            properties = new VersionEyeProperties(homeDirectory, projectDirectory, propertiesFile);
            projectId = properties.resolveProperty(PROJECT_ID, projectId);
            apiKey = properties.resolveProperty(API_KEY, apiKey);
            baseUrl = properties.resolveProperty(BASE_URL, baseUrl);
            name = resolveNameWithNamingStrategy(nameStrategy, name);

            proxyHost = properties.resolveProperty(PROXY_HOST, proxyHost);
            proxyPort = properties.resolveProperty(PROXY_PORT, proxyPort);
            proxyUser = properties.resolveProperty(PROXY_USER, proxyUser);
            proxyPassword = properties.resolveProperty(PROXY_PASSWORD, proxyPassword);

            api = new VersionEyeAPI(baseUrl, apiVersion, apiKey, proxyHost, proxyPort, proxyUser, proxyPassword);

            doExecute();
        } catch (MojoFailureException e) {
            throw e;
        } catch (MojoExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. Error: " + e.getMessage(), e);
        }
    }

    public abstract void doExecute() throws Exception;

    private String resolveNameWithNamingStrategy(String nameStrategy, String name) {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        if (nameStrategy == null || nameStrategy.isEmpty()) {
            nameStrategy = "name";
        }
        if (nameStrategy.equals("name")) {
            name = mavenSession.getTopLevelProject().getName();
            if (name == null || name.isEmpty()) {
                name = mavenSession.getTopLevelProject().getArtifactId();
            }
        } else if (nameStrategy.equals("artifact_id")) {
            name = mavenSession.getTopLevelProject().getArtifactId();
        } else if (nameStrategy.equals("GA")) {
            name = mavenSession.getTopLevelProject().getGroupId() + "/" + project.getArtifactId();
        }
        return name;
    }
}