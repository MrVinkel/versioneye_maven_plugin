package com.versioneye.utils;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Methods to deal with JSON.
 */
public class JsonUtil {

    private JsonUtil() {
        // Util class
    }

    public static ByteArrayOutputStream dependenciesToJson(MavenProject project, List<Dependency> dependencies, String name) throws Exception {
        List<Map<String, Object>> dependencyHashes = new ArrayList<Map<String, Object>>();
        if ((dependencies != null && !dependencies.isEmpty())) {
            dependencyHashes = getDependencyHashes(dependencies);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        toJson(outputStream, getJsonPom(project, dependencyHashes, name));
        return outputStream;
    }

    public static void toJson(OutputStream output, Object input) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.writeValue(output, input);
    }


    public static List<Map<String, Object>> getDependencyHashes(List<Dependency> directDependencies) {
        List<Map<String, Object>> hashes = new Vector<Map<String, Object>>();
        if (directDependencies != null && directDependencies.size() > 0) {
            hashes.addAll(generateHashFromDependencyList(directDependencies));
        }
        return hashes;
    }

    public static List<Map<String, Object>> generateHashFromDependencyList(List<Dependency> input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
        for (Dependency dependency : input) {
            HashMap<String, Object> hash = new HashMap<String, Object>(2);
            hash.put("version", dependency.getVersion());
            hash.put("name", dependency.getGroupId() + ":" + dependency.getArtifactId());
            hash.put("scope", dependency.getScope());
            output.add(hash);
        }
        return output;
    }

    public static Map<String, Object> getJsonPom(MavenProject project, List<Map<String, Object>> dependencyHashes, String name) {
        Map<String, Object> pom = new HashMap<String, Object>();
        pom.put("name", name);
        pom.put("group_id", project.getGroupId());
        pom.put("artifact_id", project.getArtifactId());
        pom.put("version", project.getVersion());
        pom.put("language", "Java");
        pom.put("prod_type", "Maven2");
        pom.put("licenses", project.getLicenses());
        pom.put("dependencies", dependencyHashes);
        return pom;
    }

}
