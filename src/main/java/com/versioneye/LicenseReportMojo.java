package com.versioneye;

import com.versioneye.utils.log.Logger;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(name = "license-report", aggregator = true)
public class LicenseReportMojo extends AbstractSuperMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    public void doExecute() throws Exception {
        LOGGER.info("Fetching license report..");
        api.fetchLicenseReport(projectId, outputDirectory, licenseReportName);
        LOGGER.info("Done! License report can be found at " + outputDirectory.getCanonicalPath() + File.separator + licenseReportName);
    }
}
