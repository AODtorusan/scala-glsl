package be.angelcorp.glsl

trait matLike[M, V] {

  def *(m: M): M
  def *(v: => V): V

}
