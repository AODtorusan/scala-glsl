package be.angelcorp.glsl.glsl410

import be.angelcorp.glsl._

@Glsl
trait FS410Core extends Glsl410 {
  "#version 410 core"

  // in
  @GlslSymbolOnly def gl_FragCoord: vec4 = ???
  @GlslSymbolOnly def gl_FrontFacing: bool = ???
  @GlslSymbolOnly def gl_ClipDistance: Array[float] = ???
  @GlslSymbolOnly def gl_PointCoord: vec2 = ???
  @GlslSymbolOnly def gl_PrimitiveID: int = ???

  // out
  @GlslSymbolOnly def gl_FragColor_=(v: vec4): Unit = ??? // deprecated
  @GlslSymbolOnly def gl_FragData: Array[vec4] = ??? // deprecated
  @GlslSymbolOnly def gl_FragDepth_=(v: float): Unit = ???

}
