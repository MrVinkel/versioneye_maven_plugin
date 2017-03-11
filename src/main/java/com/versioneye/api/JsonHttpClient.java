package com.versioneye.api;

import com.versioneye.dto.ErrorJsonResponse;
import com.versioneye.utils.log.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class JsonHttpClient {
    private static final Logger LOGGER = Logger.getLogger();

    private static final String USER_AGENT = "VersionEye Maven Plugin";
    private HttpHost proxy = null;
    private BasicCredentialsProvider basicCredentialsProvider = null;


    public String post(String url, HttpEntity entity) throws Exception {
        RequestBuilder postRequest = RequestBuilder.post()
                .setUri(url)
                .setEntity(entity);

        return executeRequest(postRequest, SC_OK, SC_CREATED);
    }

    public String get(String url) throws Exception {
        RequestBuilder getRequest = RequestBuilder.get()
                .setUri(url)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType());

        return executeRequest(getRequest, SC_OK);
    }

    public String delete(String url) throws Exception {
        RequestBuilder postRequest = RequestBuilder.delete()
                .setUri(url);

        return executeRequest(postRequest, SC_OK, SC_NO_CONTENT);
    }

    private String executeRequest(RequestBuilder requestBuilder, Integer... acceptedStatusCodes) throws Exception {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder
                .create()
                .setUserAgent(USER_AGENT);
        if (basicCredentialsProvider != null) {
            httpClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider);
        }
        HttpClient httpClient = httpClientBuilder.build();
        HttpResponse response = null;
        try {
            if (proxy != null) {
                RequestConfig config = RequestConfig.custom()
                        .setProxy(proxy)
                        .build();
                requestBuilder.setConfig(config);
            }
            HttpUriRequest request = requestBuilder.build();
            response = httpClient.execute(request);

            Set<Integer> statusCodesSet = new HashSet<Integer>();
            Collections.addAll(statusCodesSet, acceptedStatusCodes);
            if (!statusCodesSet.contains(response.getStatusLine().getStatusCode())) {
                String errorMessage = getErrorMessage(response);
                throw new MojoExecutionException("Failed to call " + request.getURI() + " : HTTP error code : " + response.getStatusLine().getStatusCode() + " : Error message :" + errorMessage);
            }

            String responseBody = tryGetResponseBody(response);
            LOGGER.debug("Response body: ");
            LOGGER.debug(responseBody);
            return responseBody;
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
        }
    }

    private static String getErrorMessage(HttpResponse response) throws Exception {
        String errorMsg = tryGetErrorFromJson(response);
        if (errorMsg != null) {
            return errorMsg;
        }
        return tryGetResponseBody(response);
    }

    private static String tryGetErrorFromJson(HttpResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ErrorJsonResponse error = mapper.readValue(response.getEntity().getContent(), ErrorJsonResponse.class);
            return error.getError();
        } catch (Exception e) {
            LOGGER.debug("Failed to get json error from response", e);
            return null;
        }
    }

    private static String tryGetResponseBody(HttpResponse response) {
        try {
            InputStreamReader content = new InputStreamReader((response.getEntity().getContent()));
            BufferedReader in = new BufferedReader(content);
            String inputLine;
            StringBuilder body = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();
            return body.toString();
        } catch (Exception e) {
            LOGGER.debug("Failed to get body from response", e);
            return "";
        }

    }

    public void setProxy(String host, String port, String username, String password) {
        if(host == null || host.isEmpty()) {
            return;
        }
        if(port == null || port.isEmpty()) {
            port = "80";
        }
        int portAsInt = Integer.parseInt(port);
        this.proxy = new HttpHost(host, portAsInt);
        if (username != null && !username.isEmpty() && password != null) {
            basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(
                    new AuthScope(host, portAsInt),
                    new UsernamePasswordCredentials(username, password));
        }
    }
}
