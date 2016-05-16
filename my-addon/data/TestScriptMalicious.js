var scripts = document.scripts;
var AlphaNumSet = createAlphaNumSet();
var AlphanumericUniformProbabilityStrategy = {"UniformProb" : 1.0/62.0};

var nGramSize = 1;

var keywordTruePositive = 0;
var keywordFalseNegative = 0;
var inputTruePositive = 0;
var inputFalseNegative = 0;
var compositeTruePositive = 0;
var compositeFalseNegative = 0;

for(var i = 4; i<=4; i++){
	run(i);
}

self.port.emit("script-response", "ScriptLength = " + scripts.length);

self.port.emit("script-response", "keywordTruePositive = " + keywordTruePositive);
self.port.emit("script-response", "keywordFalseNegative = " + keywordFalseNegative);
self.port.emit("script-response", "inputTruePositive = " + inputTruePositive);
self.port.emit("script-response", "inputFalseNegative = " + inputFalseNegative);
self.port.emit("script-response", "compositeTruePositive = " + compositeTruePositive);
self.port.emit("script-response", "compositeFalseNegative = " + compositeFalseNegative);

function run(ngram){
	nGramSize = ngram;
	
	//calculate benign and malicious probability score for each script
	for(var i = 0; i < scripts.length; i++ ){
		//self.port.emit("script-response", scripts[i].text);
		
		var inputBenignScore = query(scripts[i].text, nGramSize, "benign", "input");
		var inputMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "input");
		
		if(inputMaliciousScore > inputBenignScore){
            inputTruePositive++;
        }
        else{
            inputFalseNegative++;
        }
		
		var keywordBenignScore = query(scripts[i].text, nGramSize, "benign", "keyword");
		var keywordMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "keyword");
		
		if(keywordMaliciousScore > keywordBenignScore){
            keywordTruePositive++;
        }
        else{
            keywordFalseNegative++;
        }
		
		var compositeBenignScore = query(scripts[i].text, nGramSize, "benign", "composite");
		var compositeMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "composite");
		
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
	var text = " ";
	if(nGramSize == 1){
		var pA = 0.0;
		
		for(var i = 0; i< script.length; i++ ){
			pA = getPA(script.slice(i, i + nGramSize), type, model);
			
			probability = probability + Math.log(pA);
		}
		
	}
	else{
	
		if(model == "input"){
			text = script;
		}
		else if(model == "keyword" ){
			text = characterKeywordtransform(script);
		}
		
		else if(model == "composite"){
			text = compositeWordTransform(script);
		}
		
		for(var i = 0; i< text.length - nGramSize; i++ ){
			pA = getPA(text.slice(i, i + nGramSize -1), type, model);
			
			pAB = getPAB(text.slice(i, i + nGramSize), type, model);
			probability = probability + Math.log(pAB/pA);
			
		}
	}
	
	return probability
	
}

//This funciton get the count of ngram/totalwords for ngram  nGramSize -1
function getPA(wordSequence, type, model){
	var p = 1.0;
	/*
	var p = getP(wordSequence);
	if(p >= 0){
		alert(p);
		return p;
	}
	*/
	if(type == "benign"){
	
		if(model == "input"){
			if(InputBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = InputBenignCountsA[wordSequence]/InputBenignTotal.totalWordsA;
			return p;
		}
		else if(model == "keyword"){
			if(KeywordBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = KeywordBenignCountsA[wordSequence]/KeywordBenignTotal.totalWordsA;
			return p;
		}
		
		else if(model == "composite"){
			alert(wordSequence);
			if(CompositeBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = CompositeBenignCountsA[wordSequence]/CompositeBenignTotal.totalWordsA;
			alert(CompositeBenignCountsA[wordSequence]);
			return p;
		}
	}
	
	else if(type == "malicious"){
		if(model == "input"){
			if(InputMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = InputMaliciousCountsA[wordSequence]/InputMaliciousTotal.totalWordsA;
			return p;
		}
		
		else if(model == "keyword"){
			if(KeywordMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = KeywordMaliciousCountsA[wordSequence]/KeywordMaliciousTotal.totalWordsA;
			return p;
		}
		
		else if(model == "composite"){
			if(CompositeMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsA[wordSequence]/CompositeMaliciousTotal.totalWordsA;
			return p;
		}
	}
	
	
	
	
}

//This funciton get the count of ngram/totalwords for ngram  nGramSize
function getPAB(wordSequence, type, model){
	//alert(wordSequence);
	var p = 1.0
	/*var p = getP(wordSequence);
	if(p >= 0){
		return p;
	}*/
	
	if(type == "benign"){
		if(model == "input"){
		
			if(InputBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = InputBenignCountsAB[wordSequence]/InputBenignTotal.totalWordsAB;
		
			return p;
		}
		else if(model == "keyword"){
		
			if(KeywordBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = KeywordBenignCountsAB[wordSequence]/KeywordBenignTotal.totalWordsAB;
		
			return p;
		}
		else if(model == "composite"){
		
			if(CompositeBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = CompositeBenignCountsAB[wordSequence]/CompositeBenignTotal.totalWordsAB;
		
			return p;
		}
	}
	else if(type == "malicious"){
		if(model == "input"){
			if(InputMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = InputMaliciousCountsAB[wordSequence]/InputMaliciousTotal.totalWordsAB;
		
			return p;
		}
		else if(model == "keyword"){
			if(KeywordMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = KeywordMaliciousCountsAB[wordSequence]/KeywordMaliciousTotal.totalWordsAB;
		
			return p;
		}
		else if (model == "composite"){
			if(CompositeMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = CompositeMaliciousCountsAB[wordSequence]/CompositeMaliciousTotal.totalWordsAB;
		
			return p;
		}
	}
	
	
	
}

function getP(wordSequence){
	//alert(wordSequence);
	var p = 1.0
	
	if(AlphaNumSet.indexOf(wordSequence) != -1){
		return p;
	}
	else{
		return -1
	}
}

function incrementCountA(wordSequence, type){
	/*if(type == "benign"){
		benignCountsA[wordSequence] = 1;
		
	}
	else if(type == "malicious"){
		maliciousCountsA[wordSequence] = 1;
	}*/
}

function incrementCountAB(wordSequence, type){
	/*if(type == "benign"){
		benignCountsAB[wordSequence] = 1;
		//alert(benignCountsAB[wordSequence]);
	}
	else if(type == "malicious"){
		maliciousCountsAB[wordSequence] = 1;
	}*/
}

function createAlphaNumSet(){
	var AlphaNumSet = [];
	for(var i = 48; i <= 57; i++){
		AlphaNumSet.push(String.fromCharCode(i));
	}
	//A-Z
	for(var i = 65; i <= 90; i++){
		AlphaNumSet.push(String.fromCharCode(i));
	}
	//a-z
	for(var i = 97; i <= 122; i++){
		AlphaNumSet.push(String.fromCharCode(i));
	}
	return AlphaNumSet;
}

