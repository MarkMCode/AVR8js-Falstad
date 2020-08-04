//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
//NOTE: This code snippet requires the int nodeCounter to have been previously declared
public void nodeAction() {	//Display value of selected node and inc. selected node
	String buttonText = "Node " + String.valueOf(nodeCounter) + ": ";	//String to store text to write to button
	CircuitNode tempCN = getCircuitNode(nodeCounter);	//Select a node
	for (int links = 0; links< tempCN.links.size(); links++) {	//Get node links of TempCN
        	CircuitNodeLink tempCNL = (CircuitNodeLink)tempCN.links.elementAt(links);	//Select a link
        	//Get "info" from link element
        	String type [] = new String[10];
        	tempCNL.elm.getInfo(type);
        	//Get attributes of link element
        	double voltageDiff = tempCNL.elm.getVoltageDiff();
        	double post0Voltage = tempCNL.elm.volts[0];
        	double post1Voltage = tempCNL.elm.volts[1];
        	double current = tempCNL.elm.getCurrent();
        	//Display info in button
		buttonText += "<br>Link "+String.valueOf(links) +": ";	//Display link no.
		buttonText += "<br>Type: " + type[0] + ", " + type[3];	//Display elm type
		buttonText += "<br>Vd: " + String.valueOf(Math.round(voltageDiff * 100.0) / 100.0);//Display VDiff
		buttonText += "<br>Post 0 voltage: " + String.valueOf(Math.round(post0Voltage * 100.0) / 100.0);//Post 0 voltage
		buttonText += "<br>Post 1 voltage: " + String.valueOf(Math.round(post1Voltage * 100.0) / 100.0);//Post 1 voltage
		buttonText += "<br>Current: " + String.valueOf(current);	//Current
	}
	nodeButton.setHTML(buttonText);	//Write buttonText to button
	if(nodeCounter < nodeList.size() - 1) {	//Inc nodeCounter if appropriate
	    nodeCounter++;
	}else {
	    nodeCounter = 0;
	}	
}
