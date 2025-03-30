class Fragment {
  float x, y, vx, vy;
  float radius;
  color c;

  Fragment(float x, float y, float vx, float vy, float radius, color c) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.radius = radius;
    this.c = c;
  }

  void update(float gravity) {
    x += vx;
    y += vy;
    vy += gravity; // Aplicar gravedad
  }

  void display() {
    fill(c);
    noStroke();
    ellipse(x, y, radius * 2, radius * 2);
  }
}
