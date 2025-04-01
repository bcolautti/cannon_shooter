package models;

import processing.core.PApplet;

public class Ball {
    public float x, y, vx, vy; // Posición (x, y) y velocidad (vx, vy) de la bola.
    public float radius = 5; // Radio de la bola.
    public float mass; // Masa de la bola (calculada basada en el radio).
    public int color; // Color de la bola.
    public float restitution = 0.7f; // Coeficiente de restitución (elasticidad de la colisión).
    public boolean isInCollector = false; // Indica si la bola está dentro del colector.

    public float maxFallSpeed = 10; // Velocidad máxima de caída de la bola.
    public float airResistance = 0.98f; // Factor de resistencia del aire (reduce la velocidad con el tiempo).
    public float stopThreshold_x = 0.1f; // Umbral para detener la velocidad horizontal (si es menor, se detiene).
    public float stopThreshold_y = 0.2f; // Umbral para detener la velocidad vertical (si es menor, se detiene).
    public boolean firstUpdate = true; // Indica si es la primera actualización de la bola (para la gravedad).
    PApplet parent; // Referencia al objeto PApplet para dibujar la bola.

    public Ball(float x, float y, float vx, float vy, PApplet parent) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = PApplet.PI * radius * radius;
        this.parent = parent;
        this.color = parent.color(0, 255, 100);
    }

    public boolean shouldDisappear() {
        return false; // Por ahora, las bolas no desaparecen.
    }

    public void display() {
    //	System.out.println(radius + ".x " + x + "/y " + y);
    	parent.fill(color);
    	parent.ellipse(x, y, radius * 2, radius * 2);
    }
} 
