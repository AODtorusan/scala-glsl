package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslIf( condition: GlslNode, success: GlslBlock, failure: Option[GlslBlock] = None ) extends GlslNode {
  override def typ: GlslType = GlslType.Unit
}
