class FlappyBirb extends leikr.Engine {
	// Changelog:
	// 0.9.1
	//   Fixed scoring issues (score reset per game, high score update)
    //   Refactor for release
	// 0.9.0
	//	 Alpha release

	def birb = [
		x: 66, y:80,
		vy: 0,
		flapping: false,
		sprId: 1,
	]
	def birbStartY = 90
	
	def blocks = []
	def buttonFlag = false
	def flapBoost = 3.2
	def blockGapCounter = 0
	def levelSpeed = 2
	int score = 0
	int highScore = 0
	def readyTimer = 0
	def isReadyPromptVisible = false
	def readyCountdown = 3

	def debug = false            // enable to see debug info
	def isDbgGravityOff = false  // toggleable when debug is true

	def t = 0
	def logoAnimTimer = 120
	def parallaxLayerOffsets = [0f, 0f, 0f]
	def isThemeMusicPlaying = false

    // a FancyString is a String that keeps track of its individual letter
    // ages. works great for fancy text effects, similar to a particle system
    // used for the intro logo
	FancyString logoString1
	FancyString logoString2

	def ver = "0.9.1"

	GameMode state = GameMode.STARTUP
	enum GameMode {
		STARTUP,
		LOGO,
		TITLE,
		READY,
		PLAY,
		DIE,
	}

	def creditStartY = 88

    // map letters to sprite IDs (8x8)
	def letters = [
		f: 72,
		l: 73,
		a: 74,
		p: 75,
		y: 76,
		b: 77,
		i: 78,
		r: 79,
		space: -1,
	]
    // use our letters map to build the title string
	def titleLetters = [
		[y: -16, val: letters.f],
		[y: -16, val: letters.l],
		[y: -16, val: letters.a],
		[y: -16, val: letters.p],
		[y: -16, val: letters.p],
		[y: -16, val: letters.y],
		[y: -16, val: letters.space],
		[y: -16, val: letters.b],
		[y: -16, val: letters.i],
		[y: -16, val: letters.r],
		[y: -16, val: letters.b],
	]

	// levels
	int level = 0
	def levels = [
		// level 0 is title (uses less sprites)
		[
			minGap: 120,
			minHeight: 80,
			speed: 1,
		],
		[
			minGap: 55,
			minHeight: 64,
			speed: 1,
		],
		[
			minGap: 45,
			minHeight: 60,
			speed: 2,
		],
		[
			minGap: 42,
			minHeight: 50,
			speed: 2,
		],
		[
			minGap: 40,
			minHeight: 46,
			speed: 2,
		],
		[
			minGap: 38,
			minHeight: 42,
			speed: 2,
		],
	]

	def init() {
		blockGapCounter = levels[level].minGap
	}
	
    void create(){
		init()
		loadMap("map")
		logoString1 = new FancyString("pixelbath", 108, 72)
		logoString2 = new FancyString("games", 108, 78)
    }

	void generateBlocks() {
		if (blockGapCounter++ > levels[level].minGap+32) {
			if (randFloat(1.0) > 0.8) {
				blockGapCounter = 0
				blocks.add(new Block(levels[level].minHeight))
			}
		}
	}

	void updateGame() {
		// flap!
		if(!buttonFlag && (button("B") || key("Space"))) {
			buttonFlag = true
			birb.flapping = true
        	birb.vy = -flapBoost
			playSound("flap.wav")
        }
		if (debug) {
			if(key("Up")) {
				birb.y--
			}
			if(key("Down")) {
				birb.y++
			}
			if(!buttonFlag && (button("X") || keyPress("X"))) {
				buttonFlag = true
				isDbgGravityOff = !isDbgGravityOff
			}
		}
		if (birb.flapping) {
			// flap harder at first, else glide
			if (birb.vy < -1.5) {
				if (t%3 == 0) birb.sprId++
			} else if (birb.vy < 0) {
				if (t%5 == 0) birb.sprId++
			} else {
				birb.flapping = false
				birb.sprId = 0
			}
		}
        
		generateBlocks()

		// collision checks
		blocks.each{b ->
			b.x -= levels[level].speed
			if (b.x < -32) b.gone = true
			// only vertical collision if we're in birb range horizontally
			if (b.x + 32 > birb.x && b.x < birb.x + 16) {
				if (birb.y < b.y) {
					switchState(GameMode.DIE)
				}
				// 14 because the sprite is only 14px tall
				if (birb.y + 14 >= b.y + b.height) {
					switchState(GameMode.DIE)
				}
			}
			if (!b.passed && b.x + 32 < birb.x) b.passed = true
		}
		blocks.removeAll{it.gone == true}

    	blocks.each{b ->
			if (b.passed && !b.scored) {
				b.scored = true
				if (++score >= highScore) {
					highScore = score
				}
				playSound("chime.wav")

				// level up?
				if (score > 10 && level == 0) level++
				if (score > 25 && level == 1) level++
				if (score > 50 && level == 2) level++
				if (score > 100 && level == 3) level++
				if (score > 150 && level == 4) level++
			}
		}
		// reset flap animation index
    	if(birb.sprId > 1)birb.sprId = 0
	}
	void applyGravity() {
		// update birb's position
		if (!isDbgGravityOff) {
			birb.vy += 0.23
	        birb.y += birb.vy
		}
	}
	void switchState(GameMode newState) {
		if (newState != state) {
			state = newState
			isThemeMusicPlaying = false

			// set up variables for each state transition
            if (newState == GameMode.LOGO) {
				playSound("waves.mp3")
            }
			if (newState == GameMode.TITLE) {
                level = 0
                stopSound()
				isThemeMusicPlaying = true
				playMusic("title-theme.mp3", true)
				logoAnimTimer = 30
			}
			if (newState == GameMode.READY) {
				level = 1
				readyCountdown = 3
				readyTimer = 160
				score = 0
				birb.y = birbStartY
				blocks.clear()
				stopAllMusic()
			}
            // give player a default boost
            if (newState == GameMode.PLAY) {
                birb.vy = -flapBoost
            }
			if (newState == GameMode.DIE) {
				playSound("player-die.wav")
			}
		}
	}
    void update(float delta){
		switch(state) {
			case GameMode.STARTUP:
                switchState(GameMode.LOGO)
				break
			case GameMode.LOGO:
				if (!buttonFlag && (button("B") || keyPress("Space"))) {
					switchState(GameMode.TITLE)
					return
				}
				logoString1.update()
				logoString2.update()
				if (logoAnimTimer-- < -60) {
					switchState(GameMode.TITLE)
				}
				break
			case GameMode.TITLE:
				if (!buttonFlag && (button("B") || keyPress("Space"))) {
					switchState(GameMode.READY)
					return
				}
				blocks.each{b ->
					b.x -= levels[level].speed
					if (b.x < -32) b.gone = true
				}
				generateBlocks()
				blocks.removeAll{it.gone == true}
				updateParallax()
				break
			case GameMode.READY:
				if (readyTimer % 10 == 0) {
					isReadyPromptVisible = !isReadyPromptVisible
				}
				if (readyTimer % 60 == 0) {
					readyCountdown--
				}
				if (readyTimer-- < 0) {
                    switchState(GameMode.PLAY)
				}
				if (!buttonFlag && (button("B") || keyPress("Space"))) {
					buttonFlag = true
                    switchState(GameMode.PLAY)
				}
				updateParallax()
				break
			case GameMode.PLAY:
				applyGravity()
				if (birb.y > 146) {
					switchState(GameMode.DIE)
				}
				updateParallax()
				updateGame()
				break
			case GameMode.DIE:
				applyGravity()
				if (!buttonFlag && (button("B") || keyPress("Space"))) {
					switchState(GameMode.READY)
					return
				}
				if (!buttonFlag && (button("A") || keyPress("Z"))) {
					switchState(GameMode.TITLE)
					return
				}
				break
		}
    	t++
		if (!button("B") && !key("Space")) {
			buttonFlag = false
		}
    }
	
    // updates playfield parallax
    void updateParallax() {
		parallaxLayerOffsets[0] += 0.2
		if (parallaxLayerOffsets[0] > 80) parallaxLayerOffsets[0] = 0
		parallaxLayerOffsets[1] += 0.5
		if (parallaxLayerOffsets[1] > 64) parallaxLayerOffsets[1] = 0
		parallaxLayerOffsets[2] += 0.7
		if (parallaxLayerOffsets[2] > 32) parallaxLayerOffsets[2] = 0
	}

