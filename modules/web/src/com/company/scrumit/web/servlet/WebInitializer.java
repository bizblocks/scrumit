package com.company.scrumit.web.servlet;

import com.haulmont.cuba.core.sys.servlet.ServletRegistrationManager;
import com.haulmont.cuba.core.sys.servlet.events.ServletContextInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.Servlet;

@Component
public class WebInitializer {
    @Inject
    private ServletRegistrationManager servletRegistrationManager;

    @EventListener
    public void initializeHttpServlet(ServletContextInitializedEvent e) {
        Servlet myServlet = servletRegistrationManager.createServlet(e.getApplicationContext(), "com.company.scrumit.web.servlet.PayloadServlet");

        e.getSource().addServlet("payload", myServlet).addMapping("/payload");
    }

}
