class Player {
	//top 0 -> 5 
	//bot 16 -> 21
	
	public enum PlayerState {
		IDLE,
		WALKING,
		PUNCH,
		KICK,
		JUMP
	}
	def idx = 0
	def x, y
	Player(){
		x = 32
		y = 75
	}
	PlayerState state = PlayerState.IDLE

	void render(t, screen){
		
		switch(state){
			case PlayerState.IDLE:
				drawIdle(t, screen)
				break
			case PlayerState.WALKING:
				drawWalk(t, screen)
				break
			case PlayerState.KICK:
				drawKick(t, screen)
				break
		}
	}
	
	void setState(st){
		switch(st){
			case 0:
				state = PlayerState.IDLE
				break
			case 1:
				state = PlayerState.WALKING
				break
			case 2:
				state = PlayerState.KICK
				break
		}
	}
	
	boolean isIdle(){
		state == PlayerState.IDLE
	}
	
	void drawIdle(t, screen){
		if(t > 10) idx = (idx+1)%6
		screen.sprite(idx, x, y, 3)
		screen.sprite(idx+16, x-3, y+64, 3)
	}
	
	void drawWalk(t, screen){
		if(t > 10) idx = (idx+1)%6
		screen.sprite(idx+32, x, y, 3)
		screen.sprite(idx+48, x, y+64, 3)
	}
	
	void drawKick(t, screen){
		if(t > 19) idx = (idx+1)%2
		if(idx > 2) idx = 0
		
		println idx
		screen.sprite(idx+64, x, y, 3)
		screen.sprite(idx+96, x+64, y, 3)
		screen.sprite(idx+80, x, y+64, 3)
	}
}
