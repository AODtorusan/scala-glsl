package be.angelcorp.glsl

import be.angelcorp.glsl.detail.GenTypes._

trait vec1Like[N, V2, V3, V4] extends vecType[N, N, V2, V3, V4] {

  def +( v: V2 )(implicit d: DummyImplicit): V2
  def +( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def +( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

  def -( v: V2 )(implicit d: DummyImplicit): V2
  def -( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def -( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

  def *( v: V2 )(implicit d: DummyImplicit): V2
  def *( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def *( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

  def /( v: V2 )(implicit d: DummyImplicit): V2
  def /( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def /( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

  def &( v: V2 )(implicit d: DummyImplicit): V2
  def &( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def &( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

  def |( v: V2 )(implicit d: DummyImplicit): V2
  def |( v: V3 )(implicit d: DummyImplicit, d2: DummyImplicit): V3
  def |( v: V4 )(implicit d: DummyImplicit, d2: DummyImplicit, d3: DummyImplicit): V4

}

trait float extends genType with vec1Like[float, vec2, vec3, vec4]

object float {
  def apply( value: Float ): float = ???

  def apply( value: float ): float = ???
  def apply( value: int   ): float = ???
  def apply( value: uint  ): float = ???
  def apply( value: bool  ): float = ???
}

trait int extends genIType with vec1Like[int, ivec2, ivec3, ivec4]

object int {
  def apply( value: Int   ): int = ???

  def apply( value: float ): int = ???
  def apply( value: int   ): int = ???
  def apply( value: uint  ): int = ???
  def apply( value: bool  ): int = ???
}

trait uint extends genUType with vec1Like[uint, uvec2, uvec3, uvec4]

object uint {
  def apply( value: Int   ): uint = ???

  def apply( value: float ): uint = ???
  def apply( value: int   ): uint = ???
  def apply( value: uint  ): uint = ???
  def apply( value: bool  ): uint = ???
}

trait bool extends genBType with vec1Like[bool, bvec2, bvec3, bvec4]

object bool {
  def apply( value: Boolean ): bool = ???

  def apply( value: float ): bool = ???
  def apply( value: int   ): bool = ???
  def apply( value: uint  ): bool = ???
  def apply( value: bool  ): bool = ???
}
