var scripts = document.scripts;
//var AlphaNumSet = createAlphaNumSet();
var alphaNumProbStrategy = {"UniformProb" : 1.0/62.0};

var nGramSize = 1;
var obj = {};

for(var i = 4; i<=4; i++){
	run(i);
}

self.port.emit("script-response", "ScriptLength = " + scripts.length); self
//self.port.emit("script-response", "data = " + JSON.stringify(obj));



function run(ngram){
	nGramSize = ngram;
	
	//calculate benign and malicious probability score for each script
	for(var i = 0; i < scripts.length; i++ ){
		//self.port.emit("script-response", scripts[i].text);
		
		var compositeBenignScore = query(scripts[i].text, nGramSize, "benign", "composite");
		
		
	}
	
}

function query(script, nGramSize, type, model){
		
		script = compositeWordTransform(script);
		self.port.emit("script-response", "length = " + script.length);
		self.port.emit("script-response", "data = " + script);
	
}

