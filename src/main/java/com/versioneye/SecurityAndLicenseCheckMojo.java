package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

import static com.versioneye.utils.LogUtil.logJsonResponse;
import static com.versioneye.utils.LogUtil.logNoDependenciesFound;
import static com.versioneye.utils.LogUtil.logStartUploadDependencies;

@Mojo(name = "securityAndLicenseCheck", defaultPhase = LifecyclePhase.VERIFY)
//todo extends but dont reuse anything................
public class SecurityAndLicenseCheckMojo extends UpdateMojo {

    @Override
    public void doExecute() throws Exception {
        //todo proxy
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
        System.out.println("sv_count: " + response.getSv_count());
        if (response.getSv_count() > 0) {
            throw new MojoFailureException("Some components have security vulnerabilities! " +
                    "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId());
        }

        System.out.println("licenses_red: " + response.getLicenses_red());

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
