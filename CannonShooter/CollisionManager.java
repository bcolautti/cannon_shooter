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
        // 1. Calcular el vector de distancia entre los centros de las dos bolas.
        float dx = b2.x - b1.x; // Diferencia en la coordenada x.
        float dy = b2.y - b1.y; // Diferencia en la coordenada y.

        // 2. Calcular la distancia real entre los centros de las bolas.
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // 3. Calcular la distancia mínima requerida para que no haya colisión (suma de los radios).
        float minDistance = b1.radius + b2.radius;

        // 4. Verificar si las bolas están colisionando.
        if (distance < minDistance && distance > 0) {
            // 5. Calcular la superposición (cuánto se superponen las bolas).
            float overlap = minDistance - distance;

            // 6. Calcular el vector de movimiento para separar las bolas.
            float moveX = overlap * dx / distance;
            float moveY = overlap * dy / distance;

            // 7. Ajustar las posiciones de las bolas para separarlas.
            // Dividir el movimiento entre las dos bolas para que se muevan en direcciones opuestas.
            b1.x -= moveX * 0.5f;
            b1.y -= moveY * 0.5f;
            b2.x += moveX * 0.5f;
            b2.y += moveY * 0.5f;

            // 8. Calcular el vector normal de colisión (vector unitario en la dirección de la colisión).
            float nx = dx / distance;
            float ny = dy / distance;

            // 9. Calcular el vector tangente de colisión (perpendicular al vector normal).
            float tx = -ny;
            float ty = nx;

            // 10. Calcular las velocidades escalares de las bolas en las direcciones normal y tangente.
            float v1n = b1.vx * nx + b1.vy * ny; // Velocidad de b1 en la dirección normal.
            float v1t = b1.vx * tx + b1.vy * ty; // Velocidad de b1 en la dirección tangente.
            float v2n = b2.vx * nx + b2.vy * ny; // Velocidad de b2 en la dirección normal.
            float v2t = b2.vx * tx + b2.vy * ty; // Velocidad de b2 en la dirección tangente.

            // 11. Calcular el coeficiente de restitución (elasticidad de la colisión).
            float restitutionCoefficient = Math.min(b1.restitution, b2.restitution);

            // 12. Calcular las masas de las bolas.
            float massSum = b1.mass + b2.mass;
            float massDiff = b1.mass - b2.mass;

            // 13. Calcular las nuevas velocidades escalares en la dirección normal después de la colisión.
            float v1nAfter = (v1n * massDiff + 2 * b2.mass * v2n) / massSum;
            float v2nAfter = (v2n * (-massDiff) + 2 * b1.mass * v1n) / massSum;

            // 14. Aplicar el coeficiente de restitución a las nuevas velocidades.
            v1nAfter *= restitutionCoefficient;
            v2nAfter *= restitutionCoefficient;

            // 15. Convertir las nuevas velocidades escalares en vectores de velocidad.
            float v1nAfterX = v1nAfter * nx;
            float v1nAfterY = v1nAfter * ny;
            float v1tAfterX = v1t * tx;
            float v1tAfterY = v1t * ty;

            float v2nAfterX = v2nAfter * nx;
            float v2nAfterY = v2nAfter * ny;
            float v2tAfterX = v2t * tx;
            float v2tAfterY = v2t * ty;

            // 16. Asignar las nuevas velocidades a las bolas.
            b1.vx = v1nAfterX + v1tAfterX;
            b1.vy = v1nAfterY + v1tAfterY;
            b2.vx = v2nAfterX + v2tAfterX;
            b2.vy = v2nAfterY + v2tAfterY;
        }
    
    }
}