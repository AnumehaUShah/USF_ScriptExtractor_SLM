/******Author: Anumeha Shah *****/
/**This file carry out the parsing and malicious script detection using composite word type transformation****/

var scripts = document.scripts;
var alphaNumProbStrategy = {"UniformProb" : 1.0/62.0};
var nGramSize = 1;
var compositeTruePositive = 0;
var compositeFalseNegative = 0;

//logs the time taken for the javaScript program parsing and detection process
console.time("composite");

//this for loop calls the run function for n-grms of size 4
for(var i = 4; i<=4; i++){
	run(i);
}

//using port.emit to display final detection results to console
self.port.emit("script-response", "ScriptLength = " + scripts.length);
self.port.emit("script-response", "compositeTruePositive = " + compositeTruePositive);
self.port.emit("script-response", "compositeFalseNegative = " + compositeFalseNegative);
console.timeEnd("composite");

//logs the total time taken by the add-on
console.timeEnd("index");

//This is the main function which performs the detetion
function run(ngram){
	nGramSize = ngram;
	
	//calculate benign and malicious probability score for each script
	for(var i = 0; i < scripts.length; i++ ){
		//self.port.emit("script-response", scripts[i].text);
		
		var compositeBenignScore = query(scripts[i].text, nGramSize, "benign", "composite");
		var compositeMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "composite");
		
		
		if (compositeMaliciousScore == 0 && compositeBenignScore == 0) {
			continue;
		}
		else{
			self.port.emit("script-response", "compositeMaliciousScore = " + compositeMaliciousScore);
			self.port.emit("script-response", "compositeBenignScore = " + compositeBenignScore);
			if(compositeMaliciousScore > compositeBenignScore){
				compositeTruePositive++;
			}
			else{
				compositeFalseNegative++;
			}
			
		}	
	}	
}

function query(script, nGramSize, type, model){
	var probability = 0.0;
	script =  compositeWordTransform(script);
	if(nGramSize == 1){
		var pA = 0.0;
		
		for(var i = 0; i< script.length; i++ ){
			pA = getPA(script.slice(i, i + nGramSize), type, model);
			
			probability = probability + Math.log(pA);
		}
		
	}
	else{
		
		for(var i = 0; i< script.length - nGramSize; i++ ){
			pA = getPA(script.slice(i, i + nGramSize -1).join(""), type, model);
			
			pAB = getPAB(script.slice(i, i + nGramSize).join(""), type, model);
			probability = probability + Math.log(pAB/pA);
			
		}
	}
	
	return probability
	
}

//This funciton get the probability occuraance of an n-gram of size nGramSize -1
function getPA(wordSequence, type, model){
	var p = 1.0;
	
	if(type == "benign"){
	
		if(model == "composite"){
			if(CompositeBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
			
			p = CompositeBenignCountsA[wordSequence]/CompositeBenignTotal.totalWordsA;
			return p;
		}
		
	}
	
	else if(type == "malicious"){
		
		if(model == "composite"){
			if(CompositeMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsA[wordSequence]/CompositeMaliciousTotal.totalWordsA;
			return p;
		}	
	}
		
}

//This funciton get the probability occuraance of an n-gram of size nGramSize -1
function getPAB(wordSequence, type, model){
	var p = 1.0
	
	if(type == "benign"){
		if(model == "composite"){
		
			if(CompositeBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = CompositeBenignCountsAB[wordSequence]/CompositeBenignTotal.totalWordsAB;
		
			return p;
		}
		
	}
	else if(type == "malicious"){
	
		if(model == "composite"){
			if(CompositeMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsAB[wordSequence]/CompositeMaliciousTotal.totalWordsAB;
		
			return p;
		}
		
	}	
}

//this function implement alphanumeric probability strategy.
function getP(wordSequence){
	var p = 1.0;
	
	for(var i =0; i<wordSequence.length; i++){
		var ch = wordSequence.charCodeAt(i);
		if((ch>47 && ch<58) || (ch>64 && ch<91) || (ch>96 && ch<123)){
			p = p * alphaNumProbStrategy.UniformProb;
		}
		else{
			return -1;
		}
	}
	return p;
	
}

function incrementCountA(wordSequence, type){
	if(type == "benign"){
		CompositeBenignCountsA[wordSequence] = 1;
		
	}
	else if(type == "malicious"){
		CompositeMaliciousCountsA[wordSequence] = 1;
	}
}

function incrementCountAB(wordSequence, type){
	if(type == "benign"){
		CompositeBenignCountsAB[wordSequence] = 1;
	}
	else if(type == "malicious"){
		CompositeMaliciousCountsAB[wordSequence] = 1;
	}
}



