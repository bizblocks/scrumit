package com.company.scrumit.web.servlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.*;
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

    public TelegramBot telegramBot;

    private void registerBot(){
//        System.getProperties().put( "proxySet", "true" );
//        System.getProperties().put( "socksProxyHost", "127.0.0.1" );
//        System.getProperties().put( "socksProxyPort", "9150" );
        ApiContextInitializer.init();
        telegramBot = new TelegramBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

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
        DataService dataService = new DataService();
        String secret = dataService.getAuthenticationData();
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
                        boolean distinct = commit.getBoolean("distinct");
                        if(distinct) {
                            String message = commit.getString("message");
                            String author = commit.getJSONObject("author").getString("email");
                            dataService.updateTrackerViaService(message, author);
                            String branch = json.getString("ref");
                            branch = branch.split("/")[branch.split("/").length - 1];
                            String msg = message + "\nUser: " + json.getJSONObject("pusher").getString("name") + "\nProject: " + project + "\nBranch: " + branch;
                            if (telegramBot == null)
                                registerBot();
                            String chatId = dataService.getTelegramChatId(project);
                            if (chatId != null)
                                telegramBot.sendMsg(chatId, msg);
                        }
                    }
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
            boolean bool = computedHash.equals(expected);
            return bool;
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }
        return false;
    }
}
