//This is an edited version of the demo program provided by Wokwi at https://github.com/wokwi/avr8js/blob/master/demo/src/index.ts
//Edited by Mark Megarry August 2020

import { PinState } from 'avr8js';
import { buildHex } from './compile';
import { CPUPerformance } from './cpu-performance';
import { AVRRunner } from './execute';
import { formatTime } from './format-time';
import './index.css';
import { EditorHistoryUtil } from './utils/editor-history.util';
let editor: any; // eslint-disable-line @typescript-eslint/no-explicit-any

const runButton = document.querySelector('#run-button');
runButton.addEventListener('click', compileAndRun);
const stopButton = document.querySelector('#stop-button');
stopButton.addEventListener('click', stopCode);
const statusLabel = document.querySelector('#status-label');
const compilerOutputText = document.querySelector('#compiler-output-text');
const serialOutputText = document.querySelector('#serial-output-text');

function executeProgram(hex: string) {
   //globalThis.buttonState = false;
   globalThis.Runner = new AVRRunner(hex);
   const MHZ = 16000000;

  globalThis.Runner.usart.onByteTransmit = (value) => {
    serialOutputText.textContent += String.fromCharCode(value);
  };
  const cpuPerf = new CPUPerformance(globalThis.Runner.cpu, MHZ);
  globalThis.Runner.execute((cpu) => {
    const time = formatTime(cpu.cycles / MHZ);
    const speed = (cpuPerf.update() * 100).toFixed(0);
    statusLabel.textContent = `Simulation time: ${time} (${speed}%)`;
  });
}

async function compileAndRun() {
  //Read user input 
  var sourceCode = document.getElementById("textInput").value;
  if(sourceCode != ""){
	  runButton.setAttribute('disabled', '1');
	  serialOutputText.textContent = '';
	  try {
		//statusLabel.textContent = 'Compiling...';
		const result = sourceCode//await buildHex(sourceCode);
		//compilerOutputText.textContent = result.stderr || result.stdout;
		if (result) {
		  //compilerOutputText.textContent += '\nProgram running...';
		  stopButton.removeAttribute('disabled');
		  executeProgram(result)//.hex);
		} else {
		  runButton.removeAttribute('disabled');
		}
	  } catch (err) {
		runButton.removeAttribute('disabled');
		//revertButton.removeAttribute('disabled');
		alert('Failed: ' + err);
	  } finally {
		//statusLabel.textContent = '';
	  }
  }
  else{
	  alert("ERROR, EXPECTED HEX CODE INPUT");
  }
  
}

function stopCode() {
  stopButton.setAttribute('disabled', '1');
  runButton.removeAttribute('disabled');
  //revertButton.removeAttribute('disabled');
  if (globalThis.Runner) {
    globalThis.Runner.stop();
    globalThis.Runner = null;
  }
}