    void render(){
		switch(state) {
			case GameMode.STARTUP:
				bgColor(0)
				break
			case GameMode.LOGO:
				bgColor(0)
				def amp = (logoAnimTimer / 120) * 2
				if (amp < 0.1) amp = 0
				def spriteYOffset = ((logoAnimTimer-60) / 60) * 16
				if (spriteYOffset < 0) spriteYOffset = 0
				sprite(30, 90, 70+spriteYOffset, 1)
				for(x in 0..16) {
					for(y in 0..8) {
						def factor = cos(x+t/3)*amp - 1
						def xPos = (sin(y+t/6)*amp)+x+90 + factor
						def yPos = 102 - y
						def clr = getPixel(x+89, y*2+70)
						drawPixel(6, x+90, y+86)
						drawPixel(6, x+90, y+94)
						drawPixel(clr, xPos, yPos-8)
					}
				}
				logoString1.chars.each{c ->
					drawString("${99*c.getLife()},${155*c.getLife()},${255*c.getLife()}", c.character, c.x, c.y)
					// drawString(1, c.character, c.x + 30, c.y)
				}
				logoString2.chars.each{c ->
					drawString("${99*c.getLife()},${155*c.getLife()},${255*c.getLife()}", c.character, c.x, c.y)
				}
				for(x in 0..36) {
					for(y in 0..8) {
						def factor = cos(x+t/3)*amp - 1
						def xPos = (sin(y+t/6)*amp)+x+110 + factor
						def yPos = 102 - y
						def clr = getPixel(x+109, y*2+71)
						drawPixel(clr, xPos, yPos-9)
					}
				}
				if (debug) {
					drawString(1, "Logo timer: $logoAnimTimer", 2, 2)
				}
				break
			case GameMode.TITLE:
				if (logoAnimTimer > 0) {
					def pct = (31-logoAnimTimer)/30
					bgColor("${135*pct},${206*pct},${235*pct}")//135,206,235
					logoAnimTimer--
					return
				}
				drawPlayfield()
				drawTitle(true)

				// prompt
				drawString(1, "Press    to start", 85, 80)
				sprite(25, 108, 78, 1)

				// Leikr logo
				sprite(16, 91, creditStartY, 1)
				sprite(29, 107, creditStartY+8, 0)
				sprite(30, 115, creditStartY+8, 0)
				sprite(31, 123, creditStartY+8, 0)
				drawString(1, "A           Game", 86, creditStartY+10)
				drawString(1, "v${ver}", 212, 152)
				break
			case GameMode.READY:
				drawPlayfield()
				drawTitle(false)
				drawScore()

				// draw ready prompt
				for (int i in 0..3) {
					sprite(12+i, 50+i*16, 60, 1)
				}
				def readyY = 63
				birb.sprId = 0
				if (isReadyPromptVisible) {
					readyY = 64
					birb.sprId = 1
				}
				sprite(25, 68, readyY, 1)
				if (readyCountdown > 0) drawString(1, "$readyCountdown...", 71, 76)
				break
			case GameMode.PLAY:
				drawPlayfield()
				drawScore()
				break
			case GameMode.DIE:
				drawPlayfield()
				drawMap()
				for(i in 0..3) {
					sprite(26+i, 92+i*16, 40, 1)
				}
				drawString(1, "SCORE: $score", 74, 56)
				drawString(1, "HIGH: $highScore", 130, 56)

				String msg = ""
				if (score == 0) msg = "Try flapping!"
				if (score > 0 && highScore == score) {
					drawString((t%20>10) ? 1 : 20, "New high score!", 74, 64)
				} else {
					drawString(1, msg, 74, 64)
				}
				drawString(1, "Press    to restart", 76, 76)
				sprite(25, 99, 74, 1)
				drawString(1, "Press    to exit", 76, 86)
				sprite(24, 99, 84, 1)
				break
		}
		if (debug) {
			drawString(1, "Blocks: ${blocks.size()}", 2, 8)
			drawString(1, "State: $state", 2, 14)
			drawString(1, "Level: $level", 2, 20)
			drawString(1, "    minGap: ${levels[level].minGap}", 2, 26)
			drawString(1, "    minHeight: ${levels[level].minHeight}", 2, 32)
			drawString(1, "    speed: ${levels[level].speed}", 2, 38)
			drawString(1, "Debug flags:", 150, 2)
			drawString(1, "Grav off: ${isDbgGravityOff}", 154, 8)
			drawString(8, "DEBUG MODE", 2, 154)
		}
    }

    // handle title theme playback
    void onPause() {
		pauseAudio()
	}
	void onResume() {
		if (isThemeMusicPlaying) resumeAudio()
	}

