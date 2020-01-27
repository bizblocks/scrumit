package com.company.scrumit.web.tracker.workflow;

import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import org.dom4j.Element;

import javax.inject.Inject;

public abstract class AbstractXmlDescriptorFrame extends AbstractFrame implements Component.HasXmlDescriptor {

    @Inject
    private WindowConfig windowConfig;

    private Element element;

    @Override
    public Element getXmlDescriptor() {
        return element;
    }

    @Override
    public void setXmlDescriptor(Element element) {
        this.element = element;
    }

    @Override
    public void setId(String id) {
        super.setId(id);

//        if (element == null) {
//            WindowInfo info = windowConfig.getWindowInfo(id);
//            if (info != null) {
//                element = info.getDescriptor();
//            }
//        }
    }
}