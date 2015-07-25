const uint8_t BUFFER_SIZE = 20;
uint8_t size = 0;
char buffer[BUFFER_SIZE];

void serialEvent()
{
  while (Serial.available()) // Enquanto houverem bytes disponíveis;
  {
    char c = Serial.read(); // Lê byte do buffer serial;
    if (c == '\n') {
      if (size == 0) return;
      buffer[size] = 0;
      size = 0;
      if (!strcmp(buffer, "led_on")){
        led_on();
        Serial.println("led_on ok!");
      }
      else if (!strcmp(buffer, "led_off")) {
        led_off();
        Serial.println("led_off ok!");
      }
      else {
        Serial.println("Comando invalido!");
        return;
      }
    }
    else if (size < BUFFER_SIZE - 1) { // Adiciona carácter ao buffer se não estiver cheio.
      buffer[size] = c;
      ++size;
    }
  }
}

void led_on() {
  digitalWrite(13, 1);
}
void led_off() {
  digitalWrite(13, 0);
}

void setup() {
  // Inicializa a comunicação serial com uma taxa de 9600 bauds.
  Serial.begin(9600);
  pinMode(13, OUTPUT);
}

void loop() {
}
