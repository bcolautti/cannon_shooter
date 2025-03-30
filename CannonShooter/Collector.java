import processing.core.PApplet;
import java.util.ArrayList;
import models.Ball;

public class Collector {
    public float x, y; // Posición del colector
    public float width, height; // Dimensiones del colector
    public ArrayList<Ball> collectedBalls; // Lista de bolas dentro del colector
    public boolean gateOpen = false; // Estado de la compuerta (base)
    private float gateHeight = 5; // Altura de la compuerta (base)
    public int capacity = 5; // Capacidad antes de abrir la base
    private PApplet parent;

    // Variables físicas
    float gravityForce = 0.2f;
    float airResistance = 0.98f;
    float restitution = 0.7f;

    Collector(float x, float y, float width, float height, PApplet parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
        collectedBalls = new ArrayList<>();
    }

    void update(ArrayList<Ball> balls) {
        if (!gateOpen) {
            // Actualizar las bolas dentro del colector
            for (Ball b : collectedBalls) {
                // Aplicar gravedad
                b.vy += gravityForce;
                b.vx *= airResistance;
                b.vy *= airResistance;

                // Actualizar posición
                b.x += b.vx;
                b.y += b.vy;

                // Colisiones con las paredes internas del colector
                // Pared izquierda
                if (b.x - b.radius < 0) {
                    b.x = b.radius;
                    b.vx *= -restitution;
                }
                // Pared derecha
                if (b.x + b.radius > width) {
                    b.x = width - b.radius;
                    b.vx *= -restitution;
                }
                // Pared inferior (si la compuerta está cerrada)
                if (b.y + b.radius > height) {
                    b.y = height - b.radius;
                    b.vy *= -restitution;
                }
            }
        } else {
            // Si la compuerta está abierta, las bolas caen libremente
            for (int i = collectedBalls.size() - 1; i >= 0; i--) {
                Ball b = collectedBalls.get(i);
                // Aplicar gravedad
                b.vy += gravityForce;
                b.vx *= airResistance;
                b.vy *= airResistance;

                // Actualizar posición
                b.x += b.vx;
                b.y += b.vy;
                
                // Verificar si la bola ha salido del colector
                if (b.y - b.radius > height) {
                    // Ajustar la posición al sistema global
                    b.x += x;
                    b.y += y;// + height;
                    b.isInCollector = false;
                    balls.add(b);
                    collectedBalls.remove(i);
                }
            }
        }

        // Verificar si se debe cerrar la compuerta
        checkAndCloseGate();
    }

    void checkAndCloseGate() {
        if (gateOpen && collectedBalls.isEmpty()) {
            gateOpen = false;
        }
    }

    void display() {
        // Dibujar el colector con la línea superior abierta
        parent.noFill();
        parent.stroke(0); // Bordes negros

        // Dibujar el lado izquierdo
        parent.line(x, y, x, y + height);
        // Dibujar el lado derecho
        parent.line(x + width, y, x + width, y + height);
        // Dibujar la base (compuerta)
        if (!gateOpen) {
            parent.line(x, y + height, x + width, y + height);
        }

        // Dibujar las bolas dentro del colector
        for (Ball b : collectedBalls) {
            b.display(parent, x, y);
        }
    }

    void addBall(Ball b) {
        // Solo añadir bolas si la compuerta está cerrada
        if (!gateOpen) {
            // Ajustar la posición de la bola relativa al colector
            b.x -= x;
            b.y -= y;
            b.isInCollector = true;
            collectedBalls.add(b);
        }
    }

    int getBallCount() {
        return collectedBalls.size();
    }

    void openGate() {
        gateOpen = true;
    }
}
