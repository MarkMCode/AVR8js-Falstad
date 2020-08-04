//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
//This code requires the -generateJsInteropExports compiler argument to be passed
package com.lushprojects.circuitjs1.client;
import jsinterop.annotations.JsType;	//Required for JsInterop
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
}
