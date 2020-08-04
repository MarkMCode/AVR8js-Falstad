//Created by Mark Megarry in July 2020 as a debug example for ThreeFalstad
//Prints the voltageDiff value of the inductor in the default Falstad circuit to the console if JSCircuitElm.Java is included in compilation
//Can be injected into GWT app by placing it in the "war" folder and calling:
//ScriptInjector.fromUrl("LogInductor.js").setWindow(ScriptInjector.TOP_WINDOW).inject(); from the Java source code

var inductor = new JSCircuitElm(4);
console.log(inductor.getVoltageDiff());
