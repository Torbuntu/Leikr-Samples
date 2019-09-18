class Eval extends leikr.Engine {
    def file = new File("Programs/Eval/Code/scpt.txt")
    def readC = false, readU = false
    def c = "", u=""
    def ur
    void create(){
        file.eachLine{line ->
        	if(line.equals(";") && readC) readC = false
        	
        	if(line.equals(";") && readU) readU = false
        	
        	if(readC) c += line + "\n"
        	
        	if(readU) u += line + "\n"

        	if(line.equals("create:")) readC = true
        	
        	if(line.equals("update:")) readU = true        	
        }
        
        ur = parse(u)
        def cr = parse(c)
        cr.run()
    }
    void update(float delta){	
    	FPS()
    	ur.run()
    }
    void render(){	
       	drawRect(ur.getProperty("x"), ur.getProperty("y"), 5, 50)
    }
}	
