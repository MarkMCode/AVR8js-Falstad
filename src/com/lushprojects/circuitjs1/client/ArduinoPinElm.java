//Written by Mark Megarry in July 2020 to provide Arduino simulation with CircuitJS1 and AVR8js

package com.lushprojects.circuitjs1.client;
import com.google.gwt.core.client.GWT;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = false, namespace = JsPackage.GLOBAL)	//JsInterop setup
public class ArduinoPinElm extends CircuitElm{
   //Declare values
   public double inputVoltage;
   public double voltage;
   public String port;
   int portInt;
   public int pin;
   public int state = 2;	//Start as an input pin.	//0 = output low, 1 = output high, 2 = input
   int prevState = 2;	//Save previous state. All pins start in state 2 currently
   double resistance = 100 * Math.pow(10, 6);	//Resistance of input Arduino pin is equivalent to 100 megaOhms: https://www.arduino.cc/en/Tutorial/DigitalPins
   
   @JsIgnore
   public ArduinoPinElm(int xx, int yy){	//Constructor for when object is dragged out by user
       super(xx, yy);				
       pin = 0;		//Set pin to B0 whenever it is dragged out
       port = "B";
       checkAnalogArray();	//Create aanlogArray if necessary
       
   }
   
   @JsIgnore
   public ArduinoPinElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
   super(xa, ya, xb, yb, f);
   try {
	    portInt = new Integer(st.nextToken()).intValue();	//Set portInt (ints are easy to deal with in this context)
	    pin = new Integer(st.nextToken()).intValue();	//Set pin
	} catch (Exception e) {}
   switch(portInt) {	//Set port based on portInt (easier than dealing with strings in this context)
   case 0: port = "B"; break;
   case 1: port = "C"; break;
   case 2: port = "D"; break;
   }
   checkAnalogArray();	//Create analogArray if necessary
}
   
   public void doStep(){
       //Get state of pin and apply it to circuit simulation
       getState();
       switch(state) {
       	case 0:	//Pin is off
       	    voltage = 0.0;
	   setNodeVoltage(1, voltage);
	   sim.updateVoltageSource(nodes[0], nodes[1], voltSource,
			voltage);
	break;
       	case 1:	//Pin is on
       	    voltage = 5.0;
       	    setNodeVoltage(1, voltage);
 	   sim.updateVoltageSource(nodes[0], nodes[1], voltSource,
 			voltage);

       	break;
        case 2:	//Pin is an input
            inputVoltage = getVoltageDiff();
            if(port != "C") {
                    //Check what state to send to AVR8js
        	   if(inputVoltage >= 3.0) {	//Above high input threshold
        	       //state = 1;
        	       setState("true");
        	   }
        	   
        	   else if(inputVoltage <= 1.5) {	//Below low input threshold
        	       //state = 0;
        	       setState("false");
        	   }
            }
            else {
        	setAnalogArray(ADCoutput(inputVoltage));
            }
	   
	   
	break;
       }
       if(prevState != state) {		//If state has changed, circuit must be analysed
	   sim.needAnalyze();    //Analyse circuit
       }
       prevState = state;	//Save state
       
   }
   
   void stepFinished() {
       //TODO
   }
	
   //Get state of pin and save to state var
   //Use of eval is potentially dangerous as it can allow the user to execute arbitrary code
   public native void getState() /*-{
       if(typeof $wnd.AVR8jsFalstad.Runner !== "undefined"){	//Check for Runner
        	this.state = $wnd.eval("AVR8jsFalstad.Runner.port"+this.port+".pinState("+this.pin+");");	//TODO Ensure that port and pin are as expected,
       }											//i.e., they are a single char or int and not malicious arbitrary code
    }-*/;
    
   //Set state of pin
   public native void setState(String definedState) /*-{  //Check for Runner
           if(typeof $wnd.AVR8jsFalstad.Runner !== "undefined"){
           $wnd.eval("AVR8jsFalstad.Runner.port"+this.port+".setPin("+this.pin+","+definedState+");")		//TODO Ensure that port and pin are as expected,
           //refresh state of this pin								//i.e., they are a single char or int and not malicious arbitrary code
           this.getState();
       }
   }-*/;
   
   //Set analog array value
   public native void setAnalogArray(int adc) /*-{
       $wnd.eval("AVR8jsFalstad.analogArray["+this.pin+"] = " + adc+";");
   }-*/;
   
   //Check for presence of analogArray and create if it doesn't exist
   public native void checkAnalogArray() /*-{
       if (!($wnd.AVR8jsFalstad.analogArray instanceof Array)) {
    		$wnd.AVR8jsFalstad.analogArray = [];
	}
   }-*/;
   //Calculates approximate ADC value from a voltage.  Somewhat inaccurate and perhaps more precise than the real ADC
    int ADCoutput(double voltage) {
	return (int)((1023.0/5.0) * voltage);
    }
   //Calculate lead length when drawn
   void setPoints() {
	super.setPoints();
	calcLeads(8);
   }
   
   //Draw element as voltage source
   void draw(Graphics g) {
	setBbox(x, y, x2, y2);
	draw2Leads(g);
	setVoltageColor(g, volts[0]);
	setPowerColor(g, false);
	interpPoint2(lead1, lead2, ps1, ps2, 0, 10);
	drawThickLine(g, ps1, ps2);
	int hs = 16;
	setBbox(point1, point2, hs);
	interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
	drawThickLine(g, ps1, ps2);
	updateDotCount();
	if (sim.dragElm != this) {
	    drawDots(g, point1, point2, curcount);
	}
	drawPosts(g);
   }
   
   int getVoltageSourceCount() {
	return 1;
   }
   
   double getVoltage() {return voltage;}
   
   void getInfo(String arr[]) {		//Store information about element
  	arr[0] = "Arduino pin"; 	
  	arr[1] = "I = " + getCurrentText(getCurrent());
  	arr[2] = getVoltageText(getVoltageDiff());
      }
   
   double getPower() { return -getVoltageDiff()*current; }	//Return power through element
   
   double getVoltageDiff() {	//Return voltage across element
       if(state == 2) {		//If pin is an input, return voltage across internal resistor
	   return volts[1] - volts[2];
       }
       else {	//Otherwise return voltage between two terminals
	   return volts[1] - volts[0];
       }
   }
   
   public EditInfo getEditInfo(int n) {		//User-input box
       if(n == 0) {
	   EditInfo ei =  new EditInfo("Arduino port", portInt);
	   ei.choice = new Choice();
	   ei.choice.add("B");
	   ei.choice.add("C");
	   ei.choice.add("D");
	   ei.choice.select(portInt);
	   return ei;
	   
       }
       if(n==1) {
	 return new EditInfo("Pin", pin);
       }
       return null;
   }
   
   public void setEditValue(int n, EditInfo ei) {	//Act on user input
       if(n == 0) {
	   portInt = ei.choice.getSelectedIndex();
	   switch(portInt) {	//Set port
	   case 0: port = "B"; break;
	   case 1: port = "C"; break;
	   case 2: port = "D"; break;
	   }
       }
       
       if(n == 1) {
	   pin = (int)ei.value;	//Set pin
       }
       getState();
   }
   
   void stamp() {	//Update matrix
       getState();
       if(state == 2) {	//Input pin
	   sim.stampVoltageSource(nodes[0], nodes[2], voltSource,
		       getVoltage());
	   sim.stampResistor(nodes[1], nodes[2], resistance);	//Stamp resistor from node 1 to internal node
	   
       }
       else {	//Output node, no resistance
	   sim.stampVoltageSource(nodes[0], nodes[1], voltSource,
		       getVoltage());
       }

   }
   
   int getInternalNodeCount() {
       if(state == 2) {	//Pin is an input and therefore has an internal resistor
	   return 1;
       }
       else {return 0;}	//Pin is an output
   }
   
   String dump() {
       return super.dump() + " " + portInt + " " + pin;	//Save portInt and pin
   }
   int getDumpType() { return 412; }
   boolean nonLinear() { return true; }	//Check this
   double getCurrent(){
       if (state == 2) {	//If pin is an input, we are interested in the current entering it
	   return current * -1;
       }
       else {return current;}
   }
   
   public double getTime() {	//Return simulation time
       return circuitjs1.mysim.t;
   }
   
   public native void printTime() /*-{
   	console.log(this.getTime());		//Print simulation time to console
   }-*/;
	
}