class DataTest extends leikr.Engine {
	def info = 1
	def display = [:]
    void create(){
        
    }
    void update(float delta){
        if(keyPress("S")) writeData("data", [a:++info])
        if(keyPress("L")) display = readData("data")
    }
    void render(){
    	drawTexture("background-grid-8px.png", 0, 0)
		drawString(1, "Hello, data: $display", 0, 80, 240, 1)
    }
}

