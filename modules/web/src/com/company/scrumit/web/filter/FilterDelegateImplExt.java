package com.company.scrumit.web.filter;

import com.google.common.base.Strings;
import com.haulmont.bali.datastruct.Node;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.filter.ConditionsTree;
import com.haulmont.cuba.gui.components.filter.FilterDelegateImpl;
import com.haulmont.cuba.gui.components.filter.FtsFilterHelper;
import com.haulmont.cuba.gui.components.filter.condition.CustomCondition;
import com.haulmont.cuba.gui.icons.CubaIcon;
import org.eclipse.persistence.internal.sessions.DirectCollectionChangeRecord;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adiatullin
 */
public class FilterDelegateImplExt extends FilterDelegateImpl {
    protected Button resetAll;

    @Override
    protected void createControlsLayoutForFts() {
        super.createControlsLayoutForFts();
        resetAll = componentsFactory.createComponent(Button.class);
        resetAll.setCaption(messages.getMessage(getClass(), "resetFilter"));
        resetAll.setIcon(CubaIcon.CANCEL.source());
        resetAll.setAction(new AbstractAction("reset") {
            @Override
            public void actionPerform(Component component) {
                resetFts();
            }
        });
        controlsLayout.add(resetAll, 2);
    }

    @Override
    @PostConstruct
    public void init(){
        super.init();
        if(isFtsModeEnabled()) {
            filterMode= FilterMode.FTS_MODE;
            createLayout();
        }
    }

    protected void reset() {
        conditions = new ConditionsTree();
        setFilterEntity(adHocFilter);
        apply(true);
    }

    protected void resetFts() {
        reset();
        ftsSearchCriteriaField.setValue(null);
        datasource.refresh();
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if(resetAll!=null)
            resetAll.setVisible(editable && userCanEditFilers());
    }

    @Inject
    private WindowManager windowManager;

    @Override
    protected void applyFts() {
        if (ftsFilterHelper == null)
            return;

        if (beforeFilterAppliedHandler != null) {
            if (!beforeFilterAppliedHandler.beforeFilterApplied()) return;
        }

        String searchTerm = ftsSearchCriteriaField.getValue();
        if (Strings.isNullOrEmpty(searchTerm)) {
            windowManager.showNotification(getMainMessage("filter.fillSearchCondition"), Frame.NotificationType.TRAY);
            if (afterFilterAppliedHandler != null) {
                afterFilterAppliedHandler.afterFilterApplied();
            }
            return;
        }
        else if (searchTerm.length()<2){
            windowManager.showNotification(getMainMessage("2_letters_min"), Frame.NotificationType.TRAY);
            if (afterFilterAppliedHandler != null) {
                afterFilterAppliedHandler.afterFilterApplied();
            }
            return;
        }

        Map<String, Object> params = new HashMap<>();

        if (!Strings.isNullOrEmpty(searchTerm)) {
            FtsFilterHelper.FtsSearchResult ftsSearchResult = ftsFilterHelper.search(searchTerm, datasource.getMetaClass().getName());
            int queryKey = ftsSearchResult.getQueryKey();
            params.put(FtsFilterHelper.SESSION_ID_PARAM_NAME, userSessionSource.getUserSession().getId());
            params.put(FtsFilterHelper.QUERY_KEY_PARAM_NAME, queryKey);

            CustomCondition ftsCondition = ftsFilterHelper.createFtsCondition(datasource.getMetaClass().getName());
            conditions = new ConditionsTree();
            conditions.getRootNodes().add(new Node<>(ftsCondition));

            if ((applyTo != null) && (Table.class.isAssignableFrom(applyTo.getClass()))) {
                filterHelper.initTableFtsTooltips((Table) applyTo, ftsSearchResult.getHitInfos());
            }
        } else if ((applyTo != null) && (Table.class.isAssignableFrom(applyTo.getClass()))) {
            filterHelper.initTableFtsTooltips((Table) applyTo, Collections.emptyMap());
        }

        applyDatasourceFilter();
        initDatasourceMaxResults();
        datasource.refresh(params);

        if (afterFilterAppliedHandler != null) {
            afterFilterAppliedHandler.afterFilterApplied();
        }
    }
}
