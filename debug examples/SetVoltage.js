//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
//Sets voltage value if the element in question is a voltage source if JSCircuitElm is included in compilation
//Can be injected into GWT app by placing it in the "war" folder and calling:
//ScriptInjector.fromUrl("SetVoltage.js").setWindow(ScriptInjector.TOP_WINDOW).inject(); from the Java source code
var battery = new JSCircuitElm(5);
battery.setVoltage(10);
