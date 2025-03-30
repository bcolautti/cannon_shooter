class Cannon {
  int x, y, frameWidth, frameHeight;
  float angle = -PI / 4; // Ángulo inicial en radianes (-45 grados)
  float cannonLength = 100; // Longitud del cañón
  boolean charging = false; // Indicador de carga
  boolean autoMode = false; // Indicador de modo automático
  int chargeStartTime = 0; // Tiempo cuando se comienza a cargar
  float maxChargeTime = 500; // Tiempo máximo de carga en milisegundos (500 ms)
  float autoMoveDirection = 1; // Dirección del movimiento automático (1 o -1)
  int lastAutoMoveTime = 0; // Última vez que el ángulo cambió en modo automático
  int lastAutoShotTime = 0; // Última vez que disparó en modo automático
  int autoShotInterval = 300; // Intervalo entre disparos automáticos (milisegundos)
  boolean firedWhileCharging = false; // Indicador si ya disparó durante la carga

  Cannon(int x, int y, int frameWidth, int frameHeight) {
    this.x = x;
    this.y = y;
    this.frameWidth = frameWidth;
    this.frameHeight = frameHeight;
  }

  void update(ArrayList<Ball> balls) {
    if (autoMode) {
      // Movimiento automático del cañón
      if (millis() - lastAutoMoveTime >= 100) {
        angle += autoMoveDirection * radians(1); // Cambiar ángulo automáticamente
        lastAutoMoveTime = millis();

        // Límites del ángulo en radianes
        float minAngle = -PI / 2; // -90 grados (apunta hacia arriba)
        float maxAngle = 0;       // 0 grados (apunta hacia la derecha)

        // Restringir el ángulo dentro de los límites
        if (angle > maxAngle || angle < minAngle) {
          autoMoveDirection *= -1; // Invertir dirección si se exceden los límites
          angle = constrain(angle, minAngle, maxAngle);
        }
      }

      // Disparo automático
      if (millis() - lastAutoShotTime >= autoShotInterval) {
        fire(balls, 1.0f); // Disparar con carga máxima
        lastAutoShotTime = millis();
      }
    } else {
      // Movimiento manual: apuntar hacia el mouse
      followMouse();

      // Si está cargando, verificar si alcanzó el tiempo máximo de carga
      if (charging) {
        float chargeTime = millis() - chargeStartTime;
        if (chargeTime >= maxChargeTime && !firedWhileCharging) {
          fire(balls, 1.0f); // Disparar automáticamente con carga máxima
          firedWhileCharging = true; // Evitar múltiples disparos
        }
      }
    }
  }

  void followMouse() {
    float dx = mouseX - x;
    float dy = mouseY - y;
    angle = atan2(dy, dx); // Calcular el ángulo hacia el mouse en radianes

    // Límites del ángulo
    float minAngle = -PI / 2; // -90 grados
    float maxAngle = 0;       // 0 grados

    // Restringir el ángulo dentro de los límites
    angle = constrain(angle, minAngle, maxAngle);
  }

  void display() {
    pushMatrix();
    translate(x, y);
    rotate(angle);
    fill(100); // Color gris del cañón
    rectMode(CORNER);
    rect(0, -10, cannonLength, 20); // Dibujar el cañón

    // Dibujar barra de carga si se está cargando
    if (charging) {
      float chargeTime = millis() - chargeStartTime;
      float chargePercentage = min(chargeTime / maxChargeTime, 1);
      float loadedLength = cannonLength * chargePercentage; // Largo de la barra roja
      fill(255, 0, 0); // Color rojo
      rect(0, -10, loadedLength, 20);
    }
    popMatrix();

    // Dibujar la base del cañón
    fill(150); // Color gris claro para la base
    ellipse(x, y, 40, 40);
  }

  void fire(ArrayList<Ball> balls, float chargePercentage) {
    float initialSpeed = 40 + chargePercentage * 25; // Velocidad inicial basada en la carga
    float vx = cos(angle) * initialSpeed; // Componente x de la velocidad
    float vy = sin(angle) * initialSpeed; // Componente y de la velocidad
    float startX = x + cos(angle) * cannonLength; // Posición inicial x de la bola
    float startY = y + sin(angle) * cannonLength; // Posición inicial y de la bola
   // for(int x = 0; x < 4; x++)
      balls.add(new Ball(startX, startY, vx, vy)); // Crear la bola y añadirla a la lista
    charging = false; // Finalizar la carga tras disparar
    firedWhileCharging = false; // Reiniciar el indicador de disparo
  }

  void startCharging() {
    if (!charging) {
      charging = true;
      chargeStartTime = millis(); // Registrar el tiempo de inicio de la carga
      firedWhileCharging = false; // Reiniciar el indicador de disparo
    }
  }

  float getChargePercentage() {
    if (charging) {
      float chargeTime = millis() - chargeStartTime;
      return min(chargeTime / maxChargeTime, 1); // Devolver el porcentaje de carga (máx 100%)
    } else {
      return 0;
    }
  }

  void toggleAutoMode() {
    autoMode = !autoMode; // Alternar modo automático
    lastAutoMoveTime = millis(); // Reiniciar el temporizador de movimiento
    lastAutoShotTime = millis(); // Permitir disparos inmediatos en el nuevo modo
  }

  // Método para devolver el ángulo actual como texto
  String getAngleInfo() {
    return nf(degrees(angle), 0, 2) + "°"; // Formatear el ángulo en grados con dos decimales
  }
}
