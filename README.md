# AVR8js-Falstad
Project combining Wokwi's AVR8js Arduino simulator (https://github.com/wokwi/avr8js) and Paul Falstad's CircuitJS1 circuit simulator (https://github.com/pfalstad/circuitjs1).

## Security note
This project is not being actively maintained, and as such may contain outdated or vulnerable dependencies.

## Running
To run this project, simply clone or download the repo and open /war/index.html in a compatible browser (known to work with Chrome).

You may need to disable CORS-related security features of your web browser for the application to work. On Linux, opening Chromium with ```chromium --disable-web-security --user-data-dir=[absolute_path_to_repo]``` should work, but **this is unsafe**.

## Using
1. Copy and paste an Arduino script into the text editor on the right side of the screen
2. Click "Run"
3. Double-click on each Arduino pin to assign it to an AVR8js pin
4. Create your own circuits by selecting Draw => Arduino Pin and dragging out Arduino pins (Arduino pins must have the "negative" node connected to a ground element)
5. Ensure that simulation speed is not set to 0!

## Building
To build the avr8js part of the application, first [install npm](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm). Once NPM is installed, navigate to ```/AVR8js```, run ```npm install```, and use ```npm run-script build:demo```. This utilises my edited package.json file to build my edited version of the demo project. **NOTE: It is recommended that you check for NPM package upgrades before running ```npm install```**.  

To build the CircuitJS1 part of the application, follow the instructions at https://github.com/pfalstad/circuitjs1#building-the-web-application ***Note:Built successfully using GWT version 2.12.1 and JavaSE-25, other versions may fail. The compiler argument -generateJsInteropExports must be passed***

To integrate these two, move the contents of /avr8js/demo/dist to /war/AVR8js and edit the JS file argument on line 99 of /src/com/lushprojects/circuitjs1/client/circuitjs1.java (motorScript.setPropertyString("src", "AVR8js/src.30e1f952.js")) to reflect the new file being used. Rebuild CircuitJS1 as above, and open /war/index.html 


## License information
AVR8js (available at https://github.com/wokwi/avr8js) is used under the MIT License: https://github.com/wokwi/avr8js/blob/master/LICENSE

CircuitJS1 (available at https://github.com/pfalstad/circuitjs1) is used under the GPL 2.0 License: https://github.com/pfalstad/circuitjs1/blob/master/COPYING.txt

As a derivative work of AVR8js and CircuitJS1, this project is under the GPL 2.0 License.
