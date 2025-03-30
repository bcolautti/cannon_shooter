import java.util.ArrayList;
import models.Ball;

public class CollisionManager {

    public void checkBallCollisions(ArrayList<Ball> balls, ArrayList<Ball> collectedBalls) {
        // Colisiones entre bolas fuera del colector
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                checkAndResolveCollision(balls.get(i), balls.get(j));
            }
        }

        // Colisiones entre bolas dentro del colector
        for (int i = 0; i < collectedBalls.size(); i++) {
            for (int j = i + 1; j < collectedBalls.size(); j++) {
                checkAndResolveCollision(collectedBalls.get(i), collectedBalls.get(j));
            }
        }
    }

    private void checkAndResolveCollision(Ball b1, Ball b2) {
        // Lógica de colisión (como antes)
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
        }
    }
}