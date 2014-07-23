package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.{ArrayType, GlslType}

case class GlslArrayAccess( variable: GlslNode, arg: GlslNode ) extends GlslNode {

  override def typ: GlslType = variable.typ.asInstanceOf[ArrayType].subtyp

}
