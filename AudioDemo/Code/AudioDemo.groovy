class AudioDemo extends leikr.Engine {
	boolean playing = false, paused = false
	String action = "play", subAction = "pause"
	def files = new File("Programs/AudioDemo/Audio/Sound").listFiles()
    //We override the onResume to handle if we should continue music or not.
    
    def soundIndex = 0, offset = 0
    def pan = 0f
    void onResume(){
    	if(paused && playing){
    		resumeAudio()
    		paused = false
    	}
    }
    //override onPause to pause music and manage our paused variable
    void onPause(){
    	pauseAudio()
    	paused = true
    }
    void update(float delta){
        if(keyPress("Space")){
        	if(playing) {
        		stopAllMusic()
        		playing = false
        		action = "play"
    		} else {
        		playMusic "DST-TDT", true
        		playing = true
        		action = "stop"
    		}
        }
        
        if(keyPress("P") && playing){
        	if(paused) {
        		resumeAudio()
        		paused = false
        		subAction = "pause"
    		}else{
    			pauseAudio()
    			paused = true
    			subAction = "resume"
    		}
        }
        
        if(keyPress("Down")) soundIndex++
        if(keyPress("Up")) soundIndex--
        if(soundIndex < 0) soundIndex = 74
        if(soundIndex > 74) soundIndex = 0
        
        if(keyPress("S")){
        	String n = files[soundIndex].getName()
        	playSound n, 1f, 1f, pan
        }
        
        offset = 0
        if(soundIndex > 19) offset = 20*8
        if(soundIndex > 39) offset = 40*8
        if(soundIndex > 59) offset = 60*8
        
        if(keyPress("Left")) pan-=0.1f
        if(keyPress("Right")) pan+=0.1f
        if(pan > 1)pan = 1
        if(pan<-1)pan = -1
    }
    void render(){	
		bgColor(4)
		drawRect 7, 0, 0, 90, 40
		drawString 7, "Music Demo", 20, 4
		drawString(7, "Press Space to $action", 10, 10)
		if(playing)drawString(7, "Press P to $subAction", 10, 20)
		
		
		drawString(7, "Sound Demo\nPress S to play\nPress Up or Down to select", 0, 110)
		
		drawString(7, "Left or Right to adjust Pan", 0, 80)
		drawString(7, "pan: $pan", 0, 90)
		files.eachWithIndex{it, idx->
			if(soundIndex == idx)drawString 32, it.getName(), 130, 8*idx-offset
			else drawString 7, it.getName(), 130, 8*idx-offset
		}
		
		drawString 7, "index: $soundIndex", 0, 100
    }
}

