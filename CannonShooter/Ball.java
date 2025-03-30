package models;

import processing.core.PApplet;

public class Ball {
    public float x, y, vx, vy; // Posición (x, y) y velocidad (vx, vy) de la bola.
    public float radius = 5; // Radio de la bola.
    public float mass; // Masa de la bola (calculada basada en el radio).
    public int c; // Color de la bola.
    public float restitution = 0.7f; // Coeficiente de restitución (elasticidad de la colisión).
    public boolean isInCollector = false; // Indica si la bola está dentro del colector.

    float maxFallSpeed = 10; // Velocidad máxima de caída de la bola.
    float airResistance = 0.98f; // Factor de resistencia del aire (reduce la velocidad con el tiempo).
    float stopThreshold_x = 0.1f; // Umbral para detener la velocidad horizontal (si es menor, se detiene).
    float stopThreshold_y = 0.2f; // Umbral para detener la velocidad vertical (si es menor, se detiene).
    boolean firstUpdate = true; // Indica si es la primera actualización de la bola (para la gravedad).
    PApplet parent; // Referencia al objeto PApplet para dibujar la bola.

    public Ball(float x, float y, float vx, float vy, PApplet parent) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = PApplet.PI * radius * radius;
        this.parent = parent;
        this.c = parent.color(0, 100, 100);
    }

    public void update(boolean gravityEnabled, float gravityForce) {
        if (!isInCollector) {
            // Actualizar la posición de la bola sumando su velocidad actual.
            x += vx;
            y += vy;

            // Verificar si la gravedad está activada y si no es la primera actualización.
            if (gravityEnabled && !firstUpdate) {
                // Aplicar la fuerza de gravedad a la velocidad vertical.
                vy += gravityForce;

                // Limitar la velocidad de caída para evitar que la bola acelere demasiado.
                vy = PApplet.constrain(vy, -maxFallSpeed, maxFallSpeed);
            }

            // Aplicar resistencia del aire a las velocidades horizontal y vertical.
            vx *= airResistance;
            vy *= airResistance;

            // Verificar colisiones con los bordes del marco.
            if (x - radius < 20) {
                x = 20 + radius;
                vx *= -restitution;
            }
            if (x + radius > parent.width - 20) {
                x = parent.width - 20 - radius;
                vx *= -restitution;
            }
            if (y - radius < 20) {
                y = 20 + radius;
                vy *= -restitution;
            }
            if (y + radius > parent.height - 20) {
                y = parent.height - 20 - radius;
                vy *= -restitution;

                // Detener la bola si la velocidad vertical es muy baja.
                if (Math.abs(vy) < stopThreshold_y) {
                    vy = 0;
                } 
            }
            
            // Detener la bola si ambas velocidades son muy bajas.
            if (Math.abs(vx) < stopThreshold_x) {
                vx = 0;
            }

            firstUpdate = false;
        } else {
            // Lógica para cuando la bola está en el colector (si es necesario).
        }
    }

    public boolean shouldDisappear() {
        return false; // Por ahora, las bolas no desaparecen.
    }

    public void display(PApplet p) {
        display(p, 0, 0);
    }

    public void display(PApplet p, float offsetX, float offsetY) {
        p.fill(c);
        p.ellipse(x + offsetX, y + offsetY, radius * 2, radius * 2);
    }
} 