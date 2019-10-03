/* main JS file */
var sandwiches = [
    { name: "Thesis", price: 7.95, size: "large" },
    { name: "Dissertation", price: 8.95, size: "large" },
    { name: "Highlander", price: 6.50, size: "small" },
    { name: "Just Tuna", price: 6.50, size: "small" },
    { name: "So-La", price: 7.95, size: "large" },
    { name: "Special", price: 12.50, size: "small" }
];

var svg = d3.select("body").append("svg")
	.attr("width", 500)
    .attr("height", 500);
    console.log(svg);
    
svg.selectAll("circle")
	.data(sandwiches)
	.enter()
	.append("circle")
	.attr("fill", function(d, price){
        if(d.price < 7){
            return "green";
        }
        else{
            return "yellow";
        }
    })
    .attr("stroke","black")
	.attr("r", function(d, size){
            if(d.size == "large"){
                return 10*2;
            }
            else{
                return 10;
            }
    })
	.attr("cy", 50)
	.attr("cx", function(d, index) {
		return (index * 80)+50;
});

