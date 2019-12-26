class FlappyBirb extends leikr.Engine {


	def birb = {}
	def vy = 0
	
	def blocks = {}
	int index = 1
	def t = 0
	
    void create(){
        birb = [x: 80, y:80]
        blocks = [x: 240, y: randInt(60, 150)]
    }
    void update(float delta){
    	vy+=0.2
        if(keyPress("Space")) {
        	vy = -3
        }       
        
        birb.y += vy
        
        blocks.x -= 2
        if(blocks.x < -30) {
        	blocks.x = 240
        	blocks.y = randInt(60, 150)
    	}
    	
    	t++
    	if(t%3 == 0)index++
    	if(index > 3)index = 1
    }
    void render(){	
    	bgColor(5)

		fillCircle(8, birb.x, birb.y, 10)
		sprite(index, birb.x-26, birb.y-10, -90f, 1)
		
		fillRect(9, blocks.x, blocks.y, 30, 150)
		fillRect(9, blocks.x, blocks.y-50,30, -150)
    }
}

