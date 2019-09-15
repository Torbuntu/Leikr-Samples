class Player{
	def x = 110, y = 20, vx = 0, vy = 0, gv = 2, coin = 0, spid = 0, t = 0, i = 0
	
	def idleAnimation, walkingAnimation, runningAnimation, jumpingAnimation
	
	//0 = idle, 1 = walk, 2 = run, 3 = jump
	def state = 0
	
	//true = left, false = right
	boolean facing = true, ground = false, jumping = false
	
	BigDecimal[] frameSpeedTen = [0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f]
	BigDecimal[] frameSpeedEight = [0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f,0.1f]
	
	def jumpTime = 12
	
	def Player(screen){
		idleAnimation = [0,1,2,3,4,5,6,7,8,9]
		walkingAnimation = [10, 11, 12,13, 14, 15,16,17,18,19]
		runningAnimation = [20, 21, 22, 23, 24, 25, 26, 27]
		jumpingAnimation = [30, 31, 32, 33, 34, 35, 36, 37]
	}
	
	def setState(newState){
		state = newState
	}
	def setFace(face){
		facing = face
	}
	
	def onGround(){
		ground = true
	}
	def falling(){
		ground = false
	}
	
	def setVX(spd){
		vx = spd
	}
	
	def update(delta){
		t++
		switch(state){
			case 0:
				if(t >= 7) {
					t=0
					i++
				}
				if(i>=10)i=0
				spid = idleAnimation[i]				
				break;
			case 1:
				if(t >= 7) {
					t=0
					i++
				}
				if(i>=10)i=0
				spid = walkingAnimation[i]
				break;
			case 2:
				if(t >= 7) {
					t=0
					i++
				}
				if(i>=8)i=0
				spid = runningAnimation[i]
				break;
			case 3:
				if(t >= 7) {
					t=0
					i++
				}
				if(i>=8)i=0
				spid = jumpingAnimation[i]
				break;
		}
		
		//Movement
		if(jumping && jumpTime > 0){
			vy-=0.5f
			jumpTime--
		}else{
			vy = 0
		}
		if(jumpTime == 0){
			jumpTime = 12
			jumping = false
		}
					
		if(!ground && !jumping){
			vy += 3
			state = 3
		}
		
		y += vy
		x += vx
		
		vx = 0
		
		
		if(y>= 160) {
			x=80
			y=20
		}
	}	

	def draw(screen){
		screen.sprite(spid, x, y, facing, false, 2)
	}
}
