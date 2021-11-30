
class SaveUtil {

	def highScore = 0;
	def acheivments = [:]
	def dataManager
	SaveUtil(dataManager){
		this.dataManager = dataManager
	}
	
	def loadScore(){
		try{
			highScore = dataManager.readData("score").get("high_score")
		}catch(Exception ex){
			highScore = 0
		}
		return highScore
	}
	
	def loadAcheivments(){
		try{
			acheivments = dataManager.readData("score").get("acheivments")
			println "loaded: ${acheivments}"
			if(acheivments.size() < 10){
				acheivments = [
					aDairyQueen: false,
					aMeatPump: false, 
					aGrassGreener: false,
					aAllOrangeJuice: false,
					aFive: false,
					aTen: false,
					aFifteen: false,
					aTwenty: false,
					aBombBastic: false,
					aYeOleSwitcheroo: false
				]
			}
		}catch(Exception ex){
			acheivments = [
				aDairyQueen: false,
				aMeatPump: false, 
				aGrassGreener: false,
				aAllOrangeJuice: false,
				aFive: false,
				aTen: false,
				aFifteen: false,
				aTwenty: false,
				aBombBastic: false,
				aYeOleSwitcheroo: false
			]
		}
		return acheivments
	}
	
	
	void saveScore(score){
		if(score > highScore){
			highScore = score
			dataManager.saveData("score", ["high_score": highScore, "acheivments": acheivments])
			
		}	
	}
	
	void saveAcheivments(a){
		acheivments = a
		dataManager.saveData("score", ["high_score": highScore, "acheivments": acheivments])
	}
}
