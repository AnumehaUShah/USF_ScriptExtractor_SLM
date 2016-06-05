var scripts = document.scripts;
//var AlphaNumSet = createAlphaNumSet();
var alphaNumProbStrategy = {"UniformProb" : 1.0/62.0};

var nGramSize = 1;


var keywordTruePositive = 0;
var keywordFalseNegative = 0;

console.time("keyword");
for(var i = 4; i<=4; i++){
	run(i);
}

self.port.emit("script-response", "ScriptLength = " + scripts.length);


self.port.emit("script-response", "keywordTruePositive = " + keywordTruePositive);
self.port.emit("script-response", "keywordFalseNegative = " + keywordFalseNegative);
console.timeEnd("keyword");
console.timeEnd("index");

function run(ngram){
	nGramSize = ngram;
	
	//calculate benign and malicious probability score for each script
	for(var i = 0; i < scripts.length; i++ ){
		//self.port.emit("script-response", scripts[i].text);
		
		var keywordBenignScore = query(scripts[i].text, nGramSize, "benign", "keyword");
		var keywordMaliciousScore = query(scripts[i].text, nGramSize, "malicious", "keyword");
		
		
		if (keywordMaliciousScore == 0 && keywordBenignScore == 0) {
			continue;
		}
		else{
			self.port.emit("script-response", "keywordMaliciousScore = " + keywordMaliciousScore);
			self.port.emit("script-response", "keywordBenignScore = " + keywordBenignScore);
			if(keywordMaliciousScore > keywordBenignScore){
				keywordTruePositive++;
			}
			else{
				keywordFalseNegative++;
			}
			
		}
		
		
		
		
		
	}
	
}

function query(script, nGramSize, type, model){
	var probability = 0.0;
	script =  characterKeywordtransform(script);
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
	
		if(model == "keyword"){
			if(KeywordBenignCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
			//alert(wordSequence);
			
			p = KeywordBenignCountsA[wordSequence]/KeywordBenignTotal.totalWordsA;
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
		if(model == "keyword"){
			if(KeywordMaliciousCountsA[wordSequence] == undefined){
				incrementCountA(wordSequence, type);
			}
		
			p = KeywordMaliciousCountsA[wordSequence]/KeywordMaliciousTotal.totalWordsA;
			//alert("normal " + p)
			return p;
		}
		
		
	}
		
}

//This funciton get the count of ngram/totalwords for ngram  nGramSize
function getPAB(wordSequence, type, model){
	//alert(wordSequence);
	var p = 1.0
	
	
	if(type == "benign"){
		if(model == "keyword"){
		
			if(KeywordBenignCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = KeywordBenignCountsAB[wordSequence]/KeywordBenignTotal.totalWordsAB;
		
			return p;
		}
		
	}
	else if(type == "malicious"){
	
		/*p = getP(wordSequence);
		if(p >= 0){
			return p;
		}*/
		if(model == "keyword"){
			if(KeywordMaliciousCountsAB[wordSequence] == undefined){
				incrementCountAB(wordSequence, type);
			}
		
			p = KeywordMaliciousCountsAB[wordSequence]/KeywordMaliciousTotal.totalWordsAB;
		
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
		KeywordBenignCountsA[wordSequence] = 1;
		
	}
	else if(type == "malicious"){
		KeywordMaliciousCountsA[wordSequence] = 1;
	}
}

function incrementCountAB(wordSequence, type){
	if(type == "benign"){
		KeywordBenignCountsAB[wordSequence] = 1;
		//alert(benignCountsAB[wordSequence]);
	}
	else if(type == "malicious"){
		KeywordMaliciousCountsAB[wordSequence] = 1;
	}
}



