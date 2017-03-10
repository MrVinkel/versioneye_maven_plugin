package com.versioneye.api;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class JsonHttpClient {

    private String baseUrl;

    public JsonHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String post(String resource, ArrayList<NameValuePair> parameters) throws Exception {
        HttpUriRequest postRequest = RequestBuilder.post()
                .setUri(baseUrl + resource)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .addParameters((NameValuePair[]) parameters.toArray()).build();

        return executeRequest(postRequest);
    }

    public String get(String resource) throws Exception {
        HttpUriRequest getRequest = RequestBuilder.get()
                .setUri(baseUrl + resource)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .build();

        return executeRequest(getRequest);
    }

    private String executeRequest(HttpUriRequest request) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new MojoExecutionException("Failed to call " + request.getURI() + " : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        } finally {
            if(response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }
}
