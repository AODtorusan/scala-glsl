package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.GlslType

import scala.reflect.macros.whitebox.Context

class GlslExternalCode[C <: Context](val c: C)(callTree: Any ) extends GlslNode with GlslStatementLike {
  val tree: c.Tree = callTree.asInstanceOf[c.Tree]
  lazy val typ: GlslType = GlslType(c)(c.typecheck(tree).tpe)
}

object GlslExternalCode {

  def unapply( ext: GlslExternalCode[_] ) = Some(ext.tree)

}
