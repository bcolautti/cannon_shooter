class Ball {
  float x, y, vx, vy;
  float radius = 5;
  float mass;
  color c;
  float maxFallSpeed = 10;
  float airResistance = 0.98; // Resistencia del aire
  float restitution = 0.7;    // Coeficiente de restitución
  float stopThreshold = 0.1;
  int collisionCount = 0;
  boolean isInCollector = false; // Indica si la bola está en el colector
  boolean firstUpdate = true;    // Indica si es el primer fotograma de actualización

  Ball(float x, float y, float vx, float vy) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.mass = PI * radius * radius; // Masa proporcional al área
    this.c = color(0, 100, 100);      // Color inicial (rojo en HSB)
  }

  void update(boolean gravityEnabled, float gravityForce) {
    if (!isInCollector) {
      // Actualizar posición con base en la velocidad
      x += vx;
      y += vy;

      // Aplicar gravedad si está habilitada y no es el primer fotograma
      if (gravityEnabled && !firstUpdate) {
        vy += gravityForce;
        vy = constrain(vy, -maxFallSpeed, maxFallSpeed);
      }

      // Aplicar resistencia del aire
      vx *= airResistance;
      vy *= airResistance;

      // Colisiones con los bordes internos del lienzo
      // Borde izquierdo
      if (x - radius < 20) {
        x = 20 + radius;
        vx *= -restitution;
      }
      // Borde derecho
      if (x + radius > width - 20) {
        x = width - 20 - radius;
        vx *= -restitution;
      }
      // Borde superior
      if (y - radius < 20) {
        y = 20 + radius;
        vy *= -restitution;
      }
      // Borde inferior
      if (y + radius > height - 20) {
        y = height - 20 - radius;
        vy *= -restitution;

        if (abs(vy) < stopThreshold) {
          vy = 0;
        }
      }

      // Detener la bola si las velocidades son muy pequeñas
      if (abs(vx) < stopThreshold) {
        vx = 0;
      }
      if (abs(vy) < stopThreshold) {
        vy = 0;
      }

      // Marcar que el primer fotograma ha sido procesado
      firstUpdate = false;
    } else {
      // La actualización de bolas en el colector se maneja en la clase Collector
    }
  }

  void handleCollision() {
    collisionCount++;
    // Opcionalmente, puedes agregar efectos aquí
  }

  void checkCollisionWith(Ball other) {
/*    if (isInCollector || other.isInCollector) {
      // No verificar colisiones si alguna de las bolas está en el colector
      return;
    }
*/
    // Código de colisión entre bolas (igual que antes)
    float dx = other.x - x;
    float dy = other.y - y;
    float distance = sqrt(dx * dx + dy * dy);
    float minDistance = radius + other.radius;

    if (distance < minDistance && distance > 0) {
      // Vector normal y tangente
      float nx = dx / distance;
      float ny = dy / distance;
      float tx = -ny;
      float ty = nx;

      // Velocidades proyectadas
      float v1n = vx * nx + vy * ny;
      float v1t = vx * tx + vy * ty;
      float v2n = other.vx * nx + other.vy * ny;
      float v2t = other.vx * tx + other.vy * ty;

      // Coeficiente de restitución
      float restitutionCoefficient = min(this.restitution, other.restitution);

      // Nuevas velocidades normales (considerando masas)
      float massSum = this.mass + other.mass;
      float massDiff = this.mass - other.mass;

      float v1nAfter = (v1n * massDiff + 2 * other.mass * v2n) / massSum;
      float v2nAfter = (v2n * (-massDiff) + 2 * this.mass * v1n) / massSum;

      // Aplicar el coeficiente de restitución
      v1nAfter *= restitutionCoefficient;
      v2nAfter *= restitutionCoefficient;

      // Convertir a vectores
      float v1nAfterX = v1nAfter * nx;
      float v1nAfterY = v1nAfter * ny;
      float v1tAfterX = v1t * tx;
      float v1tAfterY = v1t * ty;

      float v2nAfterX = v2nAfter * nx;
      float v2nAfterY = v2nAfter * ny;
      float v2tAfterX = v2t * tx;
      float v2tAfterY = v2t * ty;

      // Velocidades finales
      vx = v1nAfterX + v1tAfterX;
      vy = v1nAfterY + v1tAfterY;
      other.vx = v2nAfterX + v2tAfterX;
      other.vy = v2nAfterY + v2tAfterY;

      // Ajustar posiciones para evitar superposición
      float overlap = minDistance - distance;
      x -= nx * overlap * (other.mass / massSum);
      y -= ny * overlap * (other.mass / massSum);
      other.x += nx * overlap * (this.mass / massSum);
      other.y += ny * overlap * (this.mass / massSum);

      // Manejar colisión
      handleCollision();
      other.handleCollision();
    }
  }

  // Método para determinar si la bola debe desaparecer (cuando está detenida)
  boolean shouldDisappear() {
    //return vx == 0 && vy == 0;
    return false;
  }

  // Método display para bolas fuera del colector
  void display() {
    display(0, 0);
  }

  // Método display con offset para bolas dentro del colector
  private void display(float offsetX, float offsetY) {
    fill(c);
    ellipse(x + offsetX, y + offsetY, radius * 2, radius * 2);
  }
}
