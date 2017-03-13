package com.versioneye;

import com.versioneye.dependency.DependencyToJsonConverter;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;

import static com.versioneye.utils.log.LogUtil.logJsonLocation;

/**
 * Writes all direct dependencies into a JSON file.
 */
@Mojo(name = "json", aggregator = true)
public class JsonMojo extends AbstractSuperMojo {

    @Override
    public void doExecute() throws Exception {
        DependencyToJsonConverter dependencyToJsonConverter = new DependencyToJsonConverter(project, dependencyGraphBuilder);
        ByteArrayOutputStream dependenciesAsJsonStream = dependencyToJsonConverter.getDependenciesAsJsonStream(name, includeTransitiveDependencies, excludeScopes);
        String filePath = outputDirectory + File.separator + "pom.json";
        tryWriteJsonPomToFile(dependenciesAsJsonStream, filePath);
        logJsonLocation(filePath);
    }

    private void tryWriteJsonPomToFile(ByteArrayOutputStream dependenciesAsJsonStream, String filePath) throws IOException {
        OutputStream outputStream = null;
        try {
            File pomAsJsonFile = new File(filePath);
            pomAsJsonFile.getParentFile().mkdirs();
            pomAsJsonFile.createNewFile();
            outputStream = new FileOutputStream(pomAsJsonFile, false);
            dependenciesAsJsonStream.writeTo(outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
