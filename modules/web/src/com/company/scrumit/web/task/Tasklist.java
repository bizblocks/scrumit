package com.company.scrumit.web.task;

import com.company.scrumit.entity.Priority;
import com.company.scrumit.entity.Sprint;
import com.company.scrumit.entity.Task;
import com.company.scrumit.entity.TaskType;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tasklist extends EntityCombinedScreen {
    private static final long ONEDAY = 24*60*60*1000;

    @Inject
    private TreeTable<Task> table;

    @Inject
    private DataManager dataManager;

    @Named("fieldGroup.duration")
    private TextField durationField;

    @Named("fieldGroup.deadline")
    private DateField deadlineField;

    @Named("fieldGroup.begin")
    private DateField beginField;
    @Named("fieldGroup.control")
    private CheckBox control;
    @Inject
    private HierarchicalDatasource<Task, UUID> tasksDs;
    @Inject
    private Datasource<Task> taskDs;
    @Inject
    private Metadata metadata;
    @Inject
    private CollectionDatasource trackerDs;
    @Inject
    private ExportDisplay exportDisplay;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
           if(beginField.getValue()==null)
               return;
           durationField.setValue((deadlineField.getValue().getTime()-beginField.getValue().getTime())/ONEDAY);
        });
        addBeforeCloseWithCloseButtonListener(event -> table.getDatasource().commit());
        addBeforeCloseWithShortcutListener(event -> table.getDatasource().commit());
    }


    public void onBtnCreateInGroupClick() {
        Task t = metadata.create(Task.class);
        t.setShortdesc("");
        t.setTask(table.getSingleSelected());
        t.setPriority(Priority.Middle);
        t.setType(TaskType.task);
        t.setDuration(1);
        dataManager.commit(t);
        tasksDs.refresh();
    }

    public void onMassInput(Component source) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("parent", table.getSingleSelected());
        openWindow("massInput", WindowManager.OpenType.DIALOG, map);
    }

    public void onBtnDoneClick() {
        Set<Task> tasks = table.getSelected();
        tasks.forEach(task -> {
            task.setDone(true);
            dataManager.commit(task);
        });
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    public void timeMoney() throws IOException {
        Set<Task> tasks = table.getSelected();
        HashMap<Sprint, List<Task>> tasksBySprint = new HashMap<>();
        for (Task task :
                tasks) {
            List<Sprint> sprints = task.getSprints();
            sprints.forEach(sprint -> {
               if (!tasksBySprint.containsKey(sprint)) {
                   tasksBySprint.put(sprint, new ArrayList<>());
               }
               tasksBySprint.get(sprint).add(task);
            });
        }

        HSSFWorkbook wb = new HSSFWorkbook();

        for (Sprint sprint:
             tasksBySprint.keySet()) {
            List<Task> sprintTasks = tasksBySprint.get(sprint);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
            String sheetName = String.format("%s - %s",
                    sdf.format(sprint.getPeriodStart()),
                    sdf.format(sprint.getPeriodEnd()));
            addSheet(wb, sheetName, sprintTasks);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        exportDisplay.show(new ByteArrayDataProvider(baos.toByteArray()), "test.xls", ExportFormat.XLS);
        wb.close();
    }

    private final static int tariff = 600;

    private void addSheet(HSSFWorkbook wb, String sheetName, List<Task> tasks) {
        HSSFSheet sheet = wb.createSheet(sheetName) ;

        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Тарификация (руб/час) :");
        row.createCell(1).setCellValue(tariff);

        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Наименование работы");
        row.createCell(1).setCellValue("Потрачено часов");
        row.createCell(2).setCellValue("Сумма (руб.");

        int currentRowNum = 3;
        for (Task task:
             tasks) {
            row = sheet.createRow(currentRowNum);
            row.createCell(0).setCellValue(task.getShortdesc());
            row.createCell(1).setCellValue(task.getActualTime());
            String formulaMoney = String.format("B%d*$B$1", currentRowNum + 1);
            row.createCell(2).setCellFormula(formulaMoney);
            currentRowNum++;
        }

        row = sheet.createRow(currentRowNum);
        row.createCell(0).setCellValue("Итого: ");
        String formulaActualTimeSum = String.format("SUM(B3:B%d)", currentRowNum - 1);
        String formulaMoneySum = String.format("SUM(C3:C%d)", currentRowNum - 1);
        row.createCell(1).setCellFormula(formulaActualTimeSum);
        row.createCell(2).setCellFormula(formulaMoneySum);
    }
}