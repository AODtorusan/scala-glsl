package be.angelcorp.glsl.glslBase

import be.angelcorp.glsl._
import be.angelcorp.glsl.detail.GenTypes._

trait GlslBase extends GlslDSL {


  type void = Unit

  def texelFetch[T <: vecType[_,_,_,_,_]](sampler: genSampler[T], P: ivec2, lod: Int): T = __

  def texture( sampler: sampler2D, pos: vec2 ): vec4 = __

  // Angle and Trigonometry Functions
  def radians[T <: genType](degrees: => T): T = __
  def degrees[T <: genType](radians: genType): T = __
  def sin[T <: genType](angle: genType): T = __
  def cos[T <: genType](angle: genType): T = __
  def tan[T <: genType](angle: genType): T = __
  def asin[T <: genType](angle: genType): T = __
  def acos[T <: genType](angle: genType): T = __
  def atan[T <: genType](angle: genType): T = __
  def atan[T <: genType](y: genType, x: genType): T = __
  def sinh[T <: genType](angle: genType): T = __
  def cosh[T <: genType](angle: genType): T = __
  def tanh[T <: genType](angle: genType): T = __
  def asinh[T <: genType](angle: genType): T = __
  def acosh[T <: genType](angle: genType): T = __
  def atanh[T <: genType](angle: genType): T = __

  // Exponential Functions
  def pow[T <: genType](x: => T, y: => T): T = __
  def exp[T <: genType](x: => T): T = __
  def log[T <: genType](x: => T): T = __
  def exp2[T <: genType](x: => T): T = __
  def log2[T <: genType](x: => T): T = __
  def sqrt[T <: genType](x: => T): T = __
  def inversesqrt[T <: genType](x: => T): T = __

  // Common Functions
  def abs[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def sign[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def floor[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def trunc[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def round[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def roundEven[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def ceil[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def fract[T <: vecType[_,_,_,_,_]](x: => T): T = __
  def mod[T <: vecType[_,_,_,_,_]](x: => T, y: => T): T = __
  def modf[T <: vecType[_,_,_,_,_]](x: => T, y: => T): T = __
  def min[T <: vecType[_,_,_,_,_]](x: => T, y: => T): T = __
  def max[T <: vecType[_,_,_,_,_]](x: => T, y: => T): T = __
  def clamp[T <: vecType[_,_,_,_,_]](x: => T, minVal: => T, maxVal: => T): T = __
  def mix[T <: vecType[_,_,_,_,_]](x: => T, y: => T, a: => T): T = __
  def mix[T <: vecType[_,_,_,_,_]](x: => T, y: => T, a: Float): T = __
  def mix[T <: vecType[_,_,_,_,_]](x: => T, y: => T, a: genBType): T = __
  def step[T <: vecType[_,_,_,_,_]](edge: => T, x: => T): T = __
  def step[T <: vecType[_,_,_,_,_]](edge: Float, x: => T): T = __
  def smoothstep[T <: vecType[_,_,_,_,_]](edge0: => T, edge1: => T, x: => T): T = __
  def smoothstep[T <: vecType[_,_,_,_,_]](edge0: Float, edge1: Float, x: => T): T = __
  def isnan[T <: vecType[_,_,_,_,_]](x: => T): genBType = __
  def isinf[T <: vecType[_,_,_,_,_]](x: => T): genBType = __
  def floatBitsToInt[T <: vecType[_,_,_,_,_]](x: => T): genIType = __
  def floatBitsToUint[T <: vecType[_,_,_,_,_]](x: => T): genUType = __
  def intBitsTofloat(x: genIType): genType = __
  def uintBitsTofloat(x: genUType): genType = __

  // Geometric Functions
  def length[T <: vecType[_,_,_,_,_]](vector: => T): Float = __
  def distance[T <: vecType[_,_,_,_,_]](p0: => T, p1: => T): Float = __
  def dot[T <: vecType[_,_,_,_,_]](x: => T, y: => T): Float = __
  def cross(x: vec3, y: vec3): vec3 = __
  def normalize[T <: vecType[_,_,_,_,_]](vector: => T): T = __
  def faceforward[T <: vecType[_,_,_,_,_]](N: => T, I: => T, Nref: => T): T = __
  def reflect[T <: vecType[_,_,_,_,_]](I: => T, N: => T): T = __
  def refract[T <: vecType[_,_,_,_,_]](I: => T, N: => T): T = __


}
