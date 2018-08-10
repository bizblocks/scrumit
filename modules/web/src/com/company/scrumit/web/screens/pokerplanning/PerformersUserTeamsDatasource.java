package com.company.scrumit.web.screens.pokerplanning;

import com.company.scrumit.entity.Performer;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public class PerformersUserTeamsDatasource extends CustomCollectionDatasource<Performer, UUID> {

    private Collection<Performer> performers;
    @Override
    protected Collection<Performer> getEntities(Map<String, Object> params) {
        return performers;
    }

    void setEntities(Collection<Performer> performers) {
        this.performers = performers;
    }

}