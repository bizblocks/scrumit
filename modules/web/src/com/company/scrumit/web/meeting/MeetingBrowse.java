package com.company.scrumit.web.meeting;

import com.haulmont.cuba.gui.screen.*;
import com.company.scrumit.entity.Meeting;

@UiController("scrumit$Meeting.browse")
@UiDescriptor("meeting-browse.xml")
@LookupComponent("meetingsTable")
@LoadDataBeforeShow
public class MeetingBrowse extends StandardLookup<Meeting> {
}