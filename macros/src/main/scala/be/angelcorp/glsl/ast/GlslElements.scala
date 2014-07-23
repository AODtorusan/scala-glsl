package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslElements(nodes: List[GlslStatementLike]) extends GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}
