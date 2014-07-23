package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslAssign( lhs: GlslNode, rhs: GlslNode ) extends GlslNode {
  override def typ: GlslType = GlslType.Unit
}
