package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslRangeFor( variable: GlslVariable, from: GlslNode, end: GlslNode, step: GlslNode, func: GlslBlock, isTo: Boolean ) extends GlslNode {
  override def typ: GlslType = GlslType.Unit
}
