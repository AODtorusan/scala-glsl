package be.angelcorp.glsl.glsl330

import be.angelcorp.glsl._

@Glsl
trait VS330Core extends Glsl330 {
  "#version 330"

  @GlslSymbolOnly def gl_VertexID: Int = ???
  @GlslSymbolOnly def gl_VertexID_=(v: Int): Unit = ???

  @GlslSymbolOnly def gl_InstanceID: Int = ???
  @GlslSymbolOnly def gl_InstanceID_=(v: Int): Unit = ???

  @GlslSymbolOnly def gl_Position: vec4 = ???
  @GlslSymbolOnly def gl_Position_=(v: vec4): Unit = ???

  @GlslSymbolOnly def gl_PointSize: float = ???
  @GlslSymbolOnly def gl_PointSize_=(v: float): Unit = ???

  @GlslSymbolOnly def gl_ClipDistance: Array[float] = ???

}
