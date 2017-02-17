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
	
	
	var drawFromJSON = function(file){
	d3.json(file, function(error, graph) {
	  if (error) throw error;
	
	  var keys = getFields(graph.nodes);
	  //console.log(keys);
	  
	  var arrLabels = new Array();
	
	  for (i=0; i<keys.length; i++) {
			
		  var labels = svg.append("g")
				 .attr("class", keys[i])
				.selectAll("text")
				.data(graph.nodes)
				.enter().append("text")
				  .attr("dx", 6)
				  .attr("dy", ".31em")
				  .attr("font-size","10")
				  .text(function(d) {return d[keys[i]];});
		  
		  arrLabels.push(labels);
	  }
	  
	  
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
	      .attr("fill", function(d) { if(d.id.search("START") == -1) return color(d.group); else return "green";})
	      .call(d3.drag()
	          .on("start", dragstarted)
	          .on("drag", dragged)
	          .on("end", dragended));

	
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
	    
	    
        for (i=0; i<arrLabels.length; i++) {
			arrLabels[i]
				.attr("x", function(d) { return d.x; })
				.attr("y", function(d) { return d.y+10*(i-arrLabels.length/2); });  
		}
	  }
	  
      
      function draw(data) {
    	    console.log(data); // this is your data
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
	  //Uncomment to reset the position of a node to its initial position
	  //d.fx = null;
	  //d.fy = null;
	}
	
	var oldJson = "";
	var oldLabels = [];
	this.onStateChange = function() {
		var json = this.getState().json;
		var labelsMap = this.getState().labelsMap;
		
		if (oldLabels.length === labelsMap.length) {
			for (field in oldLabels) {
				if (oldLabels[field] != labelsMap[field]) {
					oldLabels[field] = labelsMap[field];
					showHideElement(field, labelsMap[field]);
				}
			}
		}
		else {
			oldLabels = labelsMap;
			for (field in labelsMap) {
				showHideElement(field, labelsMap[field]);
			}
		}
			
		if (oldJson!=json) {
			drawFromJSON(json);
		}
		oldJson=json;
		
	}
	
	function showHideElement (className, visible) {
		if (visible) {
			d3.selectAll("." + className).style("visibility", "visible")
		}
		else {
			d3.selectAll("." + className).style("visibility", "hidden")
		}
	}
	
	function getFields (nodes) {
		var entry = nodes[0];
   	  	var fields = new Array();
   	  	for (name in entry) {
   	  		console.log(name);
   	  		if (entry.hasOwnProperty(name)) {
   	  			fields.push(name);
   	  		}
   	  	}
   	  	return fields;
     }
	
}