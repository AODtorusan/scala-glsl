package be.angelcorp.glsl

import be.angelcorp.glsl.detail.GenTypes.genType

trait vec4Like[N, V2, V3, V4] extends vecType[V4, N, V2, V3, V4] {

  def x: N
  def y: N
  def z: N
  def w: N

  def x_=(v: N)
  def y_=(v: N)
  def z_=(v: N)
  def w_=(v: N)

  def r: N
  def g: N
  def b: N
  def a: N

  def r_=(v: N)
  def g_=(v: N)
  def b_=(v: N)
  def a_=(v: N)

  def s: N
  def t: N
  def p: N
  def q: N

  def s_=(v: N)
  def t_=(v: N)
  def p_=(v: N)
  def q_=(v: N)

  def xy: V2
  def yz: V2
  def zw: V2

  def xy_=(v: V2)
  def yz_=(v: V2)
  def zw_=(v: V2)

  def rg: V2
  def gb: V2
  def ba: V2

  def rg_=(v: V2)
  def gb_=(v: V2)
  def ba_=(v: V2)

  def st: V2
  def tp: V2
  def pq: V2

  def st_=(v: V2)
  def tp_=(v: V2)
  def pq_=(v: V2)

  def xyz: V3
  def yzw: V3

  def xyz_=(v: V3)
  def yzw_=(v: V3)

  def rgb: V3
  def gba: V3

  def rgb_=(v: V3)
  def gba_=(v: V3)

  def stp: V3
  def tpq: V3

  def stp_=(v: V3)
  def tpq_=(v: V3)

  def xyzw: V4
  def rgba: V4
  def stpq: V4

  def xyzw_=(v: V4)
  def rgba_=(v: V4)
  def stpq_=(v: V4)

}

trait vec4 extends genType with vec4Like[Float, vec2, vec3, vec4]

object vec4 {

  def apply( s: Float ): vec4 = ???

  def apply( x: Float, y: Float, z: Float, w: Float ): vec4 = ???
  def apply( xy: vec2, z: Float, w: Float ): vec4 = ???
  def apply( x: Float, yz: vec2, w: Float ): vec4 = ???
  def apply( x: Float, y: Float, zw: vec2 ): vec4 = ???
  def apply( xyz: vec3, w: Float ): vec4 = ???
  def apply( x: Float, yzw: vec3 ): vec4 = ???
  def apply( xyzw: vec4Like[_,_,_,_] ): vec4 = ???

}

trait ivec4 extends genType with vec4Like[int, ivec2, ivec3, ivec4]

object ivec4 {

  def apply( s: int ): ivec4 = ???

  def apply( x: int, y: int, z: int, w: int ): ivec4 = ???
  def apply( xy: ivec2, z: int, w: int ): ivec4 = ???
  def apply( x: int, yz: ivec2, w: int ): ivec4 = ???
  def apply( x: int, y: int, zw: ivec2 ): ivec4 = ???
  def apply( xyz: ivec3, w: int ): ivec4 = ???
  def apply( x: int, yzw: ivec3 ): ivec4 = ???
  def apply( xyzw: vec4Like[_,_,_,_] ): ivec4 = ???

}

trait uvec4 extends genType with vec4Like[uint, uvec2, uvec3, uvec4]

object uvec4 {

  def apply( s: uint ): uvec4 = ???

  def apply( x: uint, y: uint, z: uint, w: uint ): uvec4 = ???
  def apply( xy: uvec2, z: uint, w: uint ): uvec4 = ???
  def apply( x: uint, yz: uvec2, w: uint ): uvec4 = ???
  def apply( x: uint, y: uint, zw: uvec2 ): uvec4 = ???
  def apply( xyz: uvec3, w: uint ): uvec4 = ???
  def apply( x: uint, yzw: uvec3 ): uvec4 = ???
  def apply( xyzw: vec4Like[_,_,_,_] ): uvec4 = ???

}

trait bvec4 extends genType with vec4Like[bool, bvec2, bvec3, bvec4]

object bvec4 {

  def apply( s: bool ): bvec4 = ???

  def apply( x: bool, y: bool, z: bool, w: bool ): bvec4 = ???
  def apply( xy: bvec2, z: bool, w: bool ): bvec4 = ???
  def apply( x: bool, yz: bvec2, w: bool ): bvec4 = ???
  def apply( x: bool, y: bool, zw: bvec2 ): bvec4 = ???
  def apply( xyz: bvec3, w: bool ): bvec4 = ???
  def apply( x: bool, yzw: bvec3 ): bvec4 = ???
  def apply( xyzw: vec4Like[_,_,_,_] ): bvec4 = ???

}
