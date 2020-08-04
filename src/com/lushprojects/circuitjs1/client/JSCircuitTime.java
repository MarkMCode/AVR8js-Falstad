//Written by Mark Megarry in July 2020 to provide Arduino simulation with CircuitJS1 and AVR8js

package com.lushprojects.circuitjs1.client;
import com.google.gwt.core.client.GWT;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = false, namespace = JsPackage.GLOBAL)	//JsInterop setup
public class JSCircuitTime{
    public double getTime() {
	return circuitjs1.mysim.t;
    }
}