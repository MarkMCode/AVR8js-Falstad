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

const BLINK_CODE = `
//Pin 7 is accessed by pin D7
//Pin 13 is accessed by pin B5
//Pin 11 is accessed by pin B3

void setup() {
  pinMode(13, OUTPUT);
  pinMode(7, INPUT);
  digitalWrite(13, LOW);
  analogWrite(11, 100);
   Serial.begin(115200);
   Serial.println("Program is starting...");
}

void loop() {
  if(digitalRead(7) == HIGH){
    digitalWrite(13, HIGH);
  }

  else if(digitalRead(7) == LOW){
    digitalWrite(13, LOW);
  }
}`.trim();

// Load Editor
declare const window: any; // eslint-disable-line @typescript-eslint/no-explicit-any
declare const monaco: any; // eslint-disable-line @typescript-eslint/no-explicit-any
window.require.config({
  paths: { vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.20.0/min/vs' },
});
window.require(['vs/editor/editor.main'], () => {
  editor = monaco.editor.create(document.querySelector('.code-editor'), {
    value: EditorHistoryUtil.getValue() || BLINK_CODE,
    language: 'cpp',
    minimap: { enabled: false },
  });
});

const runButton = document.querySelector('#run-button');
runButton.addEventListener('click', compileAndRun);
const stopButton = document.querySelector('#stop-button');
stopButton.addEventListener('click', stopCode);
const revertButton = document.querySelector('#revert-button');
revertButton.addEventListener('click', setBlinkSnippet);
const statusLabel = document.querySelector('#status-label');
const compilerOutputText = document.querySelector('#compiler-output-text');
const serialOutputText = document.querySelector('#serial-output-text');

function executeProgram(hex: string) {
   globalThis.AVR8jsFalstad.Runner = new AVRRunner(hex);
   const MHZ = 16000000;

  globalThis.AVR8jsFalstad.Runner.usart.onByteTransmit = (value) => {
    serialOutputText.textContent += String.fromCharCode(value);
  };
  const cpuPerf = new CPUPerformance(globalThis.AVR8jsFalstad.Runner.cpu, MHZ);
  globalThis.AVR8jsFalstad.Runner.execute((cpu) => {
    const time = formatTime(cpu.cycles / MHZ);
    const speed = (cpuPerf.update() * 100).toFixed(0);
    statusLabel.textContent = `Simulation time: ${time} (${speed}%)`;
  });
}

async function compileAndRun() {
  storeUserSnippet();

  runButton.setAttribute('disabled', '1');
  revertButton.setAttribute('disabled', '1');

  serialOutputText.textContent = '';
  try {
    statusLabel.textContent = 'Compiling...';
    const result = await buildHex(editor.getModel().getValue());
    compilerOutputText.textContent = result.stderr || result.stdout;
    if (result.hex) {
      compilerOutputText.textContent += '\nProgram running...';
      stopButton.removeAttribute('disabled');
      executeProgram(result.hex);
    } else {
      runButton.removeAttribute('disabled');
    }
  } catch (err) {
    runButton.removeAttribute('disabled');
    revertButton.removeAttribute('disabled');
    alert('Failed: ' + err);
  } finally {
    statusLabel.textContent = '';
  }
}

function storeUserSnippet() {
  EditorHistoryUtil.clearSnippet();
  EditorHistoryUtil.storeSnippet(editor.getValue());
}

function stopCode() {
  stopButton.setAttribute('disabled', '1');
  runButton.removeAttribute('disabled');
  //revertButton.removeAttribute('disabled');
  if (globalThis.AVR8jsFalstad.Runner) {
    globalThis.AVR8jsFalstad.Runner.stop();
    globalThis.AVR8jsFalstad.Runner = null;
  }
}

function setBlinkSnippet() {
  editor.setValue(BLINK_CODE);
  EditorHistoryUtil.storeSnippet(editor.getValue());
}
