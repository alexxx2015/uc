package de.tum.in.i22.ucwebmanager.dashboard;

//import com.vaadin.demo.dashboard.DashboardNavigator;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import de.tum.in.i22.ucwebmanager.UcWebManagerNavigator;
import de.tum.in.i22.ucwebmanager.view.StaticAnalysisView;

/*
 * Dashboard MainView is a simple HorizontalLayout that wraps the menu on the
 * left and creates a simple container for the navigator on the right.
 */
@SuppressWarnings("serial")
public class MainScreen extends HorizontalLayout {

    public MainScreen() {
    	setSizeUndefined();
//    	setSizeFull();	
        addStyleName("mainview");
        
//        Add legt dashboard menu bar
        addComponent(new DashboardMenu());

//        Add central content box
        ComponentContainer content = new CssLayout();
        content.addStyleName("view-content");
//        content.setSizeFull();
        content.setSizeUndefined();
        addComponent(content);
        setExpandRatio(content, 1.0f);

        new UcWebManagerNavigator(content);
    }
}
