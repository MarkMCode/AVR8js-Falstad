# AVR8js-Falstad
Project combining Wokwi's AVR8js Arduino simulator (https://github.com/wokwi/avr8js) and Paul Falstad's CircuitJS1 circuit simulator (https://github.com/pfalstad/circuitjs1).

## Running
To run this project, simply open /war/index.html in a compatible browser (known to work with Chrome)

## Using
1. Write an Arduino script in the Arduino IDE
2. Select Sketch => Export compiled binary
3. Open the compiled hex file in a text editor and copy the contents to clipboard
4. Paste this into the text area on the right side of AVR8js-Falstad
5. Select Draw => Add Arduino Pin and drag out an Arduino pin element (currently, they are drawn as voltage sources and the negative side must be connected to a ground element)
6. Create your circuit and double click on each Arduino Pin element to change which pin it represents
7. Run the Arduino script and ensure that the "Simulation Speed" slider is not zero

## Building
To build the CircuitJS1 part of the application, follow the instructions at https://github.com/pfalstad/circuitjs1#building-the-web-application ***Note:You must be using GWT version 2.8.1 and JDK 1.8.0 to build this. The compiler argument -generateJsInteropExports must be passed***

To build the avr8js part of the application, read the README in /avr8js and use "npm run-script build:demo". This utilises my edited package.json file to build my edited version of the demo project.  

To integrate these two, move the contents of /avr8js/demo/dist to /war/AVR8js and edit the JS file argument on line 99 of /src/com/lushprojects/circuitjs1/client/circuitjs1.java (motorScript.setPropertyString("src", "AVR8js/src.30e1f952.js")) to reflect the new file being used. 


## License information
AVR8js (available at https://github.com/wokwi/avr8js) is used under the MIT License: https://github.com/wokwi/avr8js/blob/master/LICENSE

CircuitJS1 (available at https://github.com/pfalstad/circuitjs1) is used under the GPL 2.0 License: https://github.com/pfalstad/circuitjs1/blob/master/COPYING.txt

As a derivative work of AVR8js and CircuitJS1, this project is under the GPL 2.0 License.
