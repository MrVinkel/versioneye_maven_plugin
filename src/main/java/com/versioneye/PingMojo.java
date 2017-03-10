package com.versioneye;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Ping the VersionEye API. Expects a pong in response.
 */
@Mojo(name = "ping", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class PingMojo extends SuperMojo {


    @Override
    public void doExecute() throws Exception {
        //todo proxy
        setProxy();
        initTls();
        //todo api
        InputStream inputStream = getInputStream(fetchBaseUrl() + apiPath + "/services/ping");
        BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        getLog().info("");
        while ((line = input.readLine()) != null) {
            getLog().info(line);
        }
        getLog().info("");
        input.close();
    }

    //todo api
    private InputStream getInputStream(String urlPath) throws Exception {
        URL url = new URL(urlPath);
        if (urlPath.startsWith("https")) {
            System.out.println("https");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            return con.getInputStream();
        } else {
            System.out.println("http");
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        }
    }

}
