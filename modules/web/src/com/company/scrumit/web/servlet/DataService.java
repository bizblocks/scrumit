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
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DataService {
    public static final String ENC = "UTF-8";
    private static final String SCHEME = "http";
    private static final String HOST = "scrum.groupstp.ru:8080";
    private static final String PATH_GET_TOKEN = "/app/rest/v2/oauth/token";
    private static final String PATH_REST_SERICES = "http://scrum.groupstp.ru:8080/app/rest/v2/services";
    private static final String DB_URL = "jdbc:postgresql://localhost/scrumit";
    private static final String USER = "cuba";
    private static final String PASS = "cuba";
    private String accessToken;
    private String username;
    private String password;

    public String getAuthenticationData() throws IOException {
        if(accessToken == null) {
            try {
                login();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String secret = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet get = new HttpGet(PATH_REST_SERICES + "/scrumit_GitService/getAuthenticationData");
            get.setHeader("Authorization", "Bearer " + accessToken);

            secret = httpclient.execute(get, new StringResponseHandler());
        }
        return secret;
    }

    public void updateTrackerViaService(String commit, String author) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String authorEmail = URLEncoder.encode(author, ENC);
            String commitMessage = URLEncoder.encode(commit, ENC);

            HttpGet get = new HttpGet(PATH_REST_SERICES + "/scrumit_GitService/updateTracker?"
                    + "&commit=" + commitMessage
                    + "&authorEmail=" + authorEmail);
            get.setHeader("Authorization", "Bearer " + accessToken);

            httpclient.execute(get, new StringResponseHandler());
        }
    }

    public String getTelegramBotName() throws IOException {
        if(accessToken == null){
            try {
                login();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String botName = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet get = new HttpGet(PATH_REST_SERICES + "/scrumit_GitService/getTelegramBotName");
            get.setHeader("Authorization", "Bearer " + accessToken);

            botName = httpclient.execute(get, new StringResponseHandler());
        }
        return botName;
    }

    public String getTelegramBotToken() throws IOException {
        if(accessToken == null){
            try {
                login();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String botToken = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet get = new HttpGet(PATH_REST_SERICES + "/scrumit_GitService/getTelegramBotToken");
            get.setHeader("Authorization", "Bearer " + accessToken);

            botToken = httpclient.execute(get, new StringResponseHandler());
        }
        return botToken;
    }

    public String getTelegramChatId(String project) throws IOException {
        if(accessToken == null){
            try {
                login();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        String chatId = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            HttpGet get = new HttpGet(PATH_REST_SERICES + "/scrumit_GitService/getTelegramChatId?"
                    + "&project=" + project);
            get.setHeader("Authorization", "Bearer " + accessToken);

            chatId = httpclient.execute(get, new StringResponseHandler());
        }
        return chatId;
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

    private String login() throws IOException, URISyntaxException, SQLException {
        getLoginData();
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
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(params));

            String json = httpclient.execute(post, new StringResponseHandler());
            JSONObject jsonObject = new JSONObject(json);
            accessToken = jsonObject.getString("access_token");
        }
        return accessToken;
    }

    private Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void getLoginData() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pstmnt = null;
        List<String[]> result = new ArrayList<>();
        try {
            if (connection != null) {
                String selectUsername = "SELECT value_ FROM public.sys_config where name = 'payload.username'";
                pstmnt = connection.prepareStatement(selectUsername);
                ResultSet rs = pstmnt.executeQuery();
                while (rs.next()) {
                    username = rs.getString(1);
                }
                rs.close();
                pstmnt.clearParameters();

                String selectPassword = "SELECT value_ FROM public.sys_config where name = 'payload.password'";
                pstmnt = connection.prepareStatement(selectPassword);
                ResultSet rs1 = pstmnt.executeQuery();
                while (rs1.next()) {
                    password = rs1.getString(1);
                }
                rs1.close();
                pstmnt.clearParameters();

            }
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        } finally {
            if (pstmnt != null) {
                pstmnt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
