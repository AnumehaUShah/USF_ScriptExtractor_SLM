/*****
Author: Anumeha Shah
This file is our main entry point of the add-on.
******/

var self = require('sdk/self');
var tabs = require('sdk/tabs');
var buttons = require("sdk/ui/button/action");



buttons.ActionButton({
	id: "attach-script",
	label: "Attach the script",
	icon:{
      "16": "./image/icon-16.png",
      "32": "./image/icon-32.png"
    },
	onClick: runScript
});

//function runscript attaches content scripts. The content scripts carry out javascript parsing, and exploit detection.
function runScript() {
	console.time("index");
  var job = tabs.activeTab.attach({
  	contentScriptFile: [self.data.url("models_50_wif/benign/CompositeBenignCountsA.js"),
  						self.data.url("models_50_wif/benign/CompositeBenignCountsAB.js"),
						self.data.url("models_50_wif/benign/CompositeBenignTotal.js"),
						self.data.url("models_50_wif/malicious/CompositeMaliciousTotal.js"),
  						self.data.url("models_50_wif/malicious/CompositeMaliciousCountsA.js"),
  						self.data.url("models_50_wif/malicious/CompositeMaliciousCountsAB.js"),
                        self.data.url("CompositeWordTransform.js"),
  						self.data.url("DetectComposite.js")]
                    
    
  });
  
  job.port.on("script-response", function(response) {
	
	console.log(response);
  });
}


