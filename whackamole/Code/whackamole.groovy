import Mole;

class whackamole extends leikr.Engine {
    def tick = 0;
    def moleTick = 0;
    def hammerState = 0;
    def hammerSwing = false;
    def moles = [];
    def score = 0;

    void create() {
    }

    void update(float delta) {
        //Animate and control moles
        if (tick%5 ==0) {
            for (def i = 0; i<moles.size(); i++) {
                def m = moles[i];
                if (m.state <= 2) m.state++;
                if (hammerSwing && smashes(m)) {
                    moles.remove(i);
                    score++;
                }
            }
        }

        //Control hammer
        if (mouseClick() || hammerSwing) {
            hammerSwing = true;
            if (tick%3 ==0) {
                hammerState++;
            }
            if (hammerState == 4) {
                hammerSwing = false;
                hammerState = 0;
            }
        }

        //Randomly slap a mole down
        if (moleTick == tick) {
            moles.add(new Mole(randInt(0,240-16),randInt(0,160-16)));
            moleTick += randInt(30,100);
        }

        tick++;
    }

    void render() {
        drawString(1,"Score: " + score.toString(),0,0);
        for (m in moles) {
            sprite(m.state,m.x,m.y,1);
        }
        sprite(9+hammerState,mouseX(),mouseY(),1);
    }

    //this is just straight up aabb
    boolean smashes(Mole m) {
        (m.x < mouseX() + 16     &&
            m.x + 16 > mouseX()     &&
            m.y < mouseY() + 16     &&
            m.y + 16 > mouseY())
    }
}

