package com.company.scrumit.service;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ImportCSVService {
    String NAME = "scrumit_ImportCSVService";
    List<HashMap<String, String>> parseCSV(String csv) throws IOException;

    void importContacts(List<HashMap<String, String>> data);
}