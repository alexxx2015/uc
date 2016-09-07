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
private static String newLine = System.getProperty("line.separator");
String d3Script = "var svg = d3.select(\".chart1 svg\");"+ System.getProperty("line.separator") +
"var width = svg.attr(\"width\");"+ System.getProperty("line.separator")
+ "var height = svg.attr(\"height\");"+ System.getProperty("line.separator")
+ "console.log(\"Height: \" + height);" + newLine
+ "var color = d3.scaleOrdinal(d3.schemeCategory20); "+ System.getProperty("line.separator")

+ "var simulation = d3.forceSimulation()" + System.getProperty("line.separator")
+ ".force(\"link\", d3.forceLink().id(function(d) { return d.id; }))" + System.getProperty("line.separator")
+ ".force(\"charge\", d3.forceManyBody())" + System.getProperty("line.separator")
+ ".force(\"center\", d3.forceCenter(width / 2, height / 2));" + System.getProperty("line.separator")

+ "d3.json(\"miserables.json\", function(error, graph) {" + newLine
+ "if (error) throw error;" + newLine

+ "var link = svg.append(\"g\")" + newLine
+ ".attr(\"class\", \"links\")" + newLine
+ ".selectAll(\"line\")" + newLine
+ ".data(graph.links)" + newLine
+ ".enter().append(\"line\")" + newLine
+ ".attr(\"stroke-width\", function(d) { return Math.sqrt(d.value); });" + newLine

+ "var node = svg.append(\"g\")" + newLine
  		+ ".attr(\"class\", \"nodes\")" + newLine
  		+ ".selectAll(\"circle\")" + newLine
  		+ ".data(graph.nodes)" + newLine
  		+ ".enter().append(\"circle\")" + newLine
  		+ ".attr(\"r\", 5)" + newLine
  		+ ".attr(\"fill\", function(d) { return color(d.group); })" + newLine
  		+ ".call(d3.drag()" + newLine
  		+ ".on(\"start\", dragstarted)" + newLine
  		+ ".on(\"drag\", dragged)" + newLine
  		+ ".on(\"end\", dragended));" + newLine

  +"node.append(\"title\")" + newLine
  + ".text(function(d) { return d.id; });" + newLine

  +"simulation" + newLine
  + ".nodes(graph.nodes)" + newLine
  + ".on(\"tick\", ticked);" + newLine

  +"simulation.force(\"link\")" + newLine
  		+ ".links(graph.links);" + newLine

  +"function ticked() {" + newLine
  + "link" + newLine
  		+ ".attr(\"x1\", function(d) { return d.source.x; })" + newLine
  		+ ".attr(\"y1\", function(d) { return d.source.y; })" + newLine
  		+ ".attr(\"x2\", function(d) { return d.target.x; })" + newLine
  		+ ".attr(\"y2\", function(d) { return d.target.y; });" + newLine

   + "node" + newLine
   + ".attr(\"cx\", function(d) { return d.x; })" + newLine
   + ".attr(\"cy\", function(d) { return d.y; });" + newLine
  +"}" + newLine
+"});" + newLine

+ "function dragstarted(d) {" + newLine
+ "if (!d3.event.active) simulation.alphaTarget(0.3).restart();" + newLine
+ "d.fx = d.x;" + newLine
+ "d.fy = d.y;" + newLine
+ "}" + newLine

+ "function dragged(d) {" + newLine
+ "d.fx = d3.event.x;" + newLine
+ "d.fy = d3.event.y;" + newLine
+ "}" + newLine

+"function dragended(d) {" + newLine
+ "if (!d3.event.active) simulation.alphaTarget(0);" + newLine
+ "d.fx = null;" + newLine
+ "d.fy = null;" + newLine
+ "}";
//	final RuntimeDiagram diagram = new RuntimeDiagram();
	public RuntimeView() {
		Chart chart = new Chart(); 
		chart.addStyleName("chart1");
		chart.setWidth("100%"); 
		chart.setHeight("500px"); 
		chart.setDisabledWholeChart(false);
		System.out.println(d3Script);
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"svg\").attr(\"height\", \"600\").attr(\"width\", \"960\")");
		chart.addExecutableJavascriptToRunOnce("d3.select(\".chart1 svg\")");
		chart.addExecutableJavascriptToRunOnce(d3Script);
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"path\").attr(\"d\",\"M  300 230 l 0 -200 a 200 200 0 0 1 173.2 300  Z\").attr(\"fill\",\"blue\").attr(\"stroke\",\"#FFFFFF\").attr(\"stroke-width\",1).attr(\"stroke-linejoin\",\"round\")"); 
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"path\").attr(\"d\",\"M  300 230 l 173.2 100 a 200 200 0 0 1 -346.4 0  Z\").attr(\"fill\",\"red\").attr(\"stroke\",\"#FFFFFF\").attr(\"stroke-width\",1).attr(\"stroke-linejoin\",\"round\")"); 
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"path\").attr(\"d\",\"M  300 230 l -173.2 100 a 200 200 0 0 1 173.2 -300 Z\").attr(\"fill\",\"green\").attr(\"stroke\",\"#FFFFFF\").attr(\"stroke-width\",1).attr(\"stroke-linejoin\",\"round\")"); 
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"text\").text(\"Freedom\").attr(\"x\",290).attr(\"y\",330).attr(\"fill\",\"yellow\").attr(\"style\",\"font-size:14px\")"); 
//		chart.addExecutableJavascript("d3.select(\".chart1 g.chart-data-box-low\").append(\"text\").text(\"Faith\").attr(\"x\",400).attr(\"y\",190).attr(\"fill\",\"yellow\").attr(\"style\",\"font-size:14px\")"); 
//		chart.addExecutableJavascript("d3.select(\".chart1  g.chart-data-box-low\").append(\"text\").text(\"Moral\").attr(\"x\",190).attr(\"y\",190).attr(\"fill\",\"yellow\").attr(\"style\",\"font-size:14px\")"); 
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
