//Pin 7 is accessed by pin D7
//Pin 13 is accessed by pin B5
//Pin 11 is accessed by pin B3

void setup() {
  pinMode(0, OUTPUT);
  digitalWrite(0, HIGH);
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
    //Serial.println(analogRead(A1));
  }

  else if(digitalRead(7) == LOW){
    digitalWrite(13, LOW);
  }

}
