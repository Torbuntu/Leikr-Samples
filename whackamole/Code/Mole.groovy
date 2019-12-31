class Mole {
    int x;
    int y;
    int state;
    int life;
    boolean alive;

    Mole(int x,int y) {
        this.x = x;
        this.y = y;
        this.state = 0;
        this.alive = true;
    }

    void update() {
        life++;
    }

    void setGone() {
        life = 0
        alive = 0
    }
}
