package com.company.scrumit.web.screens;

import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.data.impl.CustomValueHierarchicalDatasource;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;

import java.util.*;

public class SmartsheetDatasource extends CustomValueHierarchicalDatasource {

    private HashMap<Long, SpecialKVEntity> loadedRows;
    private Sheet sheet;

    @Override
    protected Collection<KeyValueEntity> getEntities(Map<String, Object> params) {
        if(!params.containsKey("token"))
            return null;

        authorize(params.get("token").toString());

        List<Row> rows;
        try {
             rows = getRows(Long.valueOf(params.get("sheetId").toString()));
        } catch (SmartsheetException e) {
            e.printStackTrace();
            return null;
        }

        loadedRows = new HashMap<>();
        Collection<KeyValueEntity> res = new HashSet<>();
        rows.forEach(row -> res.add(rowToKeyValueEntity(row)));

        return res;
    }

    private List<Row> getRows(Long sheetId) throws SmartsheetException {
        sheet = getSheet(sheetId);
        return sheet.getRows();
    }

    private SpecialKVEntity rowToKeyValueEntity(Row row)
    {
        Long parentId = row.getParentId();
        Long id = row.getId();
        SpecialKVEntity e = new SpecialKVEntity(this);
        e.setIdName("id");
        e.setValue("id", id);
        if(parentId!=null)
            e.setValue("parent", loadedRows.get(parentId));
        else
            e.setValue("parent", null);
        row.getCells().forEach(cell -> {
            String title = sheet.getColumnById(cell.getColumnId()).getTitle();
            e.setValue(title, cell.getValue());
        });
        loadedRows.put(row.getId(), e);
        return e;
    }

    private Smartsheet smartsheet = null;

    private void authorize(String token) {
        smartsheet = SmartsheetFactory.createDefaultClient(token);
    }

    private Sheet getSheet(Long sheetId) throws SmartsheetException {
        return  smartsheet.sheetResources().getSheet(sheetId,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

}