	void drawTitle(drawSin) {
		def xSub = 0
		def yBase = 50
		def yTarget = 0
		// draw the wavy title
		for (int i in 0..titleLetters.size()-1) {
			if (titleLetters[i].val == -1) continue
			// step through letters progressively after a bit
			if (i * 10 > t) continue
			def xPos = 80 + i*7

			// by setting a target first, we can ease toward that point smoothly
			if (drawSin) yTarget = yBase + sin((i+t/3)/3) * 4
			else yTarget = -100
			titleLetters[i].y += (yTarget - titleLetters[i].y) / 10

			// since "I" is smaller width, it gets special treatment
			// and all subsequent letters must shift left
			if (i > 0 && titleLetters[i-1].val == 78) xSub += 3

			// draw two 8x8 tiles for each letter
			sprite(titleLetters[i].val, xPos - xSub, titleLetters[i].y, 0)
			sprite(titleLetters[i].val+12, xPos - xSub, titleLetters[i].y+8, 0)
		}
	}
    // draw both scores twice to fake a drop shadow
	void drawScore() {
		drawString(5, "Score: $score", 4, 5)
		drawString(1, "Score: $score", 4, 4)
		drawString(5, "High Score: $highScore", 52, 5)
		drawString(1, "High Score: $highScore", 52, 4)
	}

    // each layer's x position is controlled by the parallax offsets except blocks and player
	void drawPlayfield() {
    	bgColor(20) // sky blue

		// clouds layer
		for(i in 0..20) {
			sprite(31+(i%5), (-parallaxLayerOffsets[0] + i*16).intValue(), 110, 1)
		}
		fillRect(1, 0, 126, 240, 36)

		// buildings
		for(i in 0..18) {
			sprite(36+(i%4), (-parallaxLayerOffsets[1] + i*16).intValue(), 134, 1)
		}

		// tree layer
		for(i in 0..8) {
			sprite(40, (-parallaxLayerOffsets[2] + i*32).intValue(), 136, 1)
			sprite(41, (-parallaxLayerOffsets[2] + i*32+16).intValue(), 136, 1)
		}
		fillRect("106,190,48", 0, 150, 240, 8)

		// draw the pipes
		blocks.each{b ->
			drawPipe(b.x, b.y, b.y + b.height)
		}

		// foreground
		drawLineSegment("106,190,48", 0, 159, 240, 159)
		drawLineSegment("55,148,110", 0, 158, 240, 158)

		// draw player if we're actually playing
		if (state == GameMode.PLAY || state == GameMode.READY || state == GameMode.DIE) {
			sprite(birb.sprId, birb.x, birb.y, 0, 1)
		}
	}

    // draws a "block" as two top-bottom pipes
	void drawPipe(x, gapStartY, gapEndY) {
		def blocksTop = Math.ceil(gapStartY / 16)
		def blocksBottom = Math.ceil((160 - gapEndY) / 16)
		
		// top pipe
		for(int y=(blocksTop * 16)-gapStartY-16; y<gapStartY-16; y+=16) {
			sprite(10, x, y, false, true, 1)
			sprite(11, x+16, y, false, true, 1)
		}
		sprite(4, x, gapStartY-16, false, false, 1)
		sprite(5, x+16, gapStartY-16, false, false, 1)

		// bottom pipe
		sprite(2, x, gapEndY, false, false, 1)
		sprite(3, x+16, gapEndY, false, false, 1)
		for(int y=gapEndY+16; y<blocksBottom*16+gapEndY; y+=16) {
			sprite(10, x, y, false, false, 1)
			sprite(11, x+16, y, false, false, 1)
		}
	}
}

// keep track of the "blocks" (pipes)
class Block {
	def x = 240
	def y = 0
	def height = 50
	def gone = false
	def scored = false, passed = false

	Block(int gapSize) {
		y = Math.random()*(128-gapSize)+16
		height = gapSize
	}
}

// used for logo intro
class FancyString {
	def chars = []

	FancyString(String text, int xPos, int yPos) {
		int i = 0
		for (String c in text) {
			chars.add(new Letter(c, i, xPos + i*4, yPos))
			i++
		}
	}

	void update() {
		chars.each{
			it.update()
		}
	}
}
class Letter {
	int index = 0
	def x = 0
	def y = 0
	def character = ""
	int life = 0
	int maxLife = (Math.random() * 60).intValue() + 5	// do not let this be zero or it'll error
	int startLife = (Math.random() * 15).intValue() + 1

	Letter(String letter, int idx, float xPos, float yPos) {
		character = letter
		index = idx
		x = xPos
		y = yPos
		if (startLife > maxLife) maxLife = startLife + 5
	}

	void update() {
		life += (life < maxLife) ? 1 : 0
	}

	float getLife() {
		if (life < startLife) {
			return 0
		}
		if (life >= maxLife) {
			return 1
		}
		return (life-startLife)/maxLife
	}
}