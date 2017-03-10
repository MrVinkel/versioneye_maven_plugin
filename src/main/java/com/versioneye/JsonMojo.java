package com.versioneye;

import com.versioneye.utils.JsonUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.Map;

import static com.versioneye.utils.LogUtil.logJsonLocation;

/**
 * Writes all direct dependencies into a JSON file.
 */
@Mojo(name = "json", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class JsonMojo extends ProjectMojo {

    @Override
    public void doExecute() throws Exception {
        Map<String, Object> jsonMap = getDirectDependenciesJsonMap(nameStrategy);
        JsonUtils jsonUtils = new JsonUtils();
        String filePath = outputDirectory + File.separator + "pom.json";
        jsonUtils.dependenciesToJsonFile(jsonMap, filePath);
        logJsonLocation(filePath);
    }

}
