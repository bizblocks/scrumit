package com.company.scrumit.service;


import java.util.UUID;

public interface UrlService {
    String NAME = "scrumit_UrlService";

    enum Command{
        OPEN,
        O
    }

    String MakeOpenUrl(Command command, String screen, UUID entityId);
}