//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
//Sets the resistance of the resistor in the default Falstad circuit to 20k if JSCircuitElm.Java is included in compilation
//Can be injected into GWT app by placing it in the "war" folder and calling:
//ScriptInjector.fromUrl("SetResistance.js").setWindow(ScriptInjector.TOP_WINDOW).inject(); from the Java source code
var resistor = new JSCircuitElm(0);
resistor.setValue(20000);
