
// Global variable with 60 attractions (JSON format)
// console.log(attractionData);



dataFiltering(attractionData);


function dataManipulation(){
	var attractions = attractionData;
	var hockey =[];
	let selectBox = document.querySelector("#attraction-category");
	let selectedValue = selectBox.options[selectBox.selectedIndex].value;
	if(selectedValue == "all"){
		for( let i=0; i< attractions.length;i++){
			hockey.push(attractions[i]);
		}
	}
	if(selectedValue == "Theme Park"){
		for( let i=0; i< attractions.length;i++){
			if(attractions[i].Category == "Theme Park"){
				hockey.push(attractions[i]);
			}
			
		}
	}
	if(selectedValue == "Water Park"){
		for( let i=0; i< attractions.length;i++){
			if(attractions[i].Category == "Water Park"){
				hockey.push(attractions[i]);
			}
			
		}
	}
	if(selectedValue == "Museum"){
		for( let i=0; i< attractions.length;i++){
			if(attractions[i].Category == "Museum"){
				hockey.push(attractions[i]);
			}
			
		}
	}
	console.log(hockey);
	dataFiltering(hockey);
	
	
}

function dataFiltering(attractcat) {

	var attractions = attractcat;

	
	var sortByProperty = function (property) {
		return function (x, y) {
			return ((x[property] === y[property]) ? 0 : ((x[property] > y[property]) ? 1 : -1));
		};
	};
	attractions.sort(sortByProperty('Visitors'));

	let hockey = sorted(attractions);
	function sorted(attractions){
		var hockey2 = [];
		hockey2[0] =attractions[attractions.length-1];
		hockey2[1] =attractions[attractions.length-2];
		hockey2[2] =attractions[attractions.length-3];
		hockey2[3] =attractions[attractions.length-4];
		hockey2[4] =attractions[attractions.length-5];
		return hockey2;
	}
	renderBarChart(hockey);
	console.log(hockey);
	
   
}
