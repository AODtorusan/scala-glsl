package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslSymbolReference(variable: GlslSymbol) extends GlslNode {
  override def typ: GlslType = variable.typ
}
