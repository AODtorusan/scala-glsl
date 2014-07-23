package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslBlock( expressions: List[GlslNode] ) extends GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}
