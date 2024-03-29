class LeikrShmup extends leikr.Engine {
	
	Player p = new Player()
	def bullets = []
	def enemies = []
	def offX = 0, offY = 0
    void create(){
    }
    
    void update(float delta){
    	offX--
    	if(offX <= -256) offX=0
    	
    	FPS()
        if(key("Space")){
        	bullets.add(new Bullet(p.x, p.y))	
        }
        
        //test
        if(key("E")) enemies.add(new Enemy(1))
        
        if(key("Left")) {
        	p.speedX = -3
        	offX += 0	
        }
        if(key("Right")) {
        	p.speedX = 3
        	offX -= 1	
        }
        if(key("Up")) {
        	p.speedY = -3
        }
        if(key("Down")) {
        	p.speedY = 3
        }
        p.update(delta)
        
        bullets.each{
        	it.update(delta)
        } 
        enemies.each{ e ->
        	e.update(delta)
        	bullets.each{b ->
        		if (e.x < b.x + 4 && e.x + 8 > b.x && e.y < b.y + 4 && e.y + 8 > b.y ) e.hit = true
        	}
        	if(e.x < 0) e.gone = true
        }
        
        //Remove enemies and bullets if they are off the lGraphics or hit.
        enemies.removeAll{it.hit == true}
        enemies.removeAll{it.gone == true}
        bullets.removeAll{it.gone == true}
    }
    
    void render(){	
    	drawTexture("exterior-parallaxBG1.png", offX-256, -35)
    	drawTexture("exterior-parallaxBG1.png", offX, -35)
    	drawTexture("exterior-parallaxBG1.png", offX+256, -35)
    	
    
		p.draw(lGraphics)
		bullets.each{
			it.draw(lGraphics)
		}
		enemies.each{
			it.draw(lGraphics)
		}
    }	
}

