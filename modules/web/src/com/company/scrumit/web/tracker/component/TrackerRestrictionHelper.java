package com.company.scrumit.web.tracker.component;


import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.util.EqualsUtils;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Helpful class to check workflow stages user access
 *
 * @author adiatullin
 */
@Component
public class TrackerRestrictionHelper {

    @Inject
    private DataManager dataManager;

    /**
     * Check what provided user is actor of this stage
     *
     * @param user  user to check
     * @param stage stage to access
     * @return is actor user or not
     */
    public boolean isActor(User user, Stage stage) {
        if (stage != null && user != null) {
            if (EqualsUtils.equalAny(stage.getType(), StageType.USERS_INTERACTION, StageType.ARCHIVE)) {
                if (!CollectionUtils.isEmpty(stage.getActorsRoles())) {
                    user = reloadIfNeed(user, "user-with-role");
                    if (!CollectionUtils.isEmpty(user.getUserRoles())) {
                        for (UserRole ur : user.getUserRoles()) {
                            if (stage.getActorsRoles().contains(ur.getRole())) {
                                return true;
                            }
                        }
                    }
                } else if (!CollectionUtils.isEmpty(stage.getActors())) {
                    return stage.getActors().contains(user);
                }
            }
        }
        return false;
    }

    /**
     * Check what provided user is viewer of this stage
     *
     * @param user  user to check
     * @param stage stage to access
     * @return is viewer user or not
     */
    public boolean isViewer(User user, Stage stage) {
        if (stage != null && user != null) {
            if (EqualsUtils.equalAny(stage.getType(), StageType.USERS_INTERACTION, StageType.ARCHIVE)) {
                if (!CollectionUtils.isEmpty(stage.getViewersRoles())) {
                    user = reloadIfNeed(user, "user-with-role");
                    if (!CollectionUtils.isEmpty(user.getUserRoles())) {
                        for (UserRole ur : user.getUserRoles()) {
                            if (stage.getViewersRoles().contains(ur.getRole())) {
                                return true;
                            }
                        }
                    }
                } else if (!CollectionUtils.isEmpty(stage.getViewers())) {
                    return stage.getViewers().contains(user);
                }
            }
        }
        return false;
    }

    private <T extends Entity> T reloadIfNeed(T entity, String view) {
        if (!PersistenceHelper.isLoadedWithView(entity, view)) {
            entity = dataManager.reload(entity, view);
        }
        return entity;
    }
}
