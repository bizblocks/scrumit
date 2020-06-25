package com.company.scrumit.web.screens;

import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.settings.Settings;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;
import org.dom4j.Element;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Smartsheetexport extends AbstractLookup {

    @Inject
    private HierarchicalDatasource<KeyValueEntity, Object> ssDs;

    private long sheetId;
    private long taskId;
    private Sprint sprint;

    @Inject
    private TextField token;


    @Inject
    private TreeTable tab;

    /**
     * Called by the framework after creation of all components and before showing the screen.
     * <br> Override this method and put initialization logic here.
     *
     * @param params parameters passed from caller's code, usually from
     *               {@link #openWindow(String, WindowManager.OpenType)} and similar methods, or set in
     *               {@code screens.xml} for this registered screen
     */
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if(params.containsKey("sheetId"))
            sheetId = Long.valueOf(params.get("sheetId").toString());
        else
            sheetId = 0;

        if(params.containsKey("taskId"))
            taskId = Long.valueOf(params.get("taskId").toString());
        else
            taskId = 0;

        if(params.containsKey("sprint"))
            sprint = (Sprint) params.get("sprint");
        else
            sprint = null;
        }

    /**
     * Hook to be implemented in subclasses. <br>
     * Called by the framework after the screen is fully initialized and opened. <br>
     * Override this method and put custom initialization logic here.
     */
    @Override
    public void ready() {
        super.ready();
        if(sprint!=null) {
            try {
                export();
            } catch (SmartsheetException e) {
                showNotification(e.getMessage());
            }
            close(Window.CLOSE_ACTION_ID);
        }
    }

    public void refresh()
    {
        if(sprint!=null)
            return;
        Map<String, Object> p = new HashMap<>();
        p.put("token", token.getValue());
        p.put("sheetId", sheetId);
        ssDs.refresh(p);
    }

    /**
     * This method is called when the screen is closed to save the screen settings to the database.
     */
    @Override
    public void saveSettings() {
        Element x = getSettings().get(this.getId());
        x.addAttribute("token", token.getRawValue());
        getSettings().setModified(true);
        super.saveSettings();
    }

    /**
     * This method is called when the screen is opened to restore settings saved in the database for the current user.
     * <p>You can override it to restore custom settings.
     * <p>For example:
     * <pre>
     * public void applySettings(Settings settings) {
     *     super.applySettings(settings);
     *     String visible = settings.get(hintBox.getId()).attributeValue("visible");
     *     if (visible != null)
     *         hintBox.setVisible(Boolean.valueOf(visible));
     * }
     * </pre>
     *
     * @param settings settings object loaded from the database for the current user
     */
    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        token.setValue(settings.get(this.getId()).attributeValue("token"));
        if(sheetId!=0)
            refresh();
    }

    private static HashMap<String, Long> columns = new HashMap<>();
    private static HashMap<String, Task> tasks = new HashMap<>();

    @Inject
    private DataManager dataManager;

    private void export() throws SmartsheetException {
        Smartsheet ss = SmartsheetFactory.createDefaultClient((String) token.getValue());
        Sheet sheet = ss.sheetResources().getSheet(sheetId,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        AtomicInteger taskIdx = new AtomicInteger();

        sheet.getColumns().forEach(col-> {
            columns.put(col.getTitle(), col.getId());
            if("Задачи".equals(col.getTitle()))
                taskIdx.set(col.getIndex());
        });

        List<Row> rows = ss.sheetResources().rowResources().addRows(sheetId, taskList());

        rows.forEach(row -> {
            Task t = tasks.get(row.getCells().get(taskIdx.get()).getDisplayValue());
            if(t==null)
                return;
            t.setSsId(row.getId());
            dataManager.commit(t);
        });

        rows.forEach(row -> {
            Task t = tasks.get(row.getCells().get(taskIdx.get()).getDisplayValue());
            if(t==null)
                return;
            Long ssid = t.getTask()==null ? null : t.getTask().getSsId();
            if(ssid==null)
                return;
            List<Row> rowsToUpdate = new LinkedList<>();
            Row updRow = new Row();
            updRow.setId(row.getId());
            updRow.setParentId(ssid);
            rowsToUpdate.add(updRow);
            try {
                ss.sheetResources().rowResources().updateRows(sheetId, rowsToUpdate);
            } catch (SmartsheetException e) {
                showNotification(t.getShortdesc()+":"+e.getMessage(), NotificationType.TRAY);
            }
        });

    }

    private List<Row> taskList()
    {
        List<Row> rows = new LinkedList<>();
        for(Task t:sprint.getTasks())
        {
            List<Cell> cells = new LinkedList<>();
            if(t.getShortdesc()==null)
                continue;
            cells.add(createCell("Задачи", t.getShortdesc()));
            if(t.getBegin()!=null)
                cells.add(createCell("Начало", ssFormatDate(t.getBegin())));
            if(t.getDeadline()!=null)
                cells.add(createCell("Период", t.getBegin().compareTo(t.getDeadline())+ 1));
            Row row = new Row();
            row.setCells(cells);
            row.setParentId(taskId);
            rows.add(row);
            tasks.put(t.getShortdesc(), t);
        }
        return rows;
    }

    private static Cell createCell(String colName, Object value)
    {
        Cell cell = new Cell();
        cell.setColumnId(columns.get(colName));
        cell.setValue(value);
        cell.setStrict(false);
        return cell;
    }

    private static String ssFormatDate(Date d) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(d);
    }
}