package com.company.scrumit.web.tracker.workflow;

import com.company.scrumit.entity.Tracker;
import com.company.scrumit.web.tracker.TabType;
import com.company.scrumit.web.tracker.workflow.frame.TrackerWorkflowBrowseTableFrame;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Step;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.event.WorkflowEvent;
import com.groupstp.workflowstp.web.bean.WorkflowWebBean;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.springframework.context.event.EventListener;


import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;

import static com.haulmont.cuba.gui.ComponentsHelper.walkComponents;

public class TrackerWorkflowBrowse extends AbstractLookup {

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    private TabSheet tabSheet;
    @Inject
    private WorkflowWebBean workflowWebBean;

    private User user;

    private Map<String, LazyTab> tabsCache = new HashMap<>();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        user = getUser();
        //инициализировать вкладки
        initTabSheets(getActiveWorkflows());
    }

    private void initTabSheets(List<Workflow> workflows) {
        //показать вкладку с новыми записями
        showNewTrackerRecords();
        //показать вкладки рабочего процесса
        showWorkflowSpecificTabs(workflows);
    }

    //other workflows queries tabs init
    private void showWorkflowSpecificTabs(List<Workflow> workflows) {
        if (!CollectionUtils.isEmpty(workflows)) {

            List<Stage> stages = new ArrayList<>();

            int j = 0;
            for (Workflow workflow : workflows) {
                if (!CollectionUtils.isEmpty(workflow.getSteps())) {
                    for (int i = 0; i < workflow.getSteps().size(); i++) {
                        Step step = workflow.getSteps().get(i);
                        if (!stages.contains(step.getStage())) {
                            if (i + j >= stages.size()) {
                                stages.add(step.getStage());
                            } else {
                                stages.add(i + j, step.getStage());
                            }
                        }
                    }
                }
                j++;
            }

            if (!CollectionUtils.isEmpty(stages)) {
                for (Stage stage : stages) {
                    boolean actor = workflowWebBean.isActor(user, stage);
                    boolean viewer = workflowWebBean.isViewer(user, stage);
                    if (actor || viewer) {
                        String stageName = stage.getName();
                        String key = getKey(stageName);
                        LazyTab lazyTab = new LazyTab(e -> createTab(TabType.WORKFLOW, !actor,
                                ParamsMap.of(TrackerWorkflowBrowseTableFrame.STAGE, stage, TrackerWorkflowBrowseTableFrame.USER, user)
                        ));
                        tabsCache.put(key, lazyTab);
                        TabSheet.Tab tab = tabSheet.addTab(key, lazyTab.getBox());
                        tab.setCaption(stageName);
                    }
                }
            }
        }
    }

    private void showNewTrackerRecords() {
        String key = getKey(TabType.NEW.getId());
        LazyTab lazyTab = new LazyTab(e -> createTab(TabType.NEW, false, Collections.emptyMap()));
        tabsCache.put(key, lazyTab);
        TabSheet.Tab tab = tabSheet.addTab(key, lazyTab.getBox());
        tab.setCaption(getMessage("trackerWorkflowBrowse.newRecords"));
    }

    private String getKey(String stageName) {
        return stageName.replaceAll("\\s", StringUtils.EMPTY).toLowerCase();
    }

    /**
     * initialize and open specified queries tab
     */
    private TrackerWorkflowBrowseTableFrame createTab(TabType tabType,
                                                      boolean viewOnly,
                                                      Map<String, Object> additionalParams) {
        Map<String, Object> params = new HashMap<>(additionalParams);
        params.put(TrackerWorkflowBrowseTableFrame.TAB_TYPE, tabType);
        params.put(TrackerWorkflowBrowseTableFrame.VIEW_ONLY, viewOnly);
        //direct init frame
        TrackerWorkflowBrowseTableFrame res = (TrackerWorkflowBrowseTableFrame) openFrame(null, TrackerWorkflowBrowseTableFrame.SCREEN_ID, params);
        return res;
    }

    @Override
    public void ready() {
        super.ready();
        //загрузить выбранную вкладку
        initTabSelection();
    }

    private void initTabSelection() {
        //обработчик переключения вкладок
        tabSheet.addSelectedTabChangeListener(event -> {
            //очистить выделение
            clearSelection();
            //показать содержимое выбранной вкладки
            setupSelectedLazyTab();
        });
        //загрузить выбранную вкладку
        setupSelectedLazyTab();
    }

    private void clearSelection() {
        TabSheet.Tab tab = tabSheet.getSelectedTab();
        if (tab != null) {
            LazyTab lazyTab = tabsCache.get(tab.getName());
            if (lazyTab != null) {
                lazyTab.getFrame().clearSelection();
            }
        }
    }

    private void setupSelectedLazyTab() {
        TabSheet.Tab tab = tabSheet.getSelectedTab();
        if (tab != null) {
            LazyTab lazyTab = tabsCache.get(tab.getName());
            if (lazyTab != null && !lazyTab.isLoaded()) {
                lazyTab.getFrame();
            }
        }
    }


    @EventListener
    protected void onWorkflowEvent(WorkflowEvent event) {
        Set<String> stagesToUpdate = new HashSet<>();
        if (!StringUtils.isEmpty(event.getCurrentStage())) {
            stagesToUpdate.add(event.getCurrentStage());
        }
    }

    private User getUser() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        if (user == null) {
            throw new DevelopmentException(getMessage("queryWorkflowBrowseTableFrame.userNotFound"));
        }
        return user;
    }

    @Nullable
    private List<Workflow> getActiveWorkflows() {
        String entityName = metadata.getClassNN(Tracker.class).getName();
        List<Workflow> list = dataManager.loadList(LoadContext.create(Workflow.class)
                .setQuery(new LoadContext.Query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.entityName = :entityName order by e.order asc")
                        .setParameter("entityName", entityName))
                .setView("tracker-workflow-browse"));
        if (!CollectionUtils.isEmpty(list)) {
            return list;
        }
        return null;
    }

    /**
     * Lazy frame table tab
     */
    private final class LazyTab {
        private BoxLayout box;
        private Function<BoxLayout, TrackerWorkflowBrowseTableFrame> creator;
        private TrackerWorkflowBrowseTableFrame frame;

        LazyTab(Function<BoxLayout, TrackerWorkflowBrowseTableFrame> creator) {
            //функция, возваращающая фрейм
            this.creator = creator;
            //место для размещения фрейма
            this.box = createBox();
        }

        private BoxLayout createBox() {
            BoxLayout box = componentsFactory.createComponent(VBoxLayout.class);
            box.setWidth("100%");
            box.setHeight("100%");
            return box;
        }

        BoxLayout getBox() {
            return box;
        }

        boolean isLoaded() {
            return frame != null;
        }

        TrackerWorkflowBrowseTableFrame getFrame() {
            if (frame == null) {
                frame = creator.apply(box);
                box.add(frame);
                frame.setWidth("100%");
                frame.setHeight("100%");

                final String id = frame.getId();
                final Settings settings = getSettings();
                walkComponents(frame, (component, name) -> {
                    if (component.getId() != null && component instanceof HasSettings) {
                        Element e = settings.get(id + "." + name);
                        if (e != null) {
                            ((HasSettings) component).applySettings(e);
                            if (component instanceof Component.HasPresentations && e.attributeValue("presentation") != null) {
                                final String def = e.attributeValue("presentation");
                                if (!StringUtils.isEmpty(def)) {
                                    UUID defaultId = UUID.fromString(def);
                                    ((Component.HasPresentations) component).applyPresentationAsDefault(defaultId);
                                }
                            }
                        }
                    }
                });
            }
            return frame;
        }
    }
}