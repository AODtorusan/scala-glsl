package be.angelcorp.glsl.extensions

import be.angelcorp.glsl._

@Glsl
trait GL_ARB_shading_language_packing {

  "#extension GL_ARB_shading_language_packing : enable"

  @GlslSymbolOnly def packUnorm2x16(v: vec2): uint = ???
  @GlslSymbolOnly def packSnorm2x16(v: vec2): uint = ???
  @GlslSymbolOnly def packUnorm4x8(v: vec4): uint = ???
  @GlslSymbolOnly def packSnorm4x8(v: vec4): uint = ???

  @GlslSymbolOnly def unpackUnorm2x16(v: uint): vec2 = ???
  @GlslSymbolOnly def unpackSnorm2x16(v: uint): vec2 = ???
  @GlslSymbolOnly def unpackUnorm4x8(v: uint): vec4 = ???
  @GlslSymbolOnly def unpackSnorm4x8(v: uint): vec4 = ???

  @GlslSymbolOnly def packHalf2x16(v: vec2): uint = ???
  @GlslSymbolOnly def unpackHalf2x16(v: uint): vec2 = ???

}

object GL_ARB_shading_language_packing extends GL_ARB_shading_language_packing
