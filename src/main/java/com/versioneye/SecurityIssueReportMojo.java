package com.versioneye;

import com.versioneye.utils.log.Logger;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

@Mojo(name = "security-report", aggregator = true)
public class SecurityIssueReportMojo extends AbstractSuperMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    public void doExecute() throws Exception {
        LOGGER.info("Fetching license report..");
        api.fetchSecurityIssuesReport(projectId, outputDirectory, securityIssuesReportName);
        LOGGER.info("Done! Security issue report can be found at " + outputDirectory.getCanonicalPath() + File.separator + securityIssuesReportName);
    }
}
