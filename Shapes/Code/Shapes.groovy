class Shapes extends leikr.Engine {
    def r, t, amt, rx, ry, i, j, x, y, x2, y2    

    void create(){
        amt = 200             
        x = 120
        y = 80
        t = 0
        rx = 120
        ry = 80
  

    }
    void update(float delta){	
    	//FPS()
    }
    void render(){	
		
        t+=1
                      
        for(j in 0..32) drawPixel j, j, 1
         
		for(i in 0..amt){ 		 	
			x = cos(i / 10 + t/40)*80
			y = sin(i / 10 + t/40)*40

			x2 = 40 + cos(x / 10 + t/40) *80
			y2 = 40 + sin(y / 10 + t/40) *40

			drawPixel((i%7)+19, (-x2+rx+5), (-y2+ry+5))//top left
			drawPixel((i%7)+19, (-x2+rx+5), (y2+ry-5) )//bottom left

			drawPixel((i%7)+19, (x2+rx-5), (-y2+ry+5))//top right
			drawPixel((i%7)+19, (x2+rx-5), (y2+ry-5))//bottom right
		}
    }
}	
