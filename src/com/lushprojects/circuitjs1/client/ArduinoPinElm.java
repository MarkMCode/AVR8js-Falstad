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
   public int state = 2;
   int prevState = 2;	//Save previous state. All pins start in state 2 currently
   double resistance = 100 * Math.pow(10, 6);	//Resistance of Arduino pin is equivalent to 100 megaOhms: https://www.arduino.cc/en/Tutorial/DigitalPins
   
   //Define port mappings
   static final int B = 0;
   static final int C = 1;
   static final int D = 2;
   
   @JsIgnore
   public ArduinoPinElm(int xx, int yy){	//Constructor for when object is dragged out by user
       super(xx, yy);				//May need to create private constructor for when circuit is loaded
       pin = 0;
       port = "B";
       
   }
   
   @JsIgnore
   public ArduinoPinElm(int xa, int ya, int xb, int yb, int f,
	    StringTokenizer st) {
   super(xa, ya, xb, yb, f);
   pin = 0;
   port = "B";
}
   
   public void doStep(){
       printTime();
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
	   /*voltage = 0.0;
	   this.volts[0] = 0.0;
	   this.volts[1] = 0.0;*/
	   inputVoltage = getVoltageDiff();
	   if(inputVoltage >= 3.0) {	//Above high input threshold
	       //state = 1;
	       setState("true");
	   }
	   
	   else if(inputVoltage <= 1.5) {	//Below low input threshold
	       //state = 0;
	       setState("false");
	   }
	   
	   
	break;
       }
       if(prevState != state) {
	   sim.needAnalyze();    //Analyse circuit
       }
       prevState = state;	//Save state
       
   }
   
   void stepFinished() {
       //TODO
   }
	
   //Get state of pin and save to state var
   public native int getState() /*-{
       if(typeof $wnd.Runner !== "undefined"){	//Check for Runner
        	//this.state = $wnd.Runner.portB.pinState(5);
        	this.state = $wnd.eval("Runner.port"+this.port+".pinState("+this.pin+");");
       }
    }-*/;
    
   //Set state of pin
   public native void setState(String definedState) /*-{  //Check for Runner
           if(typeof $wnd.Runner !== "undefined"){
           $wnd.eval("Runner.port"+this.port+".setPin("+this.pin+","+definedState+");")
           //refresh state of this pin
           this.getState();
       }
   }-*/;
   
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
   
   void getInfo(String arr[]) {
  	arr[0] = "Arduino pin"; 	
  	arr[1] = "I = " + getCurrentText(getCurrent());
  	arr[2] = getVoltageText(getVoltageDiff());
      }
   
   double getPower() { return -getVoltageDiff()*current; }
   
   double getVoltageDiff() {
       if(state == 2) {
	   return volts[1] - volts[2];
       }
       else {
	   return volts[1] - volts[0];
       }
   }
   
   public EditInfo getEditInfo(int n) {
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
   
   public void setEditValue(int n, EditInfo ei) {
       if(n == 0) {
	   portInt = ei.choice.getSelectedIndex();
	   switch(portInt) {
	   case 0: port = "B"; break;
	   case 1: port = "C"; break;
	   case 2: port = "D"; break;
	   }
       }
       
       if(n == 1) {
	   pin = (int)ei.value;
       }
       getState();
   }
   
   void stamp() {
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
       //getState();
       if(state == 2) {
	   return 1;
       }
       else {return 0;}
   }
   
   String dump() {
	// set flag so we know if duty cycle is correct for pulse waveforms
	//flags &= ~4;
       return super.dump() + " " + volts[0];
   }
   int getDumpType() { return 412; }
   boolean nonLinear() { return true; }
   double getCurrent(){
       if (state == 2) {
	   return current * -1;
       }
       else {return current;}
   }
   
   public double getTime() {
       return circuitjs1.mysim.t;
   }
   
   public native void printTime() /*-{
   console.log(this.getTime());
}-*/;
	
}