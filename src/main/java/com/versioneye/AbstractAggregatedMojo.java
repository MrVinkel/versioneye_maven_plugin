package com.versioneye;

import com.versioneye.utils.log.Logger;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAggregatedMojo extends AbstractSuperMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    public void doExecute() throws Exception {
        final int size = reactorProjects.size();
        MavenProject lastProject = reactorProjects.get(size - 1);
        if (lastProject != project) {
            // Skip all projects except the last, to make sure all dependencies in all reactor project have been initialized
            LOGGER.info("Skipping");
            return;
        }

        doFinalExecute();
    }

    abstract void doFinalExecute() throws Exception;
}
