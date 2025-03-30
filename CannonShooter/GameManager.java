import processing.core.PApplet;
import java.util.ArrayList;
import models.Ball;

public class GameManager {
    private Cannon cannon;
    private ArrayList<Ball> balls;
    private Collector collector;
    private PApplet parent;

    private int frameWidth, frameHeight;
    private boolean gravityEnabled = true;
    private boolean debugMode = false;
    private float gravityForce = 0.2f;

    private CollisionManager collisionManager;
    private BallManager ballManager;

    // Constantes para el marco del juego
    private static final int BORDER_WIDTH = 20;

    public GameManager(int frameWidth, int frameHeight, PApplet parent) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.parent = parent;

        initializeCannon();
        initializeBalls();
        initializeCollector();

        collisionManager = new CollisionManager();
        ballManager = new BallManager();
    }

    private void initializeCannon() {
        cannon = new Cannon(100, frameHeight - 100, frameWidth, frameHeight, parent);
    }

    private void initializeBalls() {
        balls = new ArrayList<>();
    }

    private void initializeCollector() {
        float collectorWidth = 100;
        float collectorHeight = 150;
        collector = new Collector(
                frameWidth / 2 - collectorWidth / 2,
                frameHeight / 2 - collectorHeight / 2,
                collectorWidth,
                collectorHeight,
                parent
        );
    }

    public void update() {
        cannon.update(balls);
        ballManager.updateBalls(balls, gravityEnabled, gravityForce, collector);
        collector.update(balls);
        checkCollectorFull();
        collisionManager.checkBallCollisions(balls, collector.collectedBalls);
    }

    private void checkCollectorFull() {
        if (collector.getBallCount() >= collector.capacity && !collector.gateOpen) {
            collector.openGate();
        }
    }

    public void display() {
        parent.background(128);
        drawGameFrame();
        collector.display();
        cannon.display();
        drawBalls();
        if (debugMode) {
            displayDebugInfo();
        }
    }

    private void drawGameFrame() {
        parent.fill(0);
        parent.rect(0, 0, frameWidth, BORDER_WIDTH);
        parent.rect(0, frameHeight - BORDER_WIDTH, frameWidth, BORDER_WIDTH);
        parent.rect(0, 0, BORDER_WIDTH, frameHeight);
        parent.rect(frameWidth - BORDER_WIDTH, 0, BORDER_WIDTH, frameHeight);
    }

    private void drawBalls() {
        for (Ball ball : balls) {
            ball.display(parent);
        }
    }

    private void displayDebugInfo() {
        // Lógica de depuración (como antes)
        parent.fill(50, 50, 50, 200);
        parent.rect(frameWidth - 220, 20, 200, 160);

        parent.fill(255);
        parent.textSize(14);
        parent.textAlign(PApplet.LEFT, PApplet.TOP);
        parent.text("Debug Mode:", frameWidth - 210, 30);
        parent.text("Gravity: " + (gravityEnabled ? "ON" : "OFF"), frameWidth - 210, 50);
        parent.text("Gravity Force: " + gravityForce, frameWidth - 210, 70);
        parent.text("Ball Count: " + balls.size(), frameWidth - 210, 90);
        parent.text("Auto Mode: " + (cannon.autoMode ? "ON" : "OFF"), frameWidth - 210, 130);
        parent.text("Collector Balls: " + collector.getBallCount(), frameWidth - 210, 150);
    }

    public void keyPressed(int keyCode, char key) {
        if (key == ' ') {
            cannon.startCharging();
        } else if (key == 'a' || key == 'A') {
            cannon.toggleAutoMode();
        }

        if (key == 'g' || key == 'G') {
            gravityEnabled = !gravityEnabled;
        } else if (key == 'd' || key == 'D') {
            debugMode = !debugMode;
        }
    }

    public void keyReleased(char key) {
        if (key == ' ' && cannon.charging) {
            if (!cannon.firedWhileCharging) {
                cannon.fire(balls, cannon.getChargePercentage());
            }
        }
    }
}