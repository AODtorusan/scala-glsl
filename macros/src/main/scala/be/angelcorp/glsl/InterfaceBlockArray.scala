package be.angelcorp.glsl

abstract class InterfaceBlockArray(val size: Int) extends InterfaceBlock {

  def apply(index: Int): this.type = ???
  def update(index: Int, value: this.type ): Unit = ???

}
