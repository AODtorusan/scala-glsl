package be.angelcorp.glsl

trait vecType[V, N, V1, V2, V3] {

  def +( v: V ): V
  def +( s: => N ): V
  def +=( v: V ): Unit
  def +=( s: => N ): Unit

  def -( v: V ): V
  def -( s: => N ): V
  def -=( v: V ): Unit
  def -=( s: => N ): Unit

  def *( v: V ): V
  def *( s: => N ): V
  def *=( v: V ): Unit
  def *=( s: => N ): Unit

  def /( v: V ): V
  def /( s: => N ): V
  def /=( v: V ): Unit
  def /=( s: => N ): Unit

  def &( v: V ): V
  def &( s: => N ): V
  def &=( v: V ): Unit
  def &=( s: => N ): Unit

  def unary_+(): V
  def unary_-(): V

  def apply( index: Int ): N
  def update( index: Int, value: N ): Unit

}
