var scripts = document.scripts;
//var AlphaNumSet = createAlphaNumSet();
var alphaNumProbStrategy = {"UniformProb" : 1.0/62.0};

var nGramSize = 1;


var compositeTruePositive = 0;
var compositeFalseNegative = 0;


for(var i = 4; i<=4; i++){
	run(i);
}

self.port.emit("script-response", "ScriptLength = " + scripts.length);


self.port.emit("script-response", "compositeTruePositive = " + compositeTruePositive);
self.port.emit("script-response", "compositeFalseNegative = " + compositeFalseNegative);


function run(ngram){
	nGramSize = ngram;
	
	//calculate benign and malicious probability score for each script
	for(var i = 0; i < scripts.length; i++ ){
		//self.port.emit("script-response", scripts[i].text);
		
		var compositeBenignScore = query(scripts[i].text, nGramSize, "benign", "composite");
		var compositeMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "composite");
		
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

function query(script, nGramSize, type, model){
	var probability = 0.0;
	alert(script);
	if (type == "malicious") {
		script = compositeWordTransform(script);
		for(var i = 0; i< script.length - nGramSize; i++ ){
			pA = getPA(script.slice(i, i + nGramSize -1).join(""), type, model);
			
			pAB = getPAB(script.slice(i, i + nGramSize).join(""), type, model);
			probability = probability + Math.log(pAB/pA);
			
		}
	}
	else{
		for(var i = 0; i< script.length - nGramSize; i++ ){
			pA = getPA(script.slice(i, i + nGramSize -1), type, model);
			
			pAB = getPAB(script.slice(i, i + nGramSize), type, model);
			probability = probability + Math.log(pAB/pA);
			
		}
	}
	
	return probability;
		
		
		
	}
	
	
	


//This funciton get the count of ngram/totalwords for ngram  nGramSize -1
function getPA(wordSequence, type, model){
	alert(wordSequence);
	var p = 1.0;
	/*
	var p = getP(wordSequence);
	if(p >= 0){
		alert(p);
		return p;
	}
	*/
	if(type == "benign"){
	
		if(model == "composite"){
			if(InputBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
			//alert(wordSequence);
			
			p = InputBenignCountsA[wordSequence]/InputBenignTotal.totalWordsA;
			//alert("benign " + p)
			return p;
		}
		
	}
	
	else if(type == "malicious"){
		
		/*alert("inside malicious :" + wordSequence);
		p = getP(wordSequence);
		if(p >= 0){
			return p;
			
		}*/
		if(model == "composite"){
			if(CompositeMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsA[wordSequence]/CompositeMaliciousTotal.totalWordsA;
			alert("normal " + p)
			return p;
		}
		
		
	}
		
}

//This funciton get the count of ngram/totalwords for ngram  nGramSize
function getPAB(wordSequence, type, model){
	alert(wordSequence);
	var p = 1.0
	
	
	if(type == "benign"){
		if(model == "composite"){
		
			if(InputBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = InputBenignCountsAB[wordSequence]/InputBenignTotal.totalWordsAB;
		
			return p;
		}
		
	}
	else if(type == "malicious"){
	
		/*p = getP(wordSequence);
		if(p >= 0){
			return p;
		}*/
		if(model == "composite"){
			if(CompositeMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsAB[wordSequence]/CompositeMaliciousTotal.totalWordsAB;
		
			return p;
		}
		
	}
	
	
	
}

function getP(wordSequence){
	alert("inside p " + wordSequence);
	var p = 1.0;
	
	for(var i =0; i<wordSequence.length; i++){
		var ch = wordSequence.charCodeAt(i);
		alert(ch);
		if((ch>47 && ch<58) || (ch>64 && ch<91) || (ch>96 && ch<123)){
			p = p * alphaNumProbStrategy.UniformProb;
		}
		else{
			return -1;
		}
	}
	alert("alphanumeric : " + p);
	return p;
	
}

function incrementCountA(wordSequence, type){
	if(type == "benign"){
		InputBenignCountsA[wordSequence] = 1;
		
	}
	else if(type == "malicious"){
		CompositeMaliciousCountsA[wordSequence] = 1;
	}
}

function incrementCountAB(wordSequence, type){
	if(type == "benign"){
		InputBenignCountsAB[wordSequence] = 1;
		//alert(benignCountsAB[wordSequence]);
	}
	else if(type == "malicious"){
		CompositeMaliciousCountsAB[wordSequence] = 1;
	}
}



