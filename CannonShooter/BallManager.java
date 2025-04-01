import java.util.ArrayList;
import models.Ball;
import processing.core.PApplet;

public class BallManager {
	
    private ArrayList<Ball> balls;
    private PApplet parent;
    

    public BallManager(PApplet parent) {
        this.parent = parent;
        this.balls = new ArrayList<>();
    }

    public void addBall(Ball ball) {
        balls.add(ball);
    }
    
    public int ballsCount() {return balls.size();}


    public void updateAndDisplayBalls(boolean gravityEnabled, float gravityForce) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);

            // Actualizar la posición y velocidad de la bola.
            updateBallPhysics(ball, gravityEnabled, gravityForce);

            // Verificar colisiones con los bordes del marco.
            checkBallCollisionsWithFrame(ball);

            // Verificar colisiones entre bolas.
            checkBallCollisionsBetweenBalls(ball);

            // Mostrar la bola.
            ball.display();
        }
    }

	private void updateBallPhysics(Ball ball, boolean gravityEnabled, float gravityForce) {
	    // Lógica de actualización de la posición y velocidad de la bola.
		 ball.x += ball.vx; 
		 ball.y += ball.vy;
	 
		 if (gravityEnabled && !ball.firstUpdate) { 
			 ball.vy += gravityForce; 
			 ball.vy = PApplet.constrain(ball.vy, -ball.maxFallSpeed, ball.maxFallSpeed); 
		 }
	 
		 ball.vx *= ball.airResistance; 
		 ball.vy *= ball.airResistance;
		  
		 ball.firstUpdate = false; 
	}

	private void checkBallCollisionsWithFrame(Ball ball) {
        // Lógica de colisión con los bordes del marco.
        if (ball.x - ball.radius < 20) {
            ball.x = 20 + ball.radius;
            ball.vx *= -ball.restitution;
        }
        if (ball.x + ball.radius > parent.width - 20) {
            ball.x = parent.width - 20 - ball.radius;
            ball.vx *= -ball.restitution;
        }
        if (ball.y - ball.radius < 20) {
            ball.y = 20 + ball.radius;
            ball.vy *= -ball.restitution;
        }
        if (ball.y + ball.radius > parent.height - 20) {
            ball.y = parent.height - 20 - ball.radius;
            ball.vy *= -ball.restitution;

            ball.restitution *= 0.9f;
            ball.vx *= 0.95f;

            if (ball.restitution < 0.1f) {
                ball.vy = 0;
            }
        }

        if (Math.abs(ball.vx) < ball.stopThreshold_x && Math.abs(ball.vy) < ball.stopThreshold_y) {
            ball.vx = 0;
            ball.vy = 0;
        }
    }

    private void checkBallCollisionsBetweenBalls(Ball ball) {
        // Lógica de colisión entre bolas.
        for (Ball other : balls) {
            if (other != ball) {
                float dx = other.x - ball.x;
                float dy = other.y - ball.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float minDistance = ball.radius + other.radius;

                if (distance < minDistance) {
                    float overlap = minDistance - distance;
                    float moveX = overlap * dx / distance;
                    float moveY = overlap * dy / distance;

                    ball.x -= moveX * 0.5f;
                    ball.y -= moveY * 0.5f;
                    other.x += moveX * 0.5f;
                    other.y += moveY * 0.5f;

                    float nx = dx / distance;
                    float ny = dy / distance;
                    float tx = -ny;
                    float ty = nx;

                    float v1n = ball.vx * nx + ball.vy * ny;
                    float v1t = ball.vx * tx + ball.vy * ty;
                    float v2n = other.vx * nx + other.vy * ny;
                    float v2t = other.vx * tx + other.vy * ty;

                    float restitutionCoefficient = Math.min(ball.restitution, other.restitution);

                    float massSum = ball.mass + other.mass;
                    float massDiff = ball.mass - other.mass;

                    float v1nAfter = (v1n * massDiff + 2 * other.mass * v2n) / massSum;
                    float v2nAfter = (v2n * (-massDiff) + 2 * ball.mass * v1n) / massSum;

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

                    ball.vx = v1nAfterX + v1tAfterX;
                    ball.vy = v1nAfterY + v1tAfterY;
                    other.vx = v2nAfterX + v2tAfterX;
                    other.vy = v2nAfterY + v2tAfterY;
                }
            }
        }
    }
}
