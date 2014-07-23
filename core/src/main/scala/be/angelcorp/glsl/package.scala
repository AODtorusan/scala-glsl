package be.angelcorp

import be.angelcorp.glsl.detail.GenTypes.genSampler

package object glsl {

  implicit def glslfloat2Float( i: float ): Float = ???
  implicit def float2glslfloat( i: Float ): float = ???

  implicit def int2glslfloat(  i: int ): float = ???
  implicit def int2glslfloat(  i: ivec2 ): vec2 = ???
  implicit def uint2glslfloat( i: uint ): float = ???
  implicit def uint2glslfloat( i: uvec2 ): vec2 = ???

  implicit def glslint2Int( i: int ): Int = ???
  implicit def int2glslint( i: Int ): int = ???

  implicit def uint2Int( i: uint ): Int = ???
  implicit def int2Uint( i: Int ): uint = ???

  implicit def bool2Boolean( b: bool ): Boolean = ???
  implicit def boolean2bool( b: Boolean ): bool = ???

  type mat2 = mat2x2
  trait mat2x2
  trait mat2x3
  trait mat2x4
  trait mat3x2
  type mat3x3 = mat3
  trait mat3x4
  trait mat4x2
  trait mat4x3
  type mat4x4 = mat4

  trait sampler1D extends genSampler[vec4]
  trait sampler2D extends genSampler[vec4]
  trait sampler3D extends genSampler[vec4]
  trait samplerCube extends genSampler[vec4]
  trait sampler2DRect extends genSampler[vec4]
  trait sampler1DShadow extends genSampler[vec4]
  trait sampler2DShadow extends genSampler[vec4]
  trait sampler2DRectShadow extends genSampler[vec4]
  trait sampler1DArray extends genSampler[vec4]
  trait sampler2DArray extends genSampler[vec4]
  trait sampler1DArrayShadow extends genSampler[vec4]
  trait sampler2DArrayShadow extends genSampler[vec4]
  trait samplerBuffer extends genSampler[vec4]
  trait sampler2DMS extends genSampler[vec4]
  trait sampler2DMSArray extends genSampler[vec4]

  trait isampler1D extends genSampler[ivec4]
  trait isampler2D extends genSampler[ivec4]
  trait isampler3D extends genSampler[ivec4]
  trait isamplerCube extends genSampler[ivec4]
  trait isampler2DRect extends genSampler[ivec4]
  trait isampler1DArray extends genSampler[ivec4]
  trait isampler2DArray extends genSampler[ivec4]
  trait isamplerBuffer extends genSampler[ivec4]
  trait isampler2DMS extends genSampler[ivec4]
  trait isampler2DMSArray extends genSampler[ivec4]

  trait usampler1D extends genSampler[uvec4]
  trait usampler2D extends genSampler[uvec4]
  trait usampler3D extends genSampler[uvec4]
  trait usamplerCube extends genSampler[uvec4]
  trait usampler2DRect extends genSampler[uvec4]
  trait usampler1DArray extends genSampler[uvec4]
  trait usampler2DArray extends genSampler[uvec4]
  trait usamplerBuffer extends genSampler[uvec4]
  trait usampler2DMS extends genSampler[uvec4]
  trait usampler2DMSArray extends genSampler[uvec4]

  /// Layout args
  trait MemoryLayout
  object shared​ extends MemoryLayout
  object packed​ extends MemoryLayout
  object std140​ extends MemoryLayout
  object std430 extends MemoryLayout

}
