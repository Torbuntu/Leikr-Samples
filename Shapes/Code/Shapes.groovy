class Shapes extends leikr.Engine {

    def r, t, amt, rx, ry, i, j, x, y, x2, y2
    void create(){
        amt = 50             
        x = 120
        y = 80
        t = 0
        rx = 120
        ry = 80
    }
    void update(float delta){	
    	t++
    }
    
    void render(){	
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
}	
