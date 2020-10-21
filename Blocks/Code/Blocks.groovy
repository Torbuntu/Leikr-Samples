//https://github.com/CodeExplainedRepo/Tetris-JavaScript/blob/master/tetris.js
/**
	Blocks is an educational demo for Leikr, based on the popular puzzle game Tetris
*/
class Blocks extends leikr.Engine {
	int ROW = 20, COL = 10, SQ = 7, EMPTY = 0, score = 0, lines = 0
	def board
	def piece, next
	def gameOver = false
	
	def singleScore = 0, doubleScore = 0, tripleScore = 0, tetrisScore = 0
	
	def PIECES = [
		[Tetrominoe.Z,8],
		[Tetrominoe.S,9],
		[Tetrominoe.T,32],
		[Tetrominoe.O,24],
		[Tetrominoe.L,26],
		[Tetrominoe.I,21],
		[Tetrominoe.J,10]
	]
	
	def bag = [0,1,2,3,4,5,6]
	def current = 0
	
	void create(){
		init()
	}
	
	void init(){
		board = []
		ROW.times{ r->
			board[r] = []
			COL.times{ c->
				board[r][c] = EMPTY
			}
		}
		bag.shuffle()
		next = randomPiece()
		piece = randomPiece()
		
		lines = 0
		score = 0
		singleScore = 0
		doubleScore = 0
		tripleScore = 0
		tetrisScore = 0
	}

	void update(){
		if(gameOver){
			if(keyPress("Enter")){
				gameOver = false
				init()
			}
			return
		}
	
		if(keyPress("Left")){
			piece.moveLeft()
		}else if(keyPress("Up")){
			piece.rotate()
		}else if(keyPress("Z")){
			piece.rotateB()
		}else if(keyPress("Right")){
			piece.moveRight()
		}else if(keyPress("Down")){
			piece.moveDown()
		}else if(keyPress("Space")){
			piece.down()
		}
		
		drop()
	}
	
	void render(){
		if(gameOver){
			drawString(1, "Game Over", 1, 1)
			drawString(1, "Score: $score", 1, 20)
			drawString(1, "Lines: $lines", 1, 30)
			drawString(1, "Enter to play again.", 1, 10)
			return
		}
		//field
		drawRect(1, 76, 0, 72, 138)
		drawBoard()
		piece.draw()
		next.drawNext(12, 4)
		drawString(1, "NEXT", 164, 16) 
		
		drawString(1, "Score: $score", 10, 30)
		drawString(1, "Lines: $lines", 10, 40)
		drawString(1, "Single: $singleScore", 10, 50)
		drawString(1, "Double: $doubleScore", 10, 60)
		drawString(1, "Triple: $tripleScore", 10, 70)
		drawString(1, "Tetris: $tetrisScore", 10, 80)
		
	}
	
	void drawBoard() {
		ROW.times{ r->
			COL.times{ c->
				drawSquare(c, r, board[r][c])
			}
		}
	}
	
	void drawSquare(x,y,color){
		//drawRect(color, 16 + x*SQ, y*SQ, 7, 7)
		drawCircle(color, 80 + x*SQ, y*SQ, 3)
	}
	
	def randomPiece(){
		int r = bag[current]
		current++
		if(current > 6) {
			current = 0
			bag.shuffle()
		}
		return new Piece(PIECES[r][0], PIECES[r][1])
	}
	
	int time = 0, speed = 25
	void drop(){
		time++
		if(time > speed) {
			time = 0
			piece.moveDown()
		}
	}
	
	class Piece{
		int x, y, tetrominoN
		def color
		def tetromino
		def activeTetromino
		
		Piece(tetromino, color){
			this.tetromino = tetromino
			this.color = color
			this.tetrominoN = 0; // we start from the first pattern
			this.activeTetromino = this.tetromino[this.tetrominoN];
			
			// we need to control the pieces
			this.x = 3;
			this.y = -2;
		}
				
		def fill(clr){
			activeTetromino.size.times{r->
				activeTetromino.size.times{c->
					if(activeTetromino[r][c]){
						drawSquare(x+c, y+r, clr)
					}
				}
			}
		}
		def drawNext(x,y){
			activeTetromino.size.times{r->
				activeTetromino.size.times{c->
					if(activeTetromino[r][c]){
						drawSquare(x+c, y+r, color)
					}
				}
			}
		}
		def draw(){
			fill(color)
		}
		def unDraw(){
			fill(EMPTY)
		}
		def moveDown(){
			if(!collision(0,1,this.activeTetromino)){
				this.y++
			}else{
				this.lock()
				piece = next
				next = randomPiece()
			}
		}
		def down(){
			while(!collision(0,1,this.activeTetromino)){
				this.y++
			}
			
			this.lock()
			piece = next
			next = randomPiece()
		}
		def moveRight(){
			if(!collision(1,0,this.activeTetromino)){
				this.x++
			}
		}
		def moveLeft(){
			if(!collision(-1,0,this.activeTetromino)){
				this.x--
			}
		}
		def rotate(){
			def nextPattern = this.tetromino[(this.tetrominoN + 1)%this.tetromino.size]
			def kick = 0
			if(this.collision(0,0,nextPattern)){
				if(this.x > COL/2){
				    // it's the right wall
				    kick = -1; // we need to move the piece to the left
				}else{
				    // it's the left wall
				    kick = 1; // we need to move the piece to the right
				}
			}
			if(!this.collision(kick,0,nextPattern)){
				this.x += kick;
				this.tetrominoN = (this.tetrominoN + 1)%this.tetromino.size; // (0+1)%4 => 1
				this.activeTetromino = this.tetromino[this.tetrominoN];
			}
		}
		def rotateB(){
			def nextPattern = this.tetromino[(this.tetrominoN - 1)%this.tetromino.size]
			def kick = 0
			if(this.collision(0,0,nextPattern)){
				if(this.x > COL/2){
				    // it's the right wall
				    kick = -1; // we need to move the piece to the left
				}else{
				    // it's the left wall
				    kick = 1; // we need to move the piece to the right
				}
			}
			if(!this.collision(kick,0,nextPattern)){
				this.x += kick;
				this.tetrominoN = (this.tetrominoN - 1)%this.tetromino.size; // (0+1)%4 => 1
				this.activeTetromino = this.tetromino[this.tetrominoN];
			}
		}
		
		
		def lock(){
			for(int r = 0; r < this.activeTetromino.size; r++){
				for(int c = 0; c < this.activeTetromino.size; c++){
				    // we skip the empty squares
				    if( !this.activeTetromino[r][c]){
				        continue;
				    }
				    // pieces to lock on top = game over
				    if(this.y + r < 0){
				        //alert("Game Over");
				        // stop request animation frame
				        gameOver = true;
				        score += singleScore * 40
				        score += doubleScore * 100
				        score += tripleScore * 300
				        score += tetrisScore * 1200
				        break;
				    }
				    // we lock the piece
				    board[this.y+r][this.x+c] = this.color;
				}
			}
			// remove full rows
			def count = 0
			for(int r = 0; r < ROW; r++){
				def isRowFull = true;
				
				for(int c = 0; c < COL; c++){
				    isRowFull = isRowFull && (board[r][c] != EMPTY);
				}
				if(isRowFull){
				    // if the row is full
				    // we move down all the rows above it
				    for(int y = r; y > 1; y--){
				        for(int c = 0; c < COL; c++){
				            board[y][c] = board[y-1][c];
				        }
				    }
				    // the top row board[0][..] has no row above it
				    for(int c = 0; c < COL; c++){
				        board[0][c] = EMPTY;
				    }
				    // increment the score
				    //score += 10;
				    count++
				}
			}
			switch(count){
				case 1:
					singleScore++
					break
				case 2:
					doubleScore++
					break
				case 3:
					tripleScore++
					break
				case 4:
					tetrisScore++
					break
			}
			lines += count
		}
		def collision(x,y,piece){
			for(int r = 0; r < piece.size; r++){
				for(int c = 0; c < piece.size; c++){
				    // if the square is empty, we skip it
				    if(!piece[r][c]){
				        continue;
				    }
				    // coordinates of the piece after movement
				    def newX = this.x + c + x;
				    def newY = this.y + r + y;
				    
				    // conditions
				    if(newX < 0 || newX >= COL || newY >= ROW){
				        return true;
				    }
				    // skip newY < 0; board[-1] will crush our game
				    if(newY < 0){
				        continue;
				    }
				    // check if there is a locked piece alrady in place
				    if( board[newY][newX] != EMPTY){
				        return true;
				    }
				}
			}
			return false;
		}
	}
}

class Tetrominoe{
	static def I = [
		[
			[0, 0, 0, 0],
			[1, 1, 1, 1],
			[0, 0, 0, 0],
			[0, 0, 0, 0],
		],
		[
			[0, 0, 1, 0],
			[0, 0, 1, 0],
			[0, 0, 1, 0],
			[0, 0, 1, 0],
		],
		[
			[0, 0, 0, 0],
			[0, 0, 0, 0],
			[1, 1, 1, 1],
			[0, 0, 0, 0],
		],
		[
			[0, 1, 0, 0],
			[0, 1, 0, 0],
			[0, 1, 0, 0],
			[0, 1, 0, 0],
		]
	];

	static def J = [
		[
			[1, 0, 0],
			[1, 1, 1],
			[0, 0, 0]
		],
		[
			[1, 1, 0],
			[1, 0, 0],
			[1, 0, 0]
		],
		[
			[0, 0, 0],
			[1, 1, 1],
			[0, 0, 1]
		],
		[
			[0, 0, 1],
			[0, 0, 1],
			[0, 1, 1]
		]
	];

	static def L = [
		[
			[0, 0, 1],
			[1, 1, 1],
			[0, 0, 0]
		],
		[
			[0, 1, 0],
			[0, 1, 0],
			[0, 1, 1]
		],
		[
			[0, 0, 0],
			[1, 1, 1],
			[1, 0, 0]
		],
		[
			[1, 1, 0],
			[0, 1, 0],
			[0, 1, 0]
		]
	];

	static def O = [
		[
			[0, 0, 0, 0],
			[0, 1, 1, 0],
			[0, 1, 1, 0],
			[0, 0, 0, 0],
		]
	];

	static def S = [
		[
			[0, 1, 1],
			[1, 1, 0],
			[0, 0, 0]
		],
		[
			[0, 1, 0],
			[0, 1, 1],
			[0, 0, 1]
		],
		[
			[0, 0, 0],
			[0, 1, 1],
			[1, 1, 0]
		],
		[
			[1, 0, 0],
			[1, 1, 0],
			[0, 1, 0]
		]
	];

	static def T = [
		[
			[0, 1, 0],
			[1, 1, 1],
			[0, 0, 0]
		],
		[
			[0, 1, 0],
			[0, 1, 1],
			[0, 1, 0]
		],
		[
			[0, 0, 0],
			[1, 1, 1],
			[0, 1, 0]
		],
		[
			[0, 1, 0],
			[1, 1, 0],
			[0, 1, 0]
		]
	];

	static def Z = [
		[
			[1, 1, 0],
			[0, 1, 1],
			[0, 0, 0]
		],
		[
			[0, 0, 1],
			[0, 1, 1],
			[0, 1, 0]
		],
		[
			[0, 0, 0],
			[1, 1, 0],
			[0, 1, 1]
		],
		[
			[0, 1, 0],
			[1, 1, 0],
			[1, 0, 0]
		]
	];
}
