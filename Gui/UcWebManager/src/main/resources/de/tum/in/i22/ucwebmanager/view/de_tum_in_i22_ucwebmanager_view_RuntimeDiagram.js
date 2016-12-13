/**
 * 
 */
window.de_tum_in_i22_ucwebmanager_view_RuntimeDiagram = function() {
	d3.select("tbody").append("svg").attr("width", 960).attr("height", 600);
	
	var svg = d3.select("svg"),
    width = +svg.attr("width"),
    height = +svg.attr("height");
	var color = d3.scaleOrdinal(d3.schemeCategory20);
	
	var simulation = d3.forceSimulation()
	    .force("link", d3.forceLink().id(function(d) { return d.id; }))
	    .force("charge", d3.forceManyBody())
	    .force("center", d3.forceCenter(width / 2, height / 2));
	
	var oldVisLabels=true;
	
	var drawFromJSON = function(file){
	d3.json(file, function(error, graph) {
	  if (error) throw error;
	
	  var link = svg.append("g")
	      .attr("class", "links")
	    .selectAll("line")
	    .data(graph.links)
	    .enter().append("line")
	      .attr("stroke-width", function(d) { return Math.sqrt(d.value); });
	
	  var node = svg.append("g")
	      .attr("class", "nodes")
	    .selectAll("circle")
	    .data(graph.nodes)
	    .enter().append("circle")
	      .attr("r", 5)
	      .attr("fill", function(d) { return color(d.group); })
	      .call(d3.drag()
	          .on("start", dragstarted)
	          .on("drag", dragged)
	          .on("end", dragended));
	  
	  //if (visLabels) {
		  var labels = svg.append("g")
	      .attr("class", "label1")
	    .selectAll("text")
	    .data(graph.nodes)
	    .enter().append("text")
	      .attr("dx", 6)
	      .attr("dy", ".31em")
			  .style("font-size",9)
		  .text("lab");
	  //}


	
	  node.append("title")
	      .text(function(d) { return d.id; });
	
	  simulation
	      .nodes(graph.nodes)
	      .on("tick", ticked);
	
	  simulation.force("link")
	      .links(graph.links);
	
	  function ticked() {
	    link
	        .attr("x1", function(d) { return d.source.x; })
	        .attr("y1", function(d) { return d.source.y; })
	        .attr("x2", function(d) { return d.target.x; })
	        .attr("y2", function(d) { return d.target.y; });
	
	    node
	        .attr("cx", function(d) { return d.x; })
	        .attr("cy", function(d) { return d.y; });
	    
	    //if (visLabels) {
		labels
	        .attr("x", function(d) { return d.x; })
	        .attr("y", function(d) { return d.y; }); 
	    //}
	  }
	  svg.selectAll("line").attr("stroke","#999").attr("stroke-opacity", 0.6);
	});
	}
	
	function dragstarted(d) {
	  if (!d3.event.active) simulation.alphaTarget(0.3).restart();
	  d.fx = d.x;
	  d.fy = d.y;
	}
	
	function dragged(d) {
	  d.fx = d3.event.x;
	  d.fy = d3.event.y;
	}
	
	function dragended(d) {
	  if (!d3.event.active) simulation.alphaTarget(0);
	  d.fx = null;
	  d.fy = null;
	}
	
	var oldJson = "";
	this.onStateChange = function() {
		var json = this.getState().json;
		var visLabels = this.getState().visLabels;
		if (oldJson!=json) {
			drawFromJSON(json);
		}
		oldJson=json;
		
		if (visLabels!=oldVisLabels) {
			showHideElement("label1", visLabels);
		}
		oldVisLabels=visLabels;
			
	}
	
	var showHideElement = function(className, visible) {
		if (visible) {
			d3.selectAll("." + className).style("visibility", "visible")
		}
		else {
			d3.selectAll("." + className).style("visibility", "hidden")
		}
	}

	
}