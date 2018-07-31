package com.company.scrumit.service;


import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.models.Sheet;

public interface SmartSheetService {
    String NAME = "scrumit_SmartSheetService";
    void authorize(String token);
    Sheet getSheet(Long sheetId) throws SmartsheetException;
}