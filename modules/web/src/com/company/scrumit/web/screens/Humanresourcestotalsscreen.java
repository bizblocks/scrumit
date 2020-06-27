package com.company.scrumit.web.screens;

import com.company.scrumit.entity.HumanResourcesAccount;
import com.company.scrumit.entity.Performer;
import com.company.scrumit.entity.Task;
import com.company.scrumit.utils.StringUtil;
import com.company.scrumit.web.aggregations.TotalDurationAggregationStrategy;
import com.company.scrumit.web.humanresourcesaccount.HumanResourcesAccountBrowse;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.model.KeyValueCollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.sql.Date;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UiController("scrumit_Humanresourcestotalsscreen")
@UiDescriptor("HumanResourcesTotalsScreen.xml")
public class Humanresourcestotalsscreen extends Screen {


    @Inject
    private DataManager dataManager;
    @Inject
    private DateField<Date> beginDateField;
    @Inject
    private DateField<Date> endDateField;
    @Inject
    private Notifications notifications;
    @Inject
    private Messages messages;
    @Inject
    private StringUtil stringUtil;
    @Inject
    private KeyValueCollectionContainer totalsData;
    @Inject
    private Metadata metadata;
    @Inject
    private InstanceContainer<HumanResourcesAccount> humanResourcesAccountDc;
    @Inject
    private CollectionLoader<Performer> performersDl;
    @Inject
    private GroupTable totalsTable;
    @Inject
    private ScreenBuilders screenBuilders;

    private Map<Task,List<HumanResourcesAccount>> result = new HashMap<>();
    @Inject
    private Button showDetailsBtn;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private MetadataTools metadataTools;


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event){
        performersDl.load();

    }
    @Subscribe
    protected void onInit(InitEvent event) {
        HumanResourcesAccount account = metadata.create(HumanResourcesAccount.class);
        humanResourcesAccountDc.setItem(account);

        Action showDetailsAction = new BaseAction("showDetails"){
            @Override
            protected boolean isPermitted() {
                if (super.isPermitted())
                   return totalsTable.getSingleSelected()!=null;
                else
                    return false;

            }

            @Override
            public void actionPerform(Component component) {
               onShowDetailsBtnClick();
            }
        };
        showDetailsBtn.setAction(showDetailsAction);
        totalsTable.addAction(showDetailsAction);

        totalsTable.addGeneratedColumn("totalDuration",entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            long durationMilis = entity.getValue("totalDuration");
            label.setValue(stringUtil.formatDurationToString(Duration.ofMillis(durationMilis)));
            return label;
        });
        MetaClass metaClass = totalsData.getEntityMetaClass();
        MetaPropertyPath propertyPath = metaClass.getPropertyPath("totalDuration");
        AggregationInfo info = new AggregationInfo();
        info.setPropertyPath(propertyPath);
        info.setStrategy(new TotalDurationAggregationStrategy());
        totalsTable.getColumn("totalDuration").setAggregation(info);


    }



    public void onRefreshTotalsTableClick() {
        if (beginDateField.isEmpty() || endDateField.isEmpty() || humanResourcesAccountDc.getItem().getPerformer() == null){
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messages.getMessage("com.company.scrumit.web.screens","enter_conditions"))
                    .show();
            return;
        }
        List<HumanResourcesAccount> resourcesAccounts = dataManager.load(HumanResourcesAccount.class)
                .query("select e from scrumit_HumanResourcesAccount e where e.startTime >= :begin and e.startTime <= :end and e.performer = :performer")
                .parameter("begin", beginDateField.getValue())
                .parameter("end",endDateField.getValue() )
                .parameter("performer", humanResourcesAccountDc.getItem().getPerformer())
                .view("humanResourcesAccount-view")
                .list();


        resourcesAccounts.forEach(humanResourcesAccount -> {
            if (result.get(humanResourcesAccount.getTask()) == null){
                result.put(humanResourcesAccount.getTask(),new ArrayList<>());
                result.get(humanResourcesAccount.getTask()).add(humanResourcesAccount);
            }else {
                result.get(humanResourcesAccount.getTask()).add(humanResourcesAccount);
            }
        });
        List<KeyValueEntity> resultList = new ArrayList<>();
        result.entrySet().forEach(taskListEntry -> {
            KeyValueEntity entity = new KeyValueEntity();
            entity.setValue("emp",humanResourcesAccountDc.getItem().getPerformer());
            entity.setValue("task", taskListEntry.getKey());
            long durationMilis = 0L;
            for (HumanResourcesAccount humanResourcesAccount : taskListEntry.getValue()) {
                durationMilis +=humanResourcesAccount.getEndTIme().getTime() - humanResourcesAccount.getStartTime().getTime();
            }
            Duration duration = Duration.ofMillis(durationMilis);
            entity.setValue("totalDuration",durationMilis);
            long aberrance;
            if (taskListEntry.getKey().getPlanningTime() != null){
                 aberrance = taskListEntry.getKey().getPlanningTime() - duration.toMinutes();
            }else {
                 aberrance = 0;
            }

            if (aberrance > 0)
                entity.setValue("aberrance", "меньше на " + stringUtil.formatDurationToString(Duration.ofMinutes(Math.abs(aberrance))));
            else if (aberrance < 0)
                entity.setValue("aberrance", "больше на " + stringUtil.formatDurationToString(Duration.ofMinutes(Math.abs(aberrance))));
            else
                entity.setValue("aberrance", "0");

            resultList.add(entity);
        });
        totalsData.setItems(resultList);
        totalsTable.repaint();

    }

    public void onShowDetailsBtnClick() {
        KeyValueEntity selected = (KeyValueEntity) totalsTable.getSingleSelected();
        HumanResourcesAccountBrowse accountBrowse = screenBuilders.screen(this)
                .withScreenClass(HumanResourcesAccountBrowse.class)
                .build();
        Task selectedTask = selected.getValue("task");
        accountBrowse.setHumanAccaunts(result.get(selectedTask));
        accountBrowse.show();
    }
}