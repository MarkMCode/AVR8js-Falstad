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

}
