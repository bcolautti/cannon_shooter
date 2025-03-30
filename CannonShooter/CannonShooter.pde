int frameWidth = 1800; // Ancho del lienzo
int frameHeight = 768;
GameManager gameManager; // Administrador del juego

void settings() {
  size(frameWidth, frameHeight);
}

void setup() {
  noStroke();
  frameRate(60);
  colorMode(HSB, 360, 100, 100); // Modo de color HSB
  gameManager = new GameManager(frameWidth, frameHeight);
}

void draw() {
  background(128); // Fondo gris
  gameManager.update();
  gameManager.display();
}

void keyPressed() {
  gameManager.keyPressed(keyCode, key);
}

void keyReleased() {
  gameManager.keyReleased(key);
}


// Genera un color aleatorio con contraste
color randomContrastingColor() {
  return color(random(128, 256), random(128, 256), random(128, 256));
}
