class Platform extends leikr.Engine {
    def x = 100, y=30, vx = 0, vy = 0, mapX = 0, mapY = 0
	
    void create(){
        loadMap("map")
    }  
    
    void update(float delta){
    	if(y >= 160) y = 30
    	
        if(key("Left")) vx = -1.5f
        else if(key("Right")) vx = 1.5f
        else vx = 0
        
        if(solid(x+vx,    y+vy) 
        || solid(x+10+vx, y+vy) 
        || solid(x+vx,    y+31+vy) 
        || solid(x+10+vx, y+31+vy)) vx = 0
        
        if(solid(x, y+32+vy) || solid(x+10, y+32+vy)) vy = 0
        else vy += 2
        
        if(vy == 0 && key("Space")) vy = -10
        
        if(vy < 0 && (solid(x+vx, y+vy) || solid(x+10+vx, y+vy))) vy = 0
        
        x += vx
        y += vy
    }
    
    void render(){	
    	bgColor(10)    	
        drawMap(mapX, mapY)
        sprite(0, x, y, 2)
    }	

    def solid(x,y){
        int id = getMapTileId(floor((x - mapX) /32),floor((y - mapY) /32))       
        return	( id > 81 && id <= 97)
    }	
}

