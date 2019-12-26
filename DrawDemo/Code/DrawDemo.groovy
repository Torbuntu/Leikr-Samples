class DrawDemo extends leikr.Engine {
	def t = 0, r = 2
	def point = [a: [x: 130, y: 50], b: [x: 0, y:0], r: 40, s:0, c:0]
	
	def angle = 0, angleInc = Math.PI/360.0
	def boxW = 0, boxH = 0, boxS = 1
	
	//Define a few Color objects to use
	def RED 
	def GREEN  
	def BLUE 
	
	void create(){
		RED = getColor("255,0,0")
		GREEN = getColor("0,255,0")
		BLUE = getColor("0,0,255")
	}
    void update(float delta){
        t++
        
        //Update circle radius
        if(t%10>8)r++
        if(r > 10) r = 2
        
        //Update red box sizes
        boxW += boxS
        boxH += boxS
        if(boxW > 20 || boxH > 20) boxS = -boxS
        if(boxW <= 0 || boxH <= 0) boxS = -boxS
        
        //Update the line rendering
    	angle+=angleInc*2
        point.s = sin(angle)
		point.c = cos(angle)
		point.b.x = point.a.x + (point.r*point.c) 
		point.b.y = point.a.y + (point.r*point.s) 
    }
    void render(){	
		bgColor(3)
		sprite(0,0,0,1)
		
		/*
		`drawPixel c, x, y`
		Drawing raw pixels to the screen is the most basic command.
		
		It simply takes a color param either an ID from the built in
		palette, or a String RGB value such as "155,255,155"
		*/
		
		//there are 33 indexes, this will draw each color
		33.times{c->
			drawPixel c, 2+c*2, 3
		}
		drawPixel 32, 10, 10 //magenta pixel in top left 10,10
		drawPixel "50,50,155", 10,8 //sort of blue pixel above the other
		
		//Get the Color object from coordinates and pass to drawPixel
		drawPixel getPixel(17, 10), 10, 20
		
		//These are of course more fun for doing fancy drawing patterns.
		200.times{i->
			def d = cos(t/5 + i/2)  * 5
			def a = i / 10 + t/120
			def x = cos(a)*d+20
			def y = sin(a)*d+10
			drawPixel (i+10)%30, x, y
			drawPixel "${50+t%155},${100+t%105},${t%255}", y+30, x-5
		}
		
		drawString(7, "Draw Pixel", 10, 25)
		drawRect(7, 0, 0, 70, 40)
		
		/*
		`drawRect c, x, y, w, h`
		Drawing rectangles is another basic command, but has many uses
		ranging from in-game dynamic art, to hitboxes for debugging.
		*/
		drawRect 10, 5, 45, 10, 10 //blue box
		drawRect "255,100,255", 5, 60, 5, 5//magenta box using RGB String
		drawRect GREEN, 11, 60, 4, 4
		
		drawRect(8, 20, 45, boxW, boxH) //red box with dynamic width and height
		drawRect "0,200,200", boxW+35, boxH+42, 10, 10 //cyan dynamic moving box
		
		drawString 7, "Draw Rect", 10, 70
		drawRect 7, 0, 40, 70, 40
		
		/*
		`void fillRect c, x, y, w ,h`
		Similar to drawRect except it is a filled rectangle instead of a 
		hollow box.
		*/
		
		fillRect 8, 5, 85, 10, 10 //solid red square
		fillRect "0,255,100", 5, 100, 5, 5 //smaller green square with RGB String
		fillRect BLUE, 11, 100, 4, 4
		
		
		fillRect(18, 20, 85, boxW, boxH) //red box with dynamic width and height
		fillRect "0,200,200", boxW+35, boxH+82, 10, 10 //cyan dynamic moving box
		
		drawRect 7, 0, 80, 70, 40
		drawString 7, "Fill Rect", 10, 110
		
		/*
		`drawCircle c, x, y, r`
		Drawing circles is just as easy as drawing rectangles.
		*/
		
		drawCircle 16, 15, 135, 12
		drawCircle "128,0,128", 15, 135, 8
		drawCircle RED, 15, 135, 5
		
		drawCircle "${50+t%155},${100+t%105},${t%255}", 45, 135, r
		drawCircle "${50+r%155},${100+t%105},${r%255}", 45, 135, r/2
		
		drawRect 7, 0, 120, 70, 40
		drawString 7, "Draw Circle", 10, 150
		
		/*
		`fillCircle c, x, y, r`
		Fill circle is just like drawCircle but filled, of course.
		*/
		fillCircle 16, 85, 135, 12
		fillCircle "128,0,128", 85, 135, 8
		fillCircle BLUE, 85, 135, 5
		
		fillCircle "${50+t%155},${100+t%105},${t%255}", 115, 135, r
		fillCircle "${50+r%155},${100+t%105},${r%255}", 115, 135, r/2
		
		drawRect 7, 70, 120, 70, 40
		drawString 7, "Fill Circle", 80, 150
		
		/*
		`drawLineSegment c, x1, y1, x2, y2`
		Draws a line from one point to another with a given color. 		
		*/
		
		
		drawLineSegment 32, point.a.x, point.a.y, point.b.x, point.b.y
		drawLineSegment "0,100,200", point.a.x, point.a.y, point.b.x+10, point.b.y+10
		drawLineSegment GREEN, point.b.x, point.b.y, point.a.x+15, point.a.y
		drawString 7, "Draw Line Segment", point.b.x-15, point.b.y
		
		
		//Test
		drawCircle(1, 200, 150, 5)
    }
}

