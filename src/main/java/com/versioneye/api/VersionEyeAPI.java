package com.versioneye.api;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * https://www.versioneye.com/api/
 */
public class VersionEyeAPI {

    private final String apiUrl;
    private final String baseUrl;
    private final String apiVersion;
    private final String apiKey;

    public VersionEyeAPI(String baseUrl, String apiVersion, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.apiKey = apiKey;
        this.apiUrl = baseUrl + "/api/" + apiVersion;
    }

    // todo refactor to just accept the jsonobject..
    public ProjectJsonResponse createProject(ByteArrayOutputStream jsonDependencies, String visibility, String projectName, String organisationName, String teamName) throws Exception {
        String url = apiUrl + "/projects?api_key=" + apiKey;
        JsonHttpClient client = new JsonHttpClient(url);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        ByteArrayBody byteArrayBody = new ByteArrayBody(jsonDependencies.toByteArray(), APPLICATION_JSON, "pom.json");
        builder.addPart("upload", byteArrayBody);

        addPartIfIsSet(builder, "visibility", visibility);
        addPartIfIsSet(builder, "name", projectName);
        addPartIfIsSet(builder, "orga_name", organisationName);
        addPartIfIsSet(builder, "team_name", teamName);

        String result = client.post(builder.build());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, ProjectJsonResponse.class );
    }

    private void addPartIfIsSet(MultipartEntityBuilder builder, String contentName, String content) {
        if (content != null && !content.isEmpty()) {
            builder.addPart(contentName, new StringBody(content, APPLICATION_JSON));
        }
    }

    public void updateProject() {

    }

    public void deleteProject() {

    }

}
