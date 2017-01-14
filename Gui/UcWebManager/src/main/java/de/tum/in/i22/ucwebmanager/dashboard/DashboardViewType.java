package de.tum.in.i22.ucwebmanager.dashboard;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import de.tum.in.i22.ucwebmanager.view.DeploymentView;
import de.tum.in.i22.ucwebmanager.view.InstrumentationView;
import de.tum.in.i22.ucwebmanager.view.MainView;
import de.tum.in.i22.ucwebmanager.view.RuntimeView;
import de.tum.in.i22.ucwebmanager.view.StaticAnalysisView;


public enum DashboardViewType {
  MAIN("Home", MainView.class, FontAwesome.HOME, true),
  STATANALYSIS("Static Analysis", StaticAnalysisView.class, FontAwesome.ANDROID, true),
  INSTRUMENT("Instrumentation", InstrumentationView.class, FontAwesome.LINKEDIN, true),
  DEPLOYMENT("Deployment", DeploymentView.class, FontAwesome.DOWNLOAD, true),
  RUNTIME("Runtime", RuntimeView.class, FontAwesome.BAR_CHART_O, false);//, TRANSACTIONS(
//            "transactions", TransactionsView.class, FontAwesome.TABLE, false), REPORTS(
//            "reports", ReportsView.class, FontAwesome.FILE_TEXT_O, true), SCHEDULE(
//            "schedule", ScheduleView.class, FontAwesome.CALENDAR_O, false);
    
    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private DashboardViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static DashboardViewType getByViewName(final String viewName) {
        DashboardViewType result = null;
        for (DashboardViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
