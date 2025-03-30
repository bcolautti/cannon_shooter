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
    int collisionCount = 0;
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

    public void handleCollision() {
        collisionCount++;
    }

    public void checkCollisionWith(Ball other) {
        float dx = other.x - x;
        float dy = other.y - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float minDistance = radius + other.radius;

        if (distance < minDistance && distance > 0) {
            float nx = dx / distance;
            float ny = dy / distance;
            float tx = -ny;
            float ty = nx;

            float v1n = vx * nx + vy * ny;
            float v1t = vx * tx + vy * ty;
            float v2n = other.vx * nx + other.vy * ny;
            float v2t = other.vx * tx + other.vy * ty;

            float restitutionCoefficient = Math.min(this.restitution, other.restitution);

            float massSum = this.mass + other.mass;
            float massDiff = this.mass - other.mass;

            float v1nAfter = (v1n * massDiff + 2 * other.mass * v2n) / massSum;
            float v2nAfter = (v2n * (-massDiff) + 2 * this.mass * v1n) / massSum;

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

            vx = v1nAfterX + v1tAfterX;
            vy = v1nAfterY + v1tAfterY;
            other.vx = v2nAfterX + v2tAfterX;
            other.vy = v2nAfterY + v2tAfterY;

            float overlap = minDistance - distance;
            x -= nx * overlap * (other.mass / massSum);
            y -= ny * overlap * (other.mass / massSum);
            other.x += nx * overlap * (this.mass / massSum);
            other.y += ny * overlap * (this.mass / massSum);

            handleCollision();
            other.handleCollision();
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
