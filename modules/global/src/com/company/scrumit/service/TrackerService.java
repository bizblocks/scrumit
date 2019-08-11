package com.company.scrumit.service;


import java.util.UUID;

public interface TrackerService {
    String NAME = "scrumit_TrackerService";

    String MakeOpenUrl(String screen, UUID entityId);
}