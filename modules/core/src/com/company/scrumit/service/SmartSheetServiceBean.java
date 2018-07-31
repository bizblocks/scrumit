package com.company.scrumit.service;

import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Sheet;
import org.springframework.stereotype.Service;

@Service(SmartSheetService.NAME)
public class SmartSheetServiceBean implements SmartSheetService {
    private Smartsheet smartsheet= null;

    public void authorize(String token) {
        smartsheet = SmartsheetFactory.createDefaultClient(token);
    }

    public Sheet getSheet(Long sheetId) throws SmartsheetException {
        return  smartsheet.sheetResources().getSheet(sheetId,
                null,
                null,
                null,
                null,
                null,
                0,
                0);
    }

}