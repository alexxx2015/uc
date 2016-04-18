package de.tum.in.i22.ucwebmanager.dashboard;

//import com.vaadin.demo.dashboard.DashboardNavigator;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import de.tum.in.i22.ucwebmanager.UcWebManagerNavigator;

/*
 * Dashboard MainView is a simple HorizontalLayout that wraps the menu on the
 * left and creates a simple container for the navigator on the right.
 */
@SuppressWarnings("serial")
public class MainScreen extends HorizontalLayout {

	public MainScreen() {
		setSizeFull();
		//setSizeUndefined();
		addStyleName("mainview");
		setHeight("100%");
		// Add legt dashboard menu bar
		DashboardMenu dbm = new DashboardMenu();
		addComponent(dbm);
		setExpandRatio(dbm, 1.0f);

		// Add central content box
		ComponentContainer content = new CssLayout();
		// content.addStyleName("myborder");
		content.addStyleName("view-content");
		content.setSizeFull();
//		 content.setSizeUndefined();
		addComponent(content);
		setExpandRatio(content, 4.0f);

		new UcWebManagerNavigator(content);
	}
}
