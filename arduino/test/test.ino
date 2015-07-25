String frase = "Hello World";
String str;
char c;
char matriz[20];
int x = 0;
void setup()
{
  Serial.begin(9600);
}
void loop()
{
  if (Serial.available())
  {
    do {
      c = Serial.read();
      matriz[x] = c;
      Serial.print(matriz[x], DEC);
      x++;
      delay(1);      //Delay para o Arduino não perder o dado da Serial
    } while (c != '\n');
    matriz[x - 1] = '\0';
    Serial.print(matriz);
    str = matriz;
    if (str == frase)
    {
      Serial.println("OK");
    }
    else
    {
      Serial.println("Erro");
    }
  }
}
