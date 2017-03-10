package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.LogUtil.logJsonResponse;
import static com.versioneye.utils.LogUtil.logNoDependenciesFound;
import static com.versioneye.utils.LogUtil.logStartUploadDependencies;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project AND
 * ensures that all used licenses are on a whitelist. If that is not the case it breaks the build.
 */
@Mojo(name = "licenseCheck", defaultPhase = LifecyclePhase.VERIFY)
//todo extends but dont reuse anything................
public class LicenseCheckMojo extends UpdateMojo {

    @Override
    public void doExecute() throws Exception {
        // todo proxy
        setProxy();
        logStartUploadDependencies();

        ByteArrayOutputStream jsonDependenciesStream;
        if (transitiveDependencies) {
            jsonDependenciesStream = getTransitiveDependenciesJsonStream(nameStrategy);
        } else {
            jsonDependenciesStream = getDirectDependenciesJsonStream(nameStrategy);
        }

        if (jsonDependenciesStream == null) {
            logNoDependenciesFound(project);
            return;
        }

        ProjectJsonResponse response = uploadDependencies(jsonDependenciesStream);
        System.out.println(response.getLicenses_red());
        if (response.getLicenses_red() > 0) {
            throw new MojoFailureException("Some components violate the license whitelist! " +
                    "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId());
        }

        if (response.getLicenses_unknown() > 0 && licenseCheckBreakByUnknown) {
            throw new MojoFailureException("Some components are without any license! " +
                    "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId());
        }

        logJsonResponse(response);
    }
}
