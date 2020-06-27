package com.company.scrumit.web.humanresourcesaccount;

import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import com.company.scrumit.entity.HumanResourcesAccount;

import javax.inject.Inject;
import java.util.List;

@UiController("scrumit_HumanResourcesAccount.browse")
@UiDescriptor("human-resources-account-browse.xml")
@LookupComponent("humanResourcesAccountsTable")
public class HumanResourcesAccountBrowse extends StandardLookup<HumanResourcesAccount> {

    private List<HumanResourcesAccount> humanAccaunts;
    @Inject
    private CollectionContainer<HumanResourcesAccount> humanResourcesAccountsDc;

    public void setHumanAccaunts(List<HumanResourcesAccount> accaunts){
        this.humanAccaunts = accaunts;
    }
    @Subscribe
    private void onBeforeShow(BeforeShowEvent event){
        if (humanAccaunts != null){
            humanResourcesAccountsDc.setItems(humanAccaunts);
        }
    }
}