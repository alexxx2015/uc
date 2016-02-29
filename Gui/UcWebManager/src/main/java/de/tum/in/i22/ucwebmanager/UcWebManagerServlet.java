package de.tum.in.i22.ucwebmanager;

import javax.servlet.ServletException;

import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")
public class UcWebManagerServlet extends VaadinServlet {

    @Override
    protected final void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new UcWebManagerSessionInitListener());
    }
}