import processing.core.PApplet;
import java.util.ArrayList;
import models.Ball; // Import the Ball class from the models package

public class GameManager {
    Cannon cannon; // El cañón
    ArrayList<Ball> balls; // Lista de bolas disparadas
    int frameWidth, frameHeight;
    boolean gravityEnabled = true; // Gravedad activada por defecto
    boolean debugMode = false; // Modo debug desactivado inicialmente
    float gravityForce = 0.2f; // Valor de la fuerza de gravedad
    int collisionCount = 0; // Contador de colisiones entre bolas
    Collector collector; // Colector de bolas
    PApplet parent;

    GameManager(int frameWidth, int frameHeight, PApplet parent) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.parent = parent;

        // Crear el cañón
        cannon = new Cannon(100, frameHeight - 100, frameWidth, frameHeight, parent);

        // Inicializar la lista de bolas
        balls = new ArrayList<>();

        // Crear el colector en el centro de la pantalla a media altura
        float collectorWidth = 100;
        float collectorHeight = 150;
        collector = new Collector(
                frameWidth / 2 - collectorWidth / 2, // Centramos horizontalmente
                frameHeight / 2 - collectorHeight / 2, // Centramos verticalmente
                collectorWidth,
                collectorHeight,
                parent
        );
    }

    void update() {
        cannon.update(balls);

        // Actualizar las bolas existentes
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball b = balls.get(i);
            b.update(gravityEnabled, gravityForce);

            // Verificar si la bola entra en el colector por la parte superior
            if (checkCollectorEntry(b, collector)) {
                balls.remove(i);
                collector.addBall(b);
                continue;
            }

            // Eliminar bolas que han dejado de moverse
            if (b.shouldDisappear()) {
                balls.remove(i);
            }
        }

        // Actualizar el colector y pasar la lista de bolas
        collector.update(balls);

        // Verificar si el colector está lleno
        if (collector.getBallCount() >= collector.capacity && !collector.gateOpen) {
            collector.openGate();
        }

        // Verificar colisiones entre bolas (incluyendo las que están fuera del colector)
        checkBallCollisions();
    }

    void display() {
        // Dibujar el fondo
        parent.background(128); // Fondo gris

        // Dibujar el marco del juego
        parent.fill(0); // Negro
        parent.rect(0, 0, frameWidth, 20); // Borde superior
        parent.rect(0, frameHeight - 20, frameWidth, 20); // Borde inferior
        parent.rect(0, 0, 20, frameHeight); // Borde izquierdo
        parent.rect(frameWidth - 20, 0, 20, frameHeight); // Borde derecho

        // Dibujar el colector
        collector.display();

        // Dibujar el cañón
        cannon.display();

        // Dibujar las bolas restantes (las que no están en el colector)
        for (Ball b : balls) {
            b.display(parent);
        }

        // Mostrar información de Debug si está activado
        if (debugMode) {
            displayDebugInfo();
        }
    }

    void checkBallCollisions() {
        // Verificar colisiones entre bolas
        for (int i = 0; i < balls.size(); i++) {
            Ball b1 = balls.get(i);
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b2 = balls.get(j);
                checkAndResolveCollision(b1, b2);
            }
        }

        // Verificar colisiones entre bolas dentro del colector
        for (int i = 0; i < collector.collectedBalls.size(); i++) {
            Ball b1 = collector.collectedBalls.get(i);
            for (int j = i + 1; j < collector.collectedBalls.size(); j++) {
                Ball b2 = collector.collectedBalls.get(j);
                checkAndResolveCollision(b1, b2);
            }
        }
    }

    void checkAndResolveCollision(Ball b1, Ball b2) {
        float dx = b2.x - b1.x;
        float dy = b2.y - b1.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float minDistance = b1.radius + b2.radius;

        if (distance < minDistance && distance > 0) {
            // Calcular la superposición y ajustar las posiciones
            float overlap = minDistance - distance;
            float moveX = overlap * dx / distance;
            float moveY = overlap * dy / distance;

            b1.x -= moveX * 0.5f;
            b1.y -= moveY * 0.5f;
            b2.x += moveX * 0.5f;
            b2.y += moveY * 0.5f;

            // Calcular nuevas velocidades (similar a checkCollisionWith en Ball)
            float nx = dx / distance;
            float ny = dy / distance;
            float tx = -ny;
            float ty = nx;

            float v1n = b1.vx * nx + b1.vy * ny;
            float v1t = b1.vx * tx + b1.vy * ty;
            float v2n = b2.vx * nx + b2.vy * ny;
            float v2t = b2.vx * tx + b2.vy * ty;

            float restitutionCoefficient = Math.min(b1.restitution, b2.restitution);

            float massSum = b1.mass + b2.mass;
            float massDiff = b1.mass - b2.mass;

            float v1nAfter = (v1n * massDiff + 2 * b2.mass * v2n) / massSum;
            float v2nAfter = (v2n * (-massDiff) + 2 * b1.mass * v1n) / massSum;

            v1nAfter *= restitutionCoefficient;
            v2nAfter *= restitutionCoefficient;

            float v1nAfterX = v1nAfter * nx;
            float v1nAfterY = v1nAfter * ny;
            float v1tAfterX = v1t * tx;
            float v1tAfterY = v1t * ty;

            float v2nAfterX = v2nAfter * nx;
            float v2nAfterY = v2nAfter * ny;
            float v2tAfterX = v2t * tx;
            float v2tAfterY = v2t * ty;

            b1.vx = v1nAfterX + v1tAfterX;
            b1.vy = v1nAfterY + v1tAfterY;
            b2.vx = v2nAfterX + v2tAfterX;
            b2.vy = v2nAfterY + v2tAfterY;

            // Manejar colisión
            b1.handleCollision();
            b2.handleCollision();
        }
    }

    void keyPressed(int keyCode, char key) {
        // Controles del cañón
        if (key == ' ') {
            cannon.startCharging();
        } else if (key == 'a' || key == 'A') {
            cannon.toggleAutoMode();
        }

        // Controles generales
        if (key == 'g' || key == 'G') {
            gravityEnabled = !gravityEnabled;
        } else if (key == 'd' || key == 'D') {
            debugMode = !debugMode;
        }
    }

    void keyReleased(char key) {
        // Disparo del cañón
        if (key == ' ' && cannon.charging) {
            float chargePercentage = cannon.getChargePercentage();
            // Verificar si el cañón ya disparó automáticamente
            if (!cannon.firedWhileCharging) {
                cannon.fire(balls, chargePercentage);
            }
            // Si ya disparó automáticamente, no hacemos nada
        }
    }

    void displayDebugInfo() {
        // Mostrar información de depuración
        parent.fill(50, 50, 50, 200); // Fondo oscuro con transparencia
        parent.rect(frameWidth - 220, 20, 200, 160);

        parent.fill(255); // Texto en blanco
        parent.textSize(14);
        parent.textAlign(PApplet.LEFT, PApplet.TOP);
        parent.text("Debug Mode:", frameWidth - 210, 30);
        parent.text("Gravity: " + (gravityEnabled ? "ON" : "OFF"), frameWidth - 210, 50);
        parent.text("Gravity Force: " + gravityForce, frameWidth - 210, 70);
        parent.text("Ball Count: " + balls.size(), frameWidth - 210, 90);
        parent.text("Collision Count: " + collisionCount, frameWidth - 210, 110);
        parent.text("Auto Mode: " + (cannon.autoMode ? "ON" : "OFF"), frameWidth - 210, 130);
        parent.text("Collector Balls: " + collector.getBallCount(), frameWidth - 210, 150);
    }

    // Método para verificar si una bola entra en el colector por la parte superior
    boolean checkCollectorEntry(Ball b, Collector collector) {
        /*if (collector.gateOpen) {
            // Si la compuerta está abierta, no aceptar nuevas bolas
            return true;
        }*/

        // La bola debe estar dentro del área horizontal del colector
        boolean withinHorizontalBounds = b.x + b.radius > collector.x && b.x - b.radius < collector.x + collector.width;
        // La bola debe entrar por la parte superior (y no estar ya dentro del colector)
        boolean enteringFromTop = b.y + b.radius > collector.y && b.y - b.radius < collector.y + 5; // 5 píxeles de margen superior

        return withinHorizontalBounds && enteringFromTop;
    }
}
