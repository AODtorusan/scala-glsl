package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslMacro( macroString: String ) extends GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}
