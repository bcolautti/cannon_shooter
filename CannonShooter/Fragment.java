import processing.core.PApplet;

class Fragment {
  float x, y, vx, vy;
  float radius;
  public int color; // Color del fragmento.

  Fragment(float x, float y, float vx, float vy, float radius, PApplet parent) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.radius = radius;
    this.color = parent.color(0, 255, 100);;
  }

  void update(float gravity) {
    x += vx;
    y += vy;
    vy += gravity; // Aplicar gravedad
  }

  void display() {
    fill(color);
    noStroke();
    ellipse(x, y, radius * 2, radius * 2);
  }
}