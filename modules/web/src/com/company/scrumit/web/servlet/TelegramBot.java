package com.company.scrumit.web.servlet;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class TelegramBot extends TelegramLongPollingBot {

    DataService dataService = new DataService();

    @Override
    public String getBotUsername() {
        try {

            return dataService.getTelegramBotName();
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public String getBotToken() {
        try {
            return dataService.getTelegramBotToken(); //Токен бота
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if(message != null && message.hasText()){
            switch (message.getText()){
                case "test":
                    sendReplyToMsg(message, "Hello!");
                    break;
                default: break;
            }
        }
    }

    public void sendReplyToMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        System.out.println(message.getChatId().toString());
        try{
            execute(sendMessage);
        } catch(TelegramApiException e){

        }
    }

    public void sendMsg(String chatId, String text){
        try{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(text);
            execute(sendMessage);
        } catch(TelegramApiException e){

        }
    }
}
