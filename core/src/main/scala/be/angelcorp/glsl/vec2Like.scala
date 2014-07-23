package be.angelcorp.glsl

import be.angelcorp.glsl.detail.GenTypes.genType

trait vec2Like[N, V2, V3, V4] extends vecType[V2, N, V2, V3, V4] {

  def x: N
  def y: N

  def x_=(v: N)
  def y_=(v: N)

  def r: N
  def g: N

  def r_=(v: N)
  def g_=(v: N)

  def s: N
  def t: N

  def s_=(v: N)
  def t_=(v: N)

  def xy: V2
  def xy_=(v: V2)

  def rg: V2
  def rg_=(v: V2)

  def st: V2
  def st_=(v: V2)

}

trait vec2 extends genType with vec2Like[Float, vec2, vec3, vec4]

object vec2 {

  def apply( s: Float ): vec2 = ???

  def apply( x: Float, y: Float ): vec2 = ???
  def apply( xy: vec2Like[_, _, _, _] ): vec2 = ???

}

trait ivec2 extends genType with vec2Like[Int, ivec2, ivec3, ivec4]

object ivec2 {

  def apply( s: Int ): ivec2 = ???

  def apply( x: Int, y: Int ): ivec2 = ???
  def apply( xy: vec2Like[_, _, _, _] ): ivec2 = ???

}

trait uvec2 extends genType with vec2Like[Int, uvec2, uvec3, uvec4]

object uvec2 {

  def apply( s: Int ): uvec2 = ???

  def apply( x: Int, y: Int ): uvec2 = ???
  def apply( xy: vec2Like[_, _, _, _] ): uvec2 = ???

}

trait bvec2 extends genType with vec2Like[Boolean, bvec2, bvec3, bvec4]

object bvec2 {

  def apply( s: Boolean ): bvec2 = ???

  def apply( x: Boolean, y: Boolean ): bvec2 = ???
  def apply( xy: vec2Like[_, _, _, _] ): bvec2 = ???

}

