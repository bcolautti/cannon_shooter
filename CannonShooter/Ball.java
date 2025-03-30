package models;

import processing.core.PApplet;

public class Ball {
    public float x, y, vx, vy;
    public float radius = 5;
    public float mass;
    public int c;
    public float restitution = 0.7f;
    public boolean isInCollector = false;
    
    float maxFallSpeed = 10;
    float airResistance = 0.98f;
    float stopThreshold = 0.1f;
    boolean firstUpdate = true;
    PApplet parent;

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
            x += vx;
            y += vy;

            if (gravityEnabled && !firstUpdate) {
                vy += gravityForce;
                vy = PApplet.constrain(vy, -maxFallSpeed, maxFallSpeed);
            }

            vx *= airResistance;
            vy *= airResistance;

            // Use parent.width and parent.height
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

                if (Math.abs(vy) < stopThreshold) {
                    vy = 0;
                }
            }

            if (Math.abs(vx) < stopThreshold) {
                vx = 0;
            }
            if (Math.abs(vy) < stopThreshold) {
                vy = 0;
            }

            firstUpdate = false;
        } else {
        }
    }



    public boolean shouldDisappear() {
        return false;
    }

    public void display(PApplet p) {
        display(p, 0, 0);
    }

    public void display(PApplet p, float offsetX, float offsetY) {
        p.fill(c);
        p.ellipse(x + offsetX, y + offsetY, radius * 2, radius * 2);
    }
}
