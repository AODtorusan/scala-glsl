package be.angelcorp.glsl

import be.angelcorp.glsl.detail.GenTypes.genType

trait vec3Like[N, V2, V3, V4] extends vecType[V3, N, V2, V3, V4] {

  def x: N
  def y: N
  def z: N

  def x_=(v: N)
  def y_=(v: N)
  def z_=(v: N)

  def r: N
  def g: N
  def b: N

  def r_=(v: N)
  def g_=(v: N)
  def b_=(v: N)

  def s: N
  def t: N
  def p: N

  def s_=(v: N)
  def t_=(v: N)
  def p_=(v: N)

  def xy: V2
  def yz: V2

  def xy_=(v: V2)
  def yz_=(v: V2)

  def rg: V2
  def gb: V2

  def rg_=(v: V2)
  def gb_=(v: V2)

  def st: V2
  def tp: V2

  def st_=(v: V2)
  def tp_=(v: V2)

  def xyz: V3
  def xyz_=(v: V3)

  def rgb: V3
  def rgb_=(v: V3)

  def stp: V3
  def stp_=(v: V3)

}

trait vec3 extends genType with vec3Like[Float, vec2, vec3, vec4]

object vec3 {

  def apply( s: Float ): vec3 = ???

  def apply( x: Float, y: Float, z: Float ): vec3 = ???
  def apply( xy: vec2, z: Float ): vec3 = ???
  def apply( x: Float, yz: vec2 ): vec3 = ???
  def apply( xyz: vec3Like[_,_,_,_] ): vec3 = ???

}

trait ivec3 extends genType with vec3Like[Int, ivec2, ivec3, ivec4]

object ivec3 {

  def apply( s: Int ): ivec3 = ???

  def apply( x: Int, y: Int, z: Int ): ivec3 = ???
  def apply( xy: ivec2, z: Int ): ivec3 = ???
  def apply( x: Int, yz: ivec2 ): ivec3 = ???
  def apply( xyz: vec3Like[_,_,_,_] ): ivec3 = ???

}

trait uvec3 extends genType with vec3Like[Int, uvec2, uvec3, uvec4]

object uvec3 {

  def apply( s: Int ): uvec3 = ???

  def apply( x: Int, y: Int, z: Int ): uvec3 = ???
  def apply( xy: uvec2, z: Int ): uvec3 = ???
  def apply( x: Int, yz: uvec2 ): uvec3 = ???
  def apply( xyz: vec3Like[_,_,_,_] ): uvec3 = ???

}

trait bvec3 extends genType with vec3Like[Boolean, bvec2, bvec3, bvec4]

object bvec3 {

  def apply( s: Boolean ): bvec3 = ???

  def apply( x: Boolean, y: Boolean, z: Boolean ): bvec3 = ???
  def apply( xy: bvec2, z: Boolean ): bvec3 = ???
  def apply( x: Boolean, yz: bvec2 ): bvec3 = ???
  def apply( xyz: vec3Like[_,_,_,_] ): bvec3 = ???

}

