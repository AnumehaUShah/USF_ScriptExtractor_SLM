<h2> How to use this repository </h2>

<h3> Firefox Add-on  </h3>
* Main entry point is the index.js
* data folder contains the following content scripts for the malware detection
    1. DetectComposite.js
    2. DetectInput.js
    3. DetectKeyword.js

<h3> DetectComposite.js </h3>
* Extracts the javascripts fro the open tab
* Passes the scripts to CompositeWordTransform function from CompositeWordTransform.js
* Calculates probability using the trainign models.


<h3> DetectKeyword.js </h3>
* Extracts the javascripts fro the open tab
* Passes the scripts to CharacterKeywordtransform function from characterKeywordtransform.js
* Calculates probability using the trainign models.
