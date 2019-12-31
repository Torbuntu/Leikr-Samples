import Mole;

class whackamole extends leikr.Engine {
    def tick = 0;
    def moleTick = 0;
    def hammerState = 0;
    def hammerSwing = false;
    def moles = [];
    def score = 0;

    boolean debug = false;

    void create() {
    }

    void update(float delta) {
        //Animate and control moles
        if (tick%5 ==0) {
            for (def i = 0; i<moles.size(); i++) {
                def m = moles[i];
                if (m.state <= 2) m.state++;
                if (m.alive && hammerSwing && smashes(m) && hammerState > 2) {
                    m.setGone()
                    score++;
                    playSound("jump.wav")
                }

                // randomly pick ones to go away
                if (m.alive && m.life > 30 && randInt(50) > 49) {
                    m.setGone()
                }

                // dead moles slowly vanish
                if (!m.alive) {
                    if (m.life < 60) m.state = 4
                    if (m.life > 60) m.state = 5
                    if (m.life > 100) moles.remove(i)
                }
                m.update()
            }
        }

        //Control hammer
        if (mouseClick() && !hammerSwing) {
            hammerSwing = true;
        }

        if (hammerSwing) {
            if (tick%3 ==0) {
                hammerState++;
            }
            if (hammerState == 4) {
                hammerSwing = false;
                hammerState = 0
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
        for (m in moles) {
            sprite(m.state, m.x, m.y, 1);
            if (debug) {
                drawString(8, m.state.toString(), m.x-2, m.y+16)
                drawString(1, m.life.toString(), m.x+2, m.y+16)

                if (m.alive) drawRect(9, m.x - 14, m.y - 6, 16, 10)
            }
        }
        if (debug) {
            drawPixel(9, mouseX(), mouseY())
            drawString(1, moles.size().toString(), 2, 152)
        }
        sprite(8+hammerState,mouseX(),mouseY(),1);

        drawString(1,"Score: $score", 2, 2);
    }

    //this is just straight up aabb
    boolean smashes(Mole m) {
        (mouseX() > m.x - 14 &&
         mouseX() < m.x + 2 &&
         mouseY() > m.y - 6 &&
         mouseY() < m.y + 4)
    }
}

