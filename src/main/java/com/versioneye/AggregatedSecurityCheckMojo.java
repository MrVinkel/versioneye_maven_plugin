package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "aggregated-securityCheck", defaultPhase = LifecyclePhase.VERIFY)
public class AggregatedSecurityCheckMojo extends AggregatedUpdateMojo {

    @Override
    public void validateResponse(ProjectJsonResponse response) throws Exception {
        super.validateResponse(response);

        if (response.getSv_count() > 0) {
            throw new MojoExecutionException("Some components have security vulnerabilities! " +
                    "More details here: " + baseUrl + "/user/projects/" + response.getId());
        }
    }
}
