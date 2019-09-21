class Shapes extends leikr.Engine {
    def r, t, amt, rx, ry, i, j, x, y, x2, y2    

<<<<<<< HEAD
    def r, t, amt, rx, ry, i, j, x, y, x2, y2
    void create(){
        amt = 50             
=======
    void create(){
        amt = 200             
>>>>>>> 7d31c91b7168376ed0a78bf90f071fec39aef3e6
        x = 120
        y = 80
        t = 0
        rx = 120
        ry = 80
<<<<<<< HEAD
    }
    void update(float delta){	
    	t++
=======
  

    }
    void update(float delta){	
    	//FPS()
>>>>>>> 7d31c91b7168376ed0a78bf90f071fec39aef3e6
    }
    
    void render(){	
<<<<<<< HEAD
        for(j = 0; j < 32; j++) drawPixel(j, j, 1)          
         
        for(i = 0; i < amt; i++){ 		 	
            x = cos((float)(i / 10 + t/40))*80
            y = sin((float)(i / 10 + t/40))*40
            
            x2 = floor((float)(40 + cos((float)(x / 10 + t/40))*80))
            y2 = floor((float)(40 + sin((float)(y / 10 + t/40))*40))

            drawPixel((i%32)+1, (-x2+rx+5), (-y2+ry+5))//top left
            drawPixel((i%32)+1, (-x2+rx+5), (y2+ry-5))//bottom left
			
            drawPixel((i%32)+1, (x2+rx-5), (-y2+ry+5))//top right
            drawPixel((i%32)+1, (x2+rx-5), (y2+ry-5))//bottom rigt
        }
    }        
=======
		
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
>>>>>>> 7d31c91b7168376ed0a78bf90f071fec39aef3e6
}	
