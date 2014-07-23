package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslApplyUnary( qualifier: GlslNode, func: String ) extends GlslNode {
  override def typ: GlslType = ???
}
