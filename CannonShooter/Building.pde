class Building {
  int x, y, width, height;
  boolean[][] grid; // Representa las partes del edificio (true: intacto, false: destruido)
  int rows, cols; // Filas y columnas de la "grilla" del edificio
  int cellSize; // Tamaño de cada celda en la grilla
  float tilt = 0; // Inclinación del edificio en grados
  float tiltLimit = 15; // Límite máximo de inclinación antes de colapsar
  float tiltSpeed = 0.1; // Velocidad de inclinación
  boolean isCollapsing = false; // Estado de colapso

  Building(int frameWidth, int frameHeight, int cellSize) {
    this.cellSize = cellSize;

    // Dimensiones del edificio
    this.width = 300; // Ancho fijo
    this.height = 700; // Altura fija

    // Posición alineada con el suelo
    this.x = frameWidth / 2 - this.width / 2; // Centrar horizontalmente
    this.y = frameHeight - this.height - 20; // Posicionar sobre el suelo (20px de margen inferior)

    this.rows = height / cellSize;
    this.cols = width / cellSize;

    // Inicializar el edificio completamente intacto
    grid = new boolean[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        grid[i][j] = true; // Cada celda está intacta inicialmente
      }
    }
  }

  void update() {
    // Calcular el equilibrio basado en el daño
    if (!isCollapsing) {
      float leftDamage = calculateDamageInColumnRange(0, cols / 2);
      float rightDamage = calculateDamageInColumnRange(cols / 2, cols);

      // Ajustar inclinación basado en el daño en los lados
      tilt += (rightDamage - leftDamage) * tiltSpeed;
      tilt = constrain(tilt, -tiltLimit, tiltLimit);

      // Verificar si el edificio debe colapsar
      if (abs(tilt) >= tiltLimit) {
        isCollapsing = true;
      }
    } else {
      // Colapso gradual hacia un lado
      tilt += tilt > 0 ? 0.5 : -0.5;

      // Si el edificio colapsa completamente, detenerlo
      if (abs(tilt) >= 90) {
        tilt = constrain(tilt, -90, 90); // Fijar inclinación máxima
      }
    }
  }

  void display() {
    pushMatrix();
    translate(x + width / 2, y + height / 2); // Mover al centro del edificio
    rotate(radians(tilt)); // Aplicar inclinación
    translate(-width / 2, -height / 2); // Volver al origen local

    // Dibujar las celdas del edificio
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (grid[i][j]) {
          fill(150); // Color de las celdas intactas
        } else {
          fill(50); // Color de las celdas destruidas
        }
        rect(j * cellSize, i * cellSize, cellSize, cellSize);
      }
    }

    popMatrix();
  }

  ArrayList<Fragment> checkCollision(Ball ball) {
    ArrayList<Fragment> fragments = new ArrayList<>();
    int col = (int) ((ball.x - x) / cellSize);
    int row = (int) ((ball.y - y) / cellSize);

    // Verificar si la bola está dentro del edificio
    if (col >= 0 && col < cols && row >= 0 && row < rows && grid[row][col]) {
      // Calcular daño basado en la velocidad y radio de la bola
      float damage = ball.vx * ball.vx + ball.vy * ball.vy; // Velocidad al cuadrado
      int destructionRange = (int) map(damage, 0, 500, 1, 3); // Rango proporcional al daño

      // Destruir celdas alrededor del punto de impacto
      for (int i = -destructionRange; i <= destructionRange; i++) {
        for (int j = -destructionRange; j <= destructionRange; j++) {
          int destroyRow = row + i;
          int destroyCol = col + j;
          if (destroyRow >= 0 && destroyRow < rows && destroyCol >= 0 && destroyCol < cols) {
            grid[destroyRow][destroyCol] = false; // Marcar celda como destruida
          }
        }
      }

      // Crear fragmentos para la explosión
      int fragmentCount = 10 + (int) (ball.radius * 5); // Cantidad basada en el tamaño de la bala
      for (int i = 0; i < fragmentCount; i++) {
        float angle = random(TWO_PI);
        float speed = random(2, 5);
        float fragVx = cos(angle) * speed;
        float fragVy = sin(angle) * speed;
        float fragRadius = random(2, 5);
        fragments.add(new Fragment(ball.x, ball.y, fragVx, fragVy, fragRadius, ball.c));
      }
    }

    return fragments;
  }

  float calculateDamageInColumnRange(int startCol, int endCol) {
    float damage = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = startCol; j < endCol; j++) {
        if (!grid[i][j]) {
          damage++;
        }
      }
    }
    return damage / ((endCol - startCol) * rows); // Normalizar el daño
  }
}
