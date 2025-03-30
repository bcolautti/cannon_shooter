import java.util.ArrayList;
import models.Ball;

public class BallManager {

    public void updateBalls(ArrayList<Ball> balls, boolean gravityEnabled, float gravityForce, Collector collector) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            ball.update(gravityEnabled, gravityForce);

            if (checkCollectorEntry(ball, collector)) {
                balls.remove(i);
                collector.addBall(ball);
                continue;
            }

            if (ball.shouldDisappear()) {
                balls.remove(i);
            }
        }
    }

    private boolean checkCollectorEntry(Ball ball, Collector collector) {
        boolean withinHorizontalBounds = ball.x + ball.radius > collector.x && ball.x - ball.radius < collector.x + collector.width;
        boolean enteringFromTop = ball.y + ball.radius > collector.y && ball.y - ball.radius < collector.y + 5;
        return withinHorizontalBounds && enteringFromTop;
    }
}