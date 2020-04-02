package com.company.scrumit.web.screens.humanresourcesaccount;

import com.haulmont.cuba.gui.screen.*;
import com.company.scrumit.entity.HumanResourcesAccount;

@UiController("scrumit_HumanResourcesAccount.browse")
@UiDescriptor("human-resources-account-browse.xml")
@LookupComponent("humanResourcesAccountsTable")
@LoadDataBeforeShow
public class HumanResourcesAccountBrowse extends StandardLookup<HumanResourcesAccount> {
}