package com.company.scrumit.web.servlet;

import com.company.scrumit.config.ScrumitWebConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.inject.Inject;

public class TelegramBot extends TelegramLongPollingBot {
    @Inject
    private ScrumitWebConfig config;

    @Override
    public String getBotUsername() {
        return config.getTelegramBotName();
    }

    @Override
    public String getBotToken() {
        return config.getTelegramBotToken();
        //Токен бота
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

    public void sendMsg(String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(config.getTelegramChatId());
        sendMessage.setText(text);
        try{
            execute(sendMessage);
        } catch(TelegramApiException e){

        }
    }
}
