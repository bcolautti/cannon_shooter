import processing.core.PApplet;
import java.util.ArrayList;
import models.Ball;

public class Cannon {
	BallManager ballManager;
    int x, y, frameWidth, frameHeight;
    float angle = -PApplet.PI / 4; // Ángulo inicial en radianes (-45 grados)
    float cannonLength = 100; // Longitud del cañón
    boolean charging = false; // Indicador de carga
    boolean autoMode = false; // Indicador de modo automático
    int chargeStartTime = 0; // Tiempo cuando se comienza a cargar
    float maxChargeTime = 500; // Tiempo máximo de carga en milisegundos (500 ms)
    float autoMoveDirection = 1; // Dirección del movimiento automático (1 o -1)
    int lastAutoMoveTime = 0; // Última vez que el ángulo cambió en modo automático
    int lastAutoShotTime = 0; // Última vez que disparó en modo automático
    int autoShotInterval = 300; // Intervalo entre disparos automáticos (milisegundos)
    boolean firedWhileCharging = false; // Indicador si ya disparó durante la carga
    PApplet parent;

    Cannon(int x, int y, int frameWidth, int frameHeight, PApplet parent, BallManager ballmanager) {
        this.x = x;
        this.y = y;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.parent = parent;
        this.ballManager = ballmanager;
    }

    void update() { 
        if (autoMode) {
            // Movimiento automático del cañón
            if (parent.millis() - lastAutoMoveTime >= 100) {
                angle += autoMoveDirection * PApplet.radians(1); // Cambiar ángulo automáticamente
                lastAutoMoveTime = parent.millis();

                // Límites del ángulo en radianes
                float minAngle = -PApplet.PI / 2; // -90 grados (apunta hacia arriba)
                float maxAngle = 0;             // 0 grados (apunta hacia la derecha)

                // Restringir el ángulo dentro de los límites
                if (angle > maxAngle || angle < minAngle) {
                    autoMoveDirection *= -1; // Invertir dirección si se exceden los límites
                    angle = PApplet.constrain(angle, minAngle, maxAngle);
                }
            }

            // Disparo automático
            if (parent.millis() - lastAutoShotTime >= autoShotInterval) {
                fire(1.0f); // Disparar con carga máxima
                lastAutoShotTime = parent.millis();
            }
        } else {
            // Movimiento manual: apuntar hacia el mouse
            followMouse();

            // Si está cargando, verificar si alcanzó el tiempo máximo de carga
            if (charging) {
                float chargeTime = parent.millis() - chargeStartTime;
                if (chargeTime >= maxChargeTime && !firedWhileCharging) {
                    fire(1.0f); // Disparar automáticamente con carga máxima
                    firedWhileCharging = true; // Evitar múltiples disparos
                }
            }
        }
    }

    void followMouse() {
        float dx = parent.mouseX - x;
        float dy = parent.mouseY - y;
        angle = PApplet.atan2(dy, dx); // Calcular el ángulo hacia el mouse en radianes

        // Límites del ángulo
        float minAngle = -PApplet.PI / 2; // -90 grados
        float maxAngle = 0;             // 0 grados

        // Restringir el ángulo dentro de los límites
        angle = PApplet.constrain(angle, minAngle, maxAngle);
    }

    void display() {
        parent.pushMatrix();
        parent.translate(x, y);
        parent.rotate(angle);
        parent.fill(100); // Color gris del cañón
        parent.rectMode(PApplet.CORNER);
        parent.rect(0, -10, cannonLength, 20); // Dibujar el cañón

        // Dibujar barra de carga si se está cargando
        if (charging) {
            float chargeTime = parent.millis() - chargeStartTime;
            float chargePercentage = PApplet.min(chargeTime / maxChargeTime, 1);
            float loadedLength = cannonLength * chargePercentage; // Largo de la barra roja
            parent.fill(255, 0, 0); // Color rojo
            parent.rect(0, -10, loadedLength, 20);
        }
        parent.popMatrix();

        // Dibujar la base del cañón
        parent.fill(150); // Color gris claro para la base
        parent.ellipse(x, y, 40, 40);
    }

    void fire(float chargePercentage) {
        float initialSpeed = 40 + chargePercentage * 25; // Velocidad inicial basada en la carga
        float vx = PApplet.cos(angle) * initialSpeed; // Componente x de la velocidad
        float vy = PApplet.sin(angle) * initialSpeed; // Componente y de la velocidad
        float startX = x + PApplet.cos(angle) * cannonLength; // Posición inicial x de la bola
        float startY = y + PApplet.sin(angle) * cannonLength; // Posición inicial y de la bola
    //    System.out.println("Creando bola en: x=" + startX + ", y=" + startY + ", vx=" + vx + ", vy=" + vy); // Imprimir valores
        
        ballManager.addBall(new Ball(startX, startY, vx, vy, parent)); // Crear la bola y añadirla a la lista
        
        charging = false; // Finalizar la carga tras disparar
        firedWhileCharging = false; // Reiniciar el indicador de disparo
    }

    void startCharging() {
        if (!charging) {
            charging = true;
            chargeStartTime = parent.millis(); // Registrar el tiempo de inicio de la carga
            firedWhileCharging = false; // Reiniciar el indicador de disparo
        }
    }

    float getChargePercentage() {
        if (charging) {
            float chargeTime = parent.millis() - chargeStartTime;
            return PApplet.min(chargeTime / maxChargeTime, 1); // Devolver el porcentaje de carga (máx 100%)
        } else {
            return 0;
        }
    }

    void toggleAutoMode() {
        autoMode = !autoMode; // Alternar modo automático
        lastAutoMoveTime = parent.millis(); // Reiniciar el temporizador de movimiento
        lastAutoShotTime = parent.millis(); // Permitir disparos inmediatos en el nuevo modo
    }

    // Método para devolver el ángulo actual como texto
    String getAngleInfo() {
        return PApplet.nf(PApplet.degrees(angle), 0, 2) + "°"; // Formatear el ángulo en grados con dos decimales
    }
}
