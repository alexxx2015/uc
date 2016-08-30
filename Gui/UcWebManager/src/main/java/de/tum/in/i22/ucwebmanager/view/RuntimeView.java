package de.tum.in.i22.ucwebmanager.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import ua.net.freecode.chart.AxisSystem;
import ua.net.freecode.chart.AxisSystem.AxisHorizontal;
import ua.net.freecode.chart.AxisSystem.AxisVertical;
import ua.net.freecode.chart.Chart;
import ua.net.freecode.chart.CurvePresentation;
import ua.net.freecode.chart.Marker;

public class RuntimeView extends VerticalLayout implements View{

//	final RuntimeDiagram diagram = new RuntimeDiagram();
	public RuntimeView() {
		Chart chart = new Chart();
        //adding the style name below is related to choosing the color scheme and discussed in Chapter 4
        chart.addStyleName("UniqueColorsBlueGreenRedScheme");
        chart.setWidth("100%");
        chart.setHeight("400px");
        //20 pixels at the right is necessary because we have long labels at the bottom axis and when
        //there is no legend, they are cut if we do not provide some additional space at the right
        chart.setMinRightMargin(20);
        //our labels at the bottom axis are at the angle of 45 and they need 100-pixel height of the space
        chart.setAxisLabelStringHeight(100);
        chart.setGeneralTitle("Sample General Chart Header");
        chart.setLegendData(new String[]{"Freecode","OST Ltd.","IT Ltd.","Dobryvechir"});
        //The value of legend item width is adjusted according to the longest string in the legend data,
        //which can be chosen experimentally
        chart.setLegendItemWidth(112);
        AxisSystem axisSystem = chart.createAxisSystem(AxisHorizontal.Bottom, AxisVertical.Left);
        //our range is 0..1000 (it is not necessary to specify 0 as minimum since it is the default)
        axisSystem.setVerticalAxisMaxValue(1000);
        //the angle for each label at the bottom axis is 45 degrees
        axisSystem.setHorizontalAxisLabelAngle(45);
        axisSystem.setHorizontalAxisTitle("Year 2012");
        axisSystem.setVerticalAxisTitle("Incomes and losses of the company");
        axisSystem.setCurvePresentation(new CurvePresentation[]{
                new CurvePresentation(new Marker(Marker.MarkerShape.Circle),0,CurvePresentation.CurveKind.VerticalBar),
                new CurvePresentation(new Marker(Marker.MarkerShape.Rectangle),0,CurvePresentation.CurveKind.VerticalBar),
                new CurvePresentation(new Marker(Marker.MarkerShape.Square),2,CurvePresentation.CurveKind.Line),
                new CurvePresentation(new Marker(Marker.MarkerShape.NoMarker),1,CurvePresentation.CurveKind.Area),
        });
        axisSystem.setXDiscreteValues(new String[]{"January 2012",
                "February 2012","March 2012","April 2012","May 2012","June 2012",
                "July 2012", "August 2012","September 2012","October 2012",
                "November 2012","December 2012"});
        axisSystem.setYDiscreteValuesForAllSeries(new double[][]{
                new double[]{300,400,450,500,657,450,230,100,500,200,300,500},
                new double[]{196,20,212,302,0,12,30,33,64,100,200,212},
                new double[]{0,100,1500,1750,187,155,-190,1900,199,1200,-5,300,-5,300},
                new double[]{0,72,500,100,20,100,500,88,150,160,10,200}
        });
		Label lab = new Label("Runtime view");
		addComponent(lab);
		

		VerticalLayout parent = new VerticalLayout();
		parent.addComponent(lab);
		
		FormLayout fl = new FormLayout();
		fl.setSizeFull();
		
//		fl.addComponent(diagram);
		fl.addComponent(chart);
		
		parent.addComponent(fl);		
		parent.setMargin(true);
		addComponent(parent);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
