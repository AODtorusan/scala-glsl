package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslSelect( qualifier: GlslNode, name: GlslSymbolReference ) extends GlslNode {
  override def typ: GlslType = name.typ
}
