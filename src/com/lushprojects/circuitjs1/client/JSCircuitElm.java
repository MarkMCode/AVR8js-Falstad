//Created by Mark Megarry June-July 2020 for use with CircuitJS1 circuit simulator
//This code requires the -generateJsInteropExports compiler argument to be passed
package com.lushprojects.circuitjs1.client;
import jsinterop.annotations.JsType;	//Required for JsInterop

//import com.gargoylesoftware.htmlunit.javascript.host.Console;

import jsinterop.annotations.JsPackage;	//Required for jsInterop

@JsType(isNative = false, namespace = JsPackage.GLOBAL)	//JsInterop setup
public class JSCircuitElm {
    public static native void console(String text)	//Log to console
    /*-{
        console.log(text);
    }-*/;
    CircuitElm elm;	//Element object
    public JSCircuitElm(int elm) {	//Constructor, assigns elm to element
        this.elm = circuitjs1.mysim.getElm(elm); 
    }
    //Get voltage difference
    public double getVoltageDiff() {
	return elm.getVoltageDiff();
    }
    //Get post voltage
    public double getPostVoltage(int n) {
	return elm.getPostVoltage(n);
    }
    //Get current
    public double getCurrent() {
	return elm.getCurrent();
    }
    
    //Set voltage if elm is a VoltageElm
    //POSSIBLY UNNECESSARY AS setValue WORKS FOR VOLTAGE SOURCE ELMS
    public void setVoltage(double volts) {
	EditInfo voltInfo = new EditInfo("Voltage", volts);	//Create EditInfo
        if(elm instanceof VoltageElm) {				//Check elm is a VoltageElm
    		((VoltageElm) elm).setEditValue(0, voltInfo);	//Update info of voltage source
    		circuitjs1.mysim.needAnalyze(); 		//analyse circuit
        }
        else {							//elm is not a VoltageElm
            console("ERROR: ELEMENT IS NOT A VOLTAGE SOURCE");	//Inform user
        }
        
    }
    
    //Set value
    public void setValue(double value) {
	EditInfo valueInfo = new EditInfo("Value", value);	//Create EditInfo
	elm.setEditValue(0, valueInfo);				//Update value of elm
	circuitjs1.mysim.needAnalyze();				//Analyze circuit
    }
    
    //Set position of a switch
    public void setSwitch(int pos) {
	if(elm instanceof SwitchElm) {				//Check if element is switch
	    ((SwitchElm)elm).position = pos;			//0=closed, 1=open
	    circuitjs1.mysim.needAnalyze();			//Analyze circuit
	}
	else {							//Element is not switch
	    console("ERROR: ELEMENT IS NOT A SWITCH");		//Inform user
	}
    }
    
    //Get current of element
    public double getPower() {
	return elm.getPower();
    }
    
    //Get ElmList size
    public int getElmListSize() {
	return circuitjs1.mysim.elmList.size();
    }
}
