package com.company.scrumit.web.tracker.workflow;

import com.company.scrumit.web.tracker.TabType;
import com.company.scrumit.web.tracker.workflow.frame.TrackerWorkflowBrowseTableFrame;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.settings.Settings;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.haulmont.cuba.gui.ComponentsHelper.walkComponents;

public class TrackerWorkflowBrowse extends AbstractLookup {

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private TabSheet tabSheet;

    private Map<String, LazyTab> tabsCache = new HashMap<>();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        //инициализировать вкладки
        initTabSheets();
    }


    @Override
    public void ready() {
        super.ready();
        //загрузить выбранную вкладку
        initTabSelection();
    }

    private void initTabSheets() {
        //показать вкладку с новыми записями
        showNewTrackerRecords();
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


    private void setupSelectedLazyTab() {
        TabSheet.Tab tab = tabSheet.getSelectedTab();
        if (tab != null) {
            LazyTab lazyTab = tabsCache.get(tab.getName());
            if (lazyTab != null && !lazyTab.isLoaded()) {
                lazyTab.getFrame();
            }
        }
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

    private void showNewTrackerRecords() {
        String key = getKey(TabType.NEW.getId());
        LazyTab lazyTab = new LazyTab(e -> createTab(TabType.NEW, false, Collections.emptyMap()));
        tabsCache.put(key, lazyTab);
        TabSheet.Tab tab = tabSheet.addTab(key, lazyTab.getBox());
        tab.setCaption(getMessage("newRecords"));
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
        return (TrackerWorkflowBrowseTableFrame) openFrame(null, TrackerWorkflowBrowseTableFrame.SCREEN_ID, params);
    }


    /**
     * Lazy frame table tab
     */
    private final class LazyTab {
        private BoxLayout box;
        private Function<BoxLayout, TrackerWorkflowBrowseTableFrame> creator;
        private TrackerWorkflowBrowseTableFrame frame;

        LazyTab(Function<BoxLayout, TrackerWorkflowBrowseTableFrame> creator) {
            this.creator = creator;
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