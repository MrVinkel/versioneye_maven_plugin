package com.versioneye;

import com.versioneye.utils.log.Logger;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Ping the VersionEye API. Expects a pong in response.
 */
@Mojo(name = "ping", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class PingMojo extends AbstractSuperMojo {
    private static final Logger LOGGER = Logger.getLogger();

    @Override
    public void doExecute() throws Exception {
        LOGGER.info("");
        LOGGER.info("Sending ping...");
        LOGGER.info("Received " + api.ping());
        LOGGER.info("");
    }
}
