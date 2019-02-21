package com.company.scrumit.web.servlet;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TelegramBot {

    private final String token;

    public static final String CHATID_FIELD = "chat_id";
    public static final String TEXT_FIELD = "text";

    public TelegramBot(String token){
        this.token = token;
    }

    public void sendMessage(String chatId, String text) {
        String urlString = "https://api.telegram.org/bot%s/sendMessage";
        urlString = String.format(urlString, token);

        String url = urlString;
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(CHATID_FIELD, chatId));
        params.add(new BasicNameValuePair(TEXT_FIELD, text));

        UrlEncodedFormEntity ent = null;
        try {
            ent = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(ent);
            HttpClient client = new DefaultHttpClient();
            HttpResponse responsePOST = client.execute(httppost);
            System.out.println(responsePOST);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
