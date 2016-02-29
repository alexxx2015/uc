package de.tum.in.i22.ucwebmanager;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

public class WebServiceServlets {
    @WebServlet(urlPatterns = "/*", name = "UcWebManagerUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = UcWebManagerUI.class, productionMode = false)
    public static class UcWebManagerUIServlet extends VaadinServlet {
    }

}
