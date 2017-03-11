package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "securityAndLicenseCheck", defaultPhase = LifecyclePhase.VERIFY)
public class SecurityAndLicenseCheckMojo extends UpdateMojo {

    @Override
    public void validateResponse(ProjectJsonResponse response) throws Exception {
        super.validateResponse(response);
        if (response.getSv_count() > 0) {
            throw new MojoFailureException("Some components have security vulnerabilities! " +
                    "More details here: " + baseUrl + "/user/projects/" + response.getId());
        }

        if (response.getLicenses_red() > 0) {
            throw new MojoFailureException("Some components violate the license whitelist! " +
                    "More details here: " + baseUrl + "/user/projects/" + response.getId());
        }

        if (response.getLicenses_unknown() > 0 && licenseCheckBreakByUnknown) {
            throw new MojoFailureException("Some components are without any license! " +
                    "More details here: " + baseUrl + "/user/projects/" + response.getId());
        }
    }
}
