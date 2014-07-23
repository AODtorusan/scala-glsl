package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslSwitch( symb: GlslSymbol, entries: List[GlslSwitchEntry] ) extends GlslNode with GlslStatementLike {
  override def typ: GlslType = GlslType.Unit
}

case class GlslSwitchEntry( cases: List[String], rhs: GlslNode )
