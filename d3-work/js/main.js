
// SVG Size
//var width = 700,
	//height = 500;
let margin = {top: 20, right: 10, bottom: 20, left: 10};
let width = 960 - margin.left - margin.right,
height = 500 - margin.top - margin.bottom;


// Load CSV file

d3.csv("data/wealth-health-2014.csv", (row)=>{
	// type conversion
	row.LifeExpectancy =+ row.LifeExpectancy;
	row.Income =+ row.Income;
	row.Population =+ row.Population;
	return row;
})
.then(data=>{
		// Analyze the dataset in the web console
		var sortByProperty = function (property) {
			return function (x, y) {
				return ((x[property] === y[property]) ? 0 : ((x[property] > y[property]) ? -1 : 1));
			};
		};
		data.sort(sortByProperty('Population'));
		

		console.log(data);
		console.log("Countries: " + data.length)
		let svg = d3.select("#chart-area").append("svg")
		.attr("width",width)
		.attr("height",height)
		let padding = 30;
		let incomeScale = d3.scaleLinear() 
			.domain([d3.min(data,d=>d.Income)-100, d3.max(data,d=>d.Income)+100]) 
			.range([padding,width-padding])
		
		let lifeExpectancyScale = d3.scaleLinear() 
			.domain([d3.min(data,d=>d.LifeExpectancy)-20, d3.max(data,d=>d.LifeExpectancy)]) 
			.range([height-padding,padding])
			

		console.log(incomeScale(5000)) 		// Returns: 23.2763
		console.log(lifeExpectancyScale(68))
		let popMin= d3.min(data,d=>d.Population);
		let popMax = d3.max(data,d=>d.Population);
		var rScale = d3.scaleLinear()
			.domain([popMin,popMax])
			.range([4, 30]);
		var colors = d3.scaleOrdinal()
			.domain(["Sub-Saharan Africa", "South Asia", "East Asia & Pacific", "Middle East & North Africa", "Americas","Europe & Central Asia"])
			.range(d3.schemeSet1);


		svg.selectAll("circ")
			.data(data)
			.enter()
			.append("circle")
			.style("fill", (d)=>colors(d.Region))
			.attr("cy", (d)=>lifeExpectancyScale(d.LifeExpectancy))
			.attr("cx", (d)=>incomeScale(d.Income))
			.attr("r",(d)=>rScale(d.Population))
			.attr("stroke","green")

		

		let xAxis = d3.axisBottom()
			.scale(incomeScale);
		svg.append("g")
			.attr("class", "axis x-axis")
			.attr("transform", "translate(-30," + (height - 40 ) + ")")
			.call(xAxis);
		svg.append("text")
		.attr("class", "axis x-axis")
		.attr("text-anchor", "end")
		.attr("x", width)
		.attr("y", height -2)
		.text("Income per Person (GDP per Capita)");
			
	

		let yAxis = d3.axisRight()
			.scale(lifeExpectancyScale);
		svg.append("g")
			.attr("class", "axis y-axis")
			.attr("transform", "translate("+ (height-450) + "," + "-10" + ")")
			.call(yAxis);

		svg.append("text")
			.attr("class", "axis y-axis")
			.attr("text-anchor", "end")
			.attr("y", 0)
			.attr("dy", ".75em")
			.attr("transform", "rotate(-90)")
			.text("Life Expectancy");


		
});