package com.company.scrumit.web.servlet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.*;

public class PayloadServlet extends HttpServlet{

    static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    public static final String ENC = "UTF-8";
    private static final String SCHEME = "http";
    private static final String HOST = "localhost:8080";
    private static final String PATH_GET_TOKEN = "/app/rest/v2/oauth/token";
    private String accessToken;

    // функция обработки метода GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // set response headers
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        // create HTML form
        PrintWriter writer = response.getWriter();
        writer.append("<!DOCTYPE html>\r\n")
                .append("<html>\r\n")
                .append("		<head>\r\n")
                .append("			<title>Form input</title>\r\n")
                .append("		</head>\r\n")
                .append("		<body>\r\n")
                .append("			<div>\r\n")
                .append("				Hello! \r\n")
                .append("			</div>\r\n")
                .append("		</body>\r\n")
                .append("</html>\r\n");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            login();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String secret = getAuthenticationData();
        if(secret != null) {
            String signature = req.getHeader("X-Hub-Signature").split("=")[1];
            StringBuilder builder = new StringBuilder();
            String aux = "";

            while ((aux = req.getReader().readLine()) != null) {
                builder.append(aux);
            }

            String text = builder.toString();

            if(verifyPayload(text.getBytes(StandardCharsets.UTF_8), secret, signature)) {
                try {
                    JSONObject json = new JSONObject(text);
                    String project = json.getJSONObject("repository").getString("name");
                    JSONArray commits = json.getJSONArray("commits");
                    for (int i = 0; i < commits.length(); i++) {
                        JSONObject commit = commits.getJSONObject(i);
                        String message = commit.getString("message");
                        String author = commit.getJSONObject("author").getString("email");
                        updateTrackerViaService(project, message, author);
                        /*try {
                            updateTracker(project, message, author);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }*/
                    }
                    System.out.println("project: " + project);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private Boolean verifyPayload(byte[] payloadBytes, String secret, String expected) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(payloadBytes);

            String computedHash = toHexString(result);
            System.out.println("Computed hash: " + computedHash);
            boolean bool = computedHash.equals(expected);
            return bool;
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }
        return false;
    }

    private String getAuthenticationData() throws IOException {
        String secret = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet get = new HttpGet("http://localhost:8080/app/rest/v2/services/scrumit_GitService/getAuthenticationData");
            get.setHeader("Authorization", "Bearer " + accessToken);

            secret = httpclient.execute(get, new StringResponseHandler());
        }
        return secret;
    }

    private void updateTrackerViaService(String project, String commit, String author) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String projectName = URLEncoder.encode(project, ENC);
            String authorEmail = URLEncoder.encode(author, ENC);
            String commitMessage = URLEncoder.encode(commit, ENC);

            HttpGet get = new HttpGet("http://localhost:8080/app/rest/v2/services/scrumit_GitService/updateTracker?"
                    + "project=" + projectName
                    + "&commit=" + commitMessage
                    + "&authorEmail=" + authorEmail);
            get.setHeader("Authorization", "Bearer " + accessToken);


            System.out.println("Executing request " + get.getRequestLine());

            String customerId = httpclient.execute(get, new StringResponseHandler());
        }
    }

    private static class StringResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    }

    private void login() throws IOException, URISyntaxException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            URIBuilder builder = new URIBuilder();
            builder.setScheme(SCHEME).setHost(HOST).setPath(PATH_GET_TOKEN);
            URI uri = builder.build();

            HttpPost post = new HttpPost(uri);

            // see cuba.rest.client.id and cuba.rest.client.secret application properties
            String credentials = Base64.getEncoder().encodeToString("client:secret".getBytes(ENC));
            post.setHeader("Authorization", "Basic " + credentials);

            // user credentials
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("username", "admin"));
            params.add(new BasicNameValuePair("password", "admin"));
            post.setEntity(new UrlEncodedFormEntity(params));

            String json = httpclient.execute(post, new StringResponseHandler());
            JSONObject jsonObject = new JSONObject(json);
            accessToken = jsonObject.getString("access_token");

        }
    }
}
