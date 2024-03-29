import Supers
import SaveUtil

import Food

class FoodChain extends leikr.Engine {

	public enum GameState {
		TITLE,
		INSTRUCTIONS,
		GAME_PLAY,
		GAME_OVER,
		ACHEIVMENTS
	}
	
	def acheivments = [
		aDairyQueen: false,
		aMeatPump: false, 
		aGrassGreener: false,
		aAllOrangeJuice: false,
		aFive: false,
		aTen: false,
		aFifteen: false,
		aTwenty: false,
		aBombBastic: false,
		aYeOleSwitcheroo: false
	]

    GameState state = GameState.TITLE//0=title, 1=instructions, 2=Game play, 3=gameover, 4=Acheivments
    int bSpeed = 0, btnSpeed = 0, dropSpeed = 0, blink = 0, flipNext = 0

    boolean page = false//used for getting to page 2 of isntructions
    //play variables

    boolean select = false, usingSwap = false
    int hungerSpeed = 0//The speed of hunger will increase with each level

    int megaScore = 10 // used for advancing to next level, 128 = max
    int level = 1 //might settle on 4 levels
    int lives = 3
    int available = 1//a meter of available moves
    int row=8, col=8
    def jar = new Food[col][row]
    int fruits= 0, veggies = 0, meats = 0, drinks = 0
    boolean fSuper = false, vSuper = false, mSuper = false, dSuper = false

    Supers supers = new Supers()
    SaveUtil saveUtil
    int	high_score

    int bombs = 1, swaps = 1, bombDur = 0, bombX, bombY, swapX, swapY, goX, goY

    def goWave = [6,4,2,0,0,2,4,6]

    int shake = 0, bgX = 0, bgY = 0
    

    int airEaten = 0
    int newAcheivmentIcon = 0, achSpeed = 100

    //cursor x, y and flash
    int cx=0, cy=0, cf = 0

    int starving = 200
    //END play variables

    def init(){
        airEaten = 0
        high_score = saveUtil.loadScore()
        acheivments = saveUtil.loadAcheivments()

        shake = 0
        bgX = 0
        bgY = 0

        starving = 200
        newAcheivmentIcon = 0
        achSpeed = 100
        state = GameState.TITLE//0=title, 1=instructions, 2=Game play, 3=gameover, 4 = achievments
        bSpeed = 0
        dropSpeed = 0
        hungerSpeed = 0
        page = false
        //title variables
        blink = 0
        //end title variables

        //play variables
        flipNext = 0
        select = false
        megaScore = 10 // used for advancing to next level, 128 = max
        level = 1
        lives = 3
        available = 1

        jar = new Food[col][row]
        fruits= 0
        veggies = 0
        meats = 0
        drinks = 0
        fSuper = false
        vSuper = false
        mSuper = false
        dSuper = false
        Supers supers = new Supers()
        bombs = 1
        swaps = 1

        //cursor x, y and flash
        cx=0
        cy=0
        cf=0
        //END play variables
    }

    void create(){
        loadImages()
        saveUtil = new SaveUtil(lData)
    	high_score  = saveUtil.loadScore()
    	acheivments = saveUtil.loadAcheivments()
    }

