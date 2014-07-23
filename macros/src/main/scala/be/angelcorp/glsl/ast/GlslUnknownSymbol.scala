package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.{SimpleType, GlslContext}

case class GlslUnknownSymbol( name: String ) extends GlslSymbol {
  def typ = new SimpleType("Any", new GlslContext(None))
  val context = new GlslContext(None)
}
