class Menu {

	boolean activeMenu = true
	
	def drawMenu(screen){
		screen.drawString("Press ENTER to start!", 0, 80, 240, 1, 32)
		screen.sprite(0, 100, 80, 2)
	}
}