    //START UPDATE
    void update(float delta){
    	bSpeed++
    	dropSpeed++
        switch(state){
        case GameState.TITLE:
            playMusic("title.wav", true)
            if(keyPress("Space") || bp("SELECT")) {
                playSound("start.wav")
                state = GameState.INSTRUCTIONS
                col.times{i->
                    row.times{j->
                        jar[i][j] = new Food(randInt(7), randInt(100))
                    }
                }
            }
            if(blink > 20) blink = 0
            blink++
            break;
        case GameState.INSTRUCTIONS://Instructions
            if(blink > 20) blink = 0
            blink++
            if(keyPress("Space") || bp("SELECT") ){
                playSound("start.wav")
                stopMusic()
                state = GameState.GAME_PLAY
            }

            if(keyPress("Left") || bp("LEFT")){
                playSound("shift.wav")
                page = false
            }

            if(keyPress("Right") || bp("RIGHT") ){
                playSound("shift.wav")
                page = true
            }

            break;
        case GameState.GAME_PLAY://Game play
            if(flipNext > 20) flipNext = 0
            if(cf > 20) cf = 0
            cf++
            flipNext++

            //hunger per level
            switch(level){
            case 1:
                if(hungerSpeed > 100){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 2:
                if(hungerSpeed > 80){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 3:
                if(hungerSpeed > 60){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 4:
                if(hungerSpeed > 40){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 5:
                if(hungerSpeed > 30){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                if(hungerSpeed > 20){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                if(hungerSpeed > 15){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            default:
                if(hungerSpeed > 10){
                    megaScore--
                    hungerSpeed=0
                }
                break;
            }
            if(megaScore <= 0) megaScore = 0
            hungerSpeed++


            //CHECL AVAILABLE MOVES
            if(!movesExist()){
                if(lives > 0){
                    playSound("noMove.wav")
                    shake = 15
                    lives--
                    resetBoard()
                    megaScore = megaScore - 46
                }else{
                    state= GameState.GAME_OVER//game over
                }
            }
            if(available > 32) available = 32

            //START STATE 1 INPUT
            handleInput()

            if(bombDur > 0){
                bombDur--
            }

            //START STATE 1 UPDATE DROP
            updateFill()

            //CHECK LVL UP
            if(megaScore >= 128){
                megaScore = 0
                level++
                high_score = level
                saveUtil.saveScore(high_score)
                lives++
                playSound("lifeUp.wav")
                if(lives>9) lives = 10
            }
            if(megaScore == 0){
                starving--
                if(starving == 0){
                    starving = 200
                    lives--
                    playSound("noMove.wav")
                    shake = 15
                }
            }
            if(megaScore > 0){
                starving = 200
            }

            if(lives == 0){
                state = GameState.GAME_OVER
                saveUtil.saveScore(high_score)
                saveUtil.saveAcheivments(acheivments)
            }
            //CHECK ACHEIVMENTS
            checkAcheivments()
            if(newAcheivmentIcon > 0) newAcheivmentIcon--


            break;
        case GameState.GAME_OVER://Game over
            if(keyPress("Space") ){
                init()
                playSound("shift.wav")
            }
            if(blink > 20) blink = 0
            blink++
            break;
        case GameState.ACHEIVMENTS: //Acheivments
            if(keyPress("Space") ){
                state = GameState.GAME_PLAY
                playSound("shift.wav")
            }

            break;
        }
    }
    //END UPDATE

    //START RENDER
    void render(){
			5.times{it ->
				
			}
        switch(state){
        case GameState.TITLE:
            drawTexture("title.png",0,0)
            if(blink > 10){
                drawString(32, "Press Space/Select", 58, 124, 120, 1)
            }else{
                drawString(1, "Press Space/Select", 58, 124, 120, 1)
            }
            drawString(32, "High Score: $high_score", 58, 108, 120, 1)
            drawString(1, "High Score: $high_score", 59, 109, 120, 1)


            break;
        case GameState.INSTRUCTIONS:
            drawInstructions(page)
            break;
        case GameState.GAME_PLAY://game play
            //background image. Obvi
            if(shake>0){
                bgX = randInt(-2, 2)
                shake--
            }else{
                bgX = 0
            }
            drawTexture("bg.png",bgX,bgY)

            //Draws the items in the jar
            col.times{i->
                row.times{j->
                    if(i ==cx && j==cy){
                        if(cf > 10){
                            jar[i][j].draw(lGraphics,(96+ i*16),(16+j*16), true)
                        }else{
                            jar[i][j].draw(lGraphics,(96+ i*16),(16+j*16), false)
                        }
                    }else{
                        jar[i][j].draw(lGraphics,(96+ i*16),(16+j*16))
                    }
                }
            }

            //draw cursor
            drawCursor()

            //Draw health
            lives.times{
                sprite(80, 8, (8*it+8) )
            }

            //Draw container scores
            drawScores()

            //Draw megaScore
            fillRect(8, 96, 7, megaScore, 4)

            //draw available
            if(available > 15) fillRect(20, 232, 144, 2, -available*4)
            else fillRect(8, 232, 144, 2, -available*4)


            if(available <= 4){
                sprite(81, 229, 148)
            }

            if(megaScore == 0 && cf > 10){
                sprite(81, 90, 5)
            }

            //Draw bombs and swaps
            bombs.times{
                if(it > 4){
                    sprite(82, 80, (8*it-32))
                }else{
                    sprite(82, 70, (8*it+8))
                }
            }
            swaps.times{
                if(it > 4){
                    sprite(83, 80, (8*it+31))
                }else{
                    sprite(83, 70, (8*it+71))
                }
            }

            if(bombDur > 0){
                sprite(2, (96+bombX*16), (16+bombY*16), 3)
            }

            if(newAcheivmentIcon > 0){
                if(cf>10)
                spriteSc(69, 98, 150, 0.0f)
                else
                spriteSc(69, 98, 150, 0.5f)
            }

            //temp
            drawString(18, "Level: $level", 8, 150)

            break;
        case GameState.GAME_OVER:// Gameover condition
	        bgColor(0)
            8.times{
                sprite(it, 56+it*16, 64+goWave[it], 1)
            }
            drawString(32, "GAME OVER", 0, 32, 240, 1)

            fillRect(50, 56, 122, 124, 60)
            if(blink > 10){
                drawString(32, "Press Space/Select", 58, 124, 120, 1)
            }else{
                drawString(1, "Press Space/Select", 58, 124, 120, 1)
            }
            drawString(32, "High Score: $high_score", 58, 108, 120, 1)
            drawString(1, "High Score: $high_score", 59, 109, 120, 1)

            break;

        case GameState.ACHEIVMENTS: //Acheivments
            drawTexture("awards.png", 0,0)
            drawString(1, "Acheivments", 0, 16, 240, 1)
            drawAcheivments()
            if(airEaten>0) drawString(32, "Air eaten: $airEaten", 8, 130, 240, 0)


            break;
        }
    }

    //END RENDER



    def movesExist(){
    	def match = 0
    	col.times{i->
            row.times{j->
                int tp = jar[i][j].type
                if((i > 0 && jar[i-1][j].type != tp || i == 0)
                    && (i < col-1 && jar[i+1][j].type != tp || i == 7)
                    && (j > 0 && jar[i][j-1].type != tp || j == 0)
                    && (j< row-1 && jar[i][j+1].type != tp || j == 7) ) {
                    //no matches yet
                }else{
                    match++
                }
            }
        }
        available = match
        boolean swpBmbAvl = false
        if(match==0){
            jar.each{
                it.each{ p->
                    if(p.type == 10 || p.type == 11)swpBmbAvl = true
                }
            }
        }
        if(match == 0 && !mSuper && !vSuper && !fSuper && !dSuper && bombs == 0 && swaps == 0 && !swpBmbAvl) return false//No moves
        return true
    }


    def checkMatches(x,y){
    	def tp = jar[x][y].type//temp type
    	if(tp==8)airEaten++
        if(tp == 10){
            bombs++
            if(bombs>10)bombs=10
            jar[x][y].type = 8
            playSound("item.wav")
            return
        }
        if(tp == 11){
            swaps++
            if(swaps>10)swaps=10
            jar[x][y].type = 8
            playSound("item.wav")
            return
        }
        //If no matches on any sides in middle.
        if(	(x > 0 && jar[x-1][y].type != tp || x == 0)
            && (x < col-1 && jar[x+1][y].type != tp || x == 7)
            && (y > 0 && jar[x][y-1].type != tp || y == 0)
            && (y < row-1 && jar[x][y+1].type != tp || y == 7) ) {
            playSound("select.wav")
            return
        }


    	jar[x][y].type = 8
    	playSound("eat.wav")
    	int score = matchesScoring(tp, x, y)

    	foodTypeScoreCheck(tp, score)
    }

    def checkMatchesExplode(x,y){
    	def tp = jar[x][y].type//temp type
    	if(tp == 10){
            bombs++
            if(bombs>10)bombs=10
            jar[x][y].type = 8
            return
        }
        if(tp == 11){
            swaps++
            if(swaps>10)swaps=10
            jar[x][y].type = 8
            return
        }
        //If no matches on any sides in middle.
        if(	(x > 0 && jar[x-1][y].type != tp || x == 0)
            && (x < col-1 && jar[x+1][y].type != tp || x == 7)
            && (y > 0 && jar[x][y-1].type != tp || y == 0)
            && (y < row-1 && jar[x][y+1].type != tp || y == 7) ) {
            jar[x][y].type = 8
            foodTypeScoreCheck(tp, 2)
            return
        }

    	jar[x][y].type = 8

        int score = matchesScoring(tp, x, y)
        foodTypeScoreCheck(tp, score)
    }
    
    def matchesScoring(tp, x, y){
    	int score = -3//sets the score correctly for the i variable offset

    	//Scan the x axis (horiz)
    	int i = 1
    	while(x-i >= 0){
            if(jar[x-i][y].type == tp){
                jar[x-i][y].type = 8
            } else{
                break
            }
            i++
    	}
    	score += i
    	i = 1
    	while(x+i < col){
            if(jar[x+i][y].type == tp) {
                jar[x+i][y].type = 8
            }else{
                break
            }
            i++
    	}
    	score += i
    	//Scan the y axis (verti)
    	i = 1
    	while(y-i >= 0){
            if(jar[x][y-i].type == tp){
                jar[x][y-i].type = 8
            } else{
                break
            }
            i++
    	}
    	score += i
    	i = 1
    	while(y+i < row){
            if(jar[x][y+i].type == tp) {
                jar[x][y+i].type = 8
            }else{
                break
            }
            i++
    	}

    	//UPDATE SCORE AND MEGASCORE APPLYING MULTIPLYER
    	score += i
    	if(score < 3)  megaScore += score
    	if(score == 3) megaScore += score * 3
    	if(score == 4) megaScore += score * 3
    	if(score == 5) megaScore += score * 5
    	if(score == 6) megaScore += score * 6
    	if(score > 6)  megaScore += score * 10

    	return score
    }

    def foodTypeScoreCheck(t, score){
    	//if drink type
    	if(t == 0 || t == 1) {
            drinks += score
            if(drinks >= 41){
                drinks = 41
                dSuper = true
            }
    	}
    	//if veggie type
    	if(t == 2 || t == 3) {
            veggies += score
            if(veggies >= 41) {
                veggies = 41
                vSuper = true
            }
    	}
    	//if fruit type
    	if(t == 4 || t == 5) {
            fruits += score
            if(fruits >= 41) {
                fruits = 41
                fSuper = true
            }
    	}
    	//if meat type
    	if(t == 6 || t == 7) {
            meats += score
            if(meats >= 41) {
                meats = 41
                mSuper = true
            }
    	}
    }

    //RESET BOARD
    def resetBoard(){
        col.times{i->
            row.times{j->
                jar[i][j] = new Food(randInt(7), randInt(100))
            }
        }
    }
    //END RESET BOARD

    //BOMB POWER
    def explode(x,y){
    	bombDur = 10
    	shake = 10

    	bombX = x-1
    	bombY = y-1

    	int a = x-1
    	int b = y-1

    	if(a<0)a=0
    	if(b<0)b=0

    	3.times{
            3.times{
                if(a>7)a=7
                if(b>7)b=7
                checkMatchesExplode(a, b)
                a++
            }
            a-=3
            b++
    	}
    }
    //END BOMB POWER

    //SWAP POWER
    def swapStart(x,y){
    	swapX = x
    	swapY = y
    	usingSwap = true
    }
    def doSwap(x,y){
    	def temp = jar[swapX][swapY].type
    	jar[swapX][swapY].type = jar[x][y].type
    	jar[x][y].type = temp
    	usingSwap = false
    	swaps--
    	playSound("swap.wav")
    }
    //END SWAP POWER

    //STATE 1 INPUT
    def handleInput(){
    	//Debug
        if(keyPress("C")){
            checkAvailableMoves()
        }
        //End debug

        //ENTER ACHEIVMENT PAGE
        if(keyPress("Space") ) {
            state = GameState.ACHEIVMENTS
            playSound("shift.wav")
        }

        if(usingSwap){
            moveCursor()
            if(keyPress("W")){
                doSwap(cx, cy)
            }
            return
        }

        //Check for Super usage input
        if(mSuper && (keyPress("X") || bp("X") )){
            playSound("feast.wav")
            mSuper = false
            megaScore += supers.useSuper(jar, 6, 7)
            meats = 0
        }
        if(vSuper && (keyPress("Y") || bp("Y"))){
            playSound("feast.wav")
            vSuper = false
            megaScore += supers.useSuper(jar, 2, 3)
            veggies = 0
        }
        if(fSuper && (keyPress("Z") || bp("X") )){
            playSound("feast.wav")
            fSuper = false
            megaScore += supers.useSuper(jar, 4, 5)
            fruits = 0
        }
        if(dSuper && (keyPress("B") || bp("B") )){
            playSound("feast.wav")
            dSuper = false
            megaScore += supers.useSuper(jar, 0, 1)
            drinks = 0
        }
        //Bombs or Swaps
        if( (keyPress("Q") || (bp("LEFT_BUMPER"))) && bombs > 0){
            playSound("bomb.wav")
            bombs--
            explode(cx, cy)
        }
        if( (keyPress("W") && swaps > 0)){
            swapStart(cx, cy)
            return
        }

        //Cursor movement
        if(!select){
            moveCursor()
            if((keyPress("A")|| bp("A"))) {
                select = true
                dropSpeed = 0
            }
        }else{
            checkMatches(cx, cy)

            select = false
        }
    }
    //END STATE 1 INPUT


    //MOVEMENT
    def moveCursor(){
    	if(keyPress("Right") && cx < 7) {
            cx ++
        }
        if(keyPress("Left") && cx > 0) {
            cx --
        }
        if(keyPress("Up") && cy > 0) {
            cy--
        }
        if(keyPress("Down") && cy < 7){
            cy++
        }
    }
    //END MOVEMENT

    //STATE 1 UPDATE DROP
    //updateDrop checks every position to see if an item can be dropped a row.
    def updateFill(){
    	if(dropSpeed > 6){
            dropSpeed = 0
            col.times{i->
                if(jar[i][0].type==8) jar[i][0] = new Food(randInt(7), randInt(100))
                row.times{j->
                    if(j==row)return
                    if(j+1 < row && jar[i][j].type!=8 && jar[i][j+1].type==8){
                        def tmp = jar[i][j]
                        jar[i][j] = jar[i][j+1]
                        jar[i][j+1] = tmp
                    }
                }
            }
        }
    }
    //END STATE 1 UPDATE DROP

    //Draw cursor
    def drawCursor(){
    	if(usingSwap){
            drawRect(9, (96+cx*16), (16+cy*16), 16, 16)
            drawRect(20, (96+swapX*16), (16+swapY*16), 16, 16)
            //Draw line between the swapping tiles
            drawLineSegment(22, (96+swapX*16)+8, (16+swapY*16)+8, (96+cx*16)+8, (16+cy*16)+8)
            return
    	}
    	if(cf>10){
            drawRect(21, (96+cx*16), (16+cy*16), 16, 16)
            drawFeastAvailable(true)
        }else{
            drawRect(24, (96+cx*16), (16+cy*16), 16, 16)
            drawFeastAvailable(false)
        }
    }

    def drawFeastAvailable(boolean a){
    	if(a){
            if(meats == 41)
            spriteSc(65, 29, 55, 1)
            if(veggies == 41)
            spriteSc(66, 53, 55, 1)
            if(fruits == 41)
            spriteSc(67, 29, 120, 1)
            if(drinks == 41)
            spriteSc(68, 53, 120, 1)
        }else{
            if(meats == 41)
            spriteSc(65, 29, 55, 0.5f )
            if(veggies == 41)
            spriteSc(66, 53, 55, 0.5f)
            if(fruits == 41)
            spriteSc(67, 29, 120, 0.5f)
            if(drinks == 41)
            spriteSc(68, 53, 120, 0.5f)
        }
    }

    //Draw the container scores:
    def drawScores(){
        fillRect(8, 24, 48, 16, -meats)//meats
        fillRect(26, 48, 48, 16, -veggies)//veggies
        fillRect(15, 24, 112, 16, -fruits)//fruits
        fillRect(1, 48, 112, 16, -drinks)//drinks
    }

    def drawInstructions(page){
    	bgColor(5)
    	if(page){
            drawString(32, "How to Play. Pg 2", 0, 0, 240, 1)
            sprite(34,0,0 )

            sprite(10, 8, 16, 1)
            drawString(1, "Collect bombs to blast away 3x3 squares with chain reaction explosions! Use `Q` or Left Bumper.", 32, 16, 200)

            sprite(11, 8, 60, 1)
            drawString(1, "Collect swap tiles to be able to swap any two tiles on the board! Use `W` or Right Bumper.", 32, 60, 200)

            drawString(1, "Find the biggest combos to earn the most points to fill your hunger meter and progress to the next level.", 0, 100, 240)
    	}else{
            drawTexture("instruct.png", 0,0)

            drawString(32, "How to Play. Pg 1", 0, 0, 240, 1)

            drawString(1, "Fill these to activate a Feast!\nEach container holds a food type: Drinks, Veggies, Fruits or Meats.", 38,18, 200)

            //Draw types
            8.times{
                sprite(it, 14 + (it*18), 62, 1)
            }

            //Draw arrows
            2.times{
                sprite(32+it, 14+(it*9), 80)
            }
            2.times{
                sprite(34+it, 14+(it*9), 88)
            }
            drawString(1, "Move the cursor with Arrow Keys/D-pad\n`A` to make selection.",38,80, 200)

            //Hearts
            sprite(80, 8, 108)
            drawString(1, "Hearts are used up when you run out of moves. Each level earns an extra life!", 16, 108, 200)
            sprite(33, 232, 0)
    	}

        if(blink > 10){
            drawString(32, "Press Space/Select", 58, 142, 120, 1)
        }else{
            drawString(1, "Press Space/Select", 58, 142, 120, 1)
        }
    }

    def checkAcheivments(){
    	if(level >= 5 && !acheivments["aFive"]) {
            playSound("award.wav")
            acheivments["aFive"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(level >= 10 && !acheivments["aTen"]) {
            playSound("award.wav")
            acheivments["aTen"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(level >= 15 && !acheivments["aFifteen"]){
            playSound("award.wav")
            acheivments["aFifteen"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(level >= 20 && !acheivments["aTwenty"]){
            playSound("award.wav")
            acheivments["aTwenty"] = true
            newAcheivmentIcon = achSpeed
    	}

    	if(bombs == 10 && !acheivments["aBombBastic"]){
            playSound("award.wav")
            acheivments["aBombBastic"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(swaps == 10 && !acheivments["aYeOleSwitcheroo"]){
            playSound("award.wav")
            acheivments["aYeOleSwitcheroo"] = true
            newAcheivmentIcon = achSpeed
    	}

    	if(meats >= 40 && !acheivments["aMeatPump"]) {
            playSound("award.wav")
            acheivments["aMeatPump"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(veggies >= 40 && !acheivments["aGrassGreener"]) {
            playSound("award.wav")
            acheivments["aGrassGreener"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(fruits >= 40 && !acheivments["aAllOrangeJuice"]) {
            playSound("award.wav")
            acheivments["aAllOrangeJuice"] = true
            newAcheivmentIcon = achSpeed
    	}
    	if(drinks >= 40 && !acheivments["aDairyQueen"]) {
            playSound("award.wav")
            newAcheivmentIcon = achSpeed
            acheivments["aDairyQueen"] = true
    	}
    }

    def drawAcheivments(){
        if(acheivments["aFive"]) {sprite(19, 16, 40, 1)}else{sprite(23, 16, 40, 1)}
        if(acheivments["aTen"]) {sprite(20, 64, 40, 1)}else{sprite(23, 64, 40, 1)}
        if(acheivments["aFifteen"]) {sprite(21, 112, 40, 1)}else{sprite(23, 112, 40, 1)}
        if(acheivments["aTwenty"]) {sprite(22, 160, 40, 1)}else{sprite(23, 160, 40, 1)}

        if(acheivments["aBombBastic"]) {sprite(10, 16, 104,1)}else{sprite(23, 16, 104, 1)}
        if(acheivments["aYeOleSwitcheroo"]){ sprite(11, 64, 104,1)}else{sprite(23, 64, 104, 1)}
        if(acheivments["aMeatPump"]) {sprite(13, 112, 104,1)}else{sprite(23, 112, 104, 1)}
        if(acheivments["aAllOrangeJuice"]) {sprite(15, 160, 104,1)}else{sprite(23, 160, 104, 1)}

        if(acheivments["aDairyQueen"]) {sprite(12, 208, 40,1)}else{sprite(23, 208, 40, 1)}
        if(acheivments["aGrassGreener"]){ sprite(14, 208, 104,1)}else{sprite(23, 208, 104, 1)}
    }

    boolean bp(b){
        buttonPress(b)
    }

}
