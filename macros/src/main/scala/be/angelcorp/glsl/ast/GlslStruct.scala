package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.{GlslContext, SimpleType}

import scala.reflect.macros.whitebox.Context

class GlslStruct(val name: String, val vars: List[GlslVariable], val context: GlslContext) extends GlslSymbol {

  val typ = new SimpleType(name, context)

}
object GlslStruct {

  def unapply( s: GlslStruct ): Option[(String, List[GlslVariable])] =
    Some( s.name -> s.vars  )

}
