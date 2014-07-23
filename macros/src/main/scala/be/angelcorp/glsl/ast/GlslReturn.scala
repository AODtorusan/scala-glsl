package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslReturn( ret: GlslStatementLike ) extends GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}
