package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

case class GlslApplyBinary( lhs: GlslNode, function: GlslSymbol, shorthand: String, rhs: GlslNode ) extends GlslNode {
  override def typ: GlslType = function.typ
}

object GlslApplyBinary {

  val mappings = Map(
    "$eq$eq"        -> "==",
    "$bang$eq"      -> "!=",
    "$greater"      -> ">",
    "$greater$eq"   -> ">=",
    "$less"         -> "<",
    "$less$eq"      -> "<=",

    "$plus"         -> "+",
    "$plus$eq"      -> "+=",
    "$minus"        -> "-",
    "$minus$eq"     -> "-=",
    "$times"        -> "*",
    "$times$eq"     -> "*=",
    "$div"          -> "/",
    "$div$eq"       -> "/=",
    "$bar"          -> "|",
    "$bar$eq"       -> "|=",
    "$amp"          -> "&",
    "$amp$eq"       -> "&="
  )

}
