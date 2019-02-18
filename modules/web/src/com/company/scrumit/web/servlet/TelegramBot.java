package com.company.scrumit.web.servlet;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class TelegramBot {

    private final String token;

    public TelegramBot(String token){
        this.token = token;
    }


    public void sendMessage(String chatId, String text) {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        text = text.replace(" ", "+");
        text = text.replace("\n", "%0A");
        urlString = String.format(urlString, token, chatId, text);
        performUrlString(urlString);
    }

    private String performUrlString(String urlString){
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            String response = sb.toString();
            return response;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return urlString;
    }
}
