import java.math.BigDecimal;
open class Kotlin: leikr.Engine() {
	override fun create(){}
	override fun update(delta: Float){}
	override fun render(){
		val x:BigDecimal =  BigDecimal(0)
		super.drawString("Hello, world!", x, x, 28)
	}
}
