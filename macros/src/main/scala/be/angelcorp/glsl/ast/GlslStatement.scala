package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslStatement( code: GlslNode ) extends GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}
