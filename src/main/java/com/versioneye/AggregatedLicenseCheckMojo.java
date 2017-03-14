package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project AND
 * ensures that all used licenses are on a whitelist. If that is not the case it breaks the build.
 */
@Mojo(name = "aggregated-licenseCheck", defaultPhase = LifecyclePhase.VERIFY)
public class AggregatedLicenseCheckMojo extends AggregatedUpdateMojo {

    @Override
    public void validateResponse(ProjectJsonResponse response) throws Exception {
        super.validateResponse(response);
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
