/* main JS file */
var filtered =[];


d3.csv("data/cities.csv", function(data) {
 
    return data;

}).then(function(data){
    for(let i = 0 ; i< data.length; i++){
        if(data[i].eu == 'true'){
            data[i].population = +data[i].population;
            data[i].x = +data[i].x;
            data[i].y = +data[i].y;
            filtered.push(data[i]);
        }
    }
    var svg = d3.select("body").append("svg")
	    .attr("width", 700)
        .attr("height", 550)

     d3.select("body").append("h1").text("Number of EU Countries: "+ filtered.length)
    svg.selectAll("text")
    .data(filtered)
    .enter()
    .append("text")
    .attr("class", "city-label")
    .text(function(d,city){
        return d.city
    })
    .attr("y", function(d,y){
        return d.y-10
    })
    .attr("x", function(d,x){
        return d.x+20
    })
    .attr("opacity",function(d,population){
        if(d.population >= 1000000){
            return 1;
        }
        else{
            return 0;
        }
    })
    svg.selectAll("circle")
        .data(filtered)
        .enter()
        .append("circle")
        .attr("fill", "green")
        .attr("stroke","black")
        .attr("r",function(d,population){
            if(d.population < 1000000){
                return 4;
            }
            else{
                return 8;
            }
        })
        .attr("cy", function(d,y){
            return d.y
        })
        .attr("cx", function(d,x){
            return d.x
        })

});

    
