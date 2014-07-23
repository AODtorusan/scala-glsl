package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslApply( fun: GlslSymbolReference, args: List[GlslNode] ) extends GlslNode {

  override def typ: GlslType = fun.typ

}
