package com.company.scrumit.service;


import org.springframework.stereotype.Service;

public interface GitService {
    String NAME = "scrumit_GitService";

    String getAuthenticationData();
    void updateTracker(String project, String commit, String authorEmail);
}