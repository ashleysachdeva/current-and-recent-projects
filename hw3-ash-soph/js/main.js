import StackedAreaChart from './StackedAreaChart.js';
import Timeline from './Timeline.js';


let yearData, categoryData;


let parseDate = d3.timeParse("%Y");

let areachart = StackedAreaChart();

function handleClick(d,i){
	listeners.apply("selectCategory", this, [d.key,d.index]);
}

let timeline = Timeline()
.on('brushed', onBrushRange);


let filterCategory, filterRange;

// Start application by loading the data
Promise.all([ // load multiple files
	d3.csv('data/year_data.csv', d=>{
		d.Expenditures = parseFloat(d.Expenditures) ;
		d.Year = parseDate(d.Year.toString());
		return d;
	}),
	d3.csv('data/Summer_filter.csv', d=>{
		Object.keys(d).forEach(key=>{
			//console.log(key)
			//console.log(d[key])
			if (key != "Year") {
				d[key] = parseFloat(d[key]);
			} else if(key == "Year") {
				d[key] = parseDate(d[key].toString());
			}
		})
		
		return d; 
	})
]).then(data=>{
	//console.log(data[0])
	//console.log(data[1])
	yearData = data[0];
	categoryData = data[1];


	d3.select('#stacked-area-chart') // a container element
	.datum(categoryData) // specify data
	.call(areachart);
	d3.select('#timeline')
	.datum(yearData) // per year data
	.call(timeline);

})
// callback for selecting a category in the stack area chart
function onSelectCategory(d,i){
	filterCategory = filterCategory===d?null:d; // toggle the filter to go back to all categories
	let filtered = filterCategoryData(filterCategory, filterRange);
	d3.select('#stacked-area-chart')
		.datum(filtered)
		.call(areachart);
}
areachart = StackedAreaChart()
.on('selectCategory', onSelectCategory);
			
// callback for brushing on the timeline
function onBrushRange(dateRange) {
	filterRange = dateRange;
	let filtered = filterCategoryData(filterCategory, filterRange);
	d3.select('#stacked-area-chart')
		.datum(filtered)
		.call(areachart);
}


// check if a year is within the year range
function within(d, range){
	return d.getTime()>=range[0].getTime()&&d.getTime()<=range[1].getTime();
}

// filter category data based on a specific category (if any) and year range
function filterCategoryData(category, dateRange){
	let filtered = dateRange?categoryData.filter(d=>within(d.Year, dateRange)): categoryData;
	filtered = filtered.map(row=>{
		return category?{
			Year:row.Year,
			[category]:row[category]
		}:row;
	});
	return filtered;
}