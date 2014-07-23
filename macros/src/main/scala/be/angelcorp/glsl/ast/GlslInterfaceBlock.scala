
package be.angelcorp.glsl.ast



import be.angelcorp.glsl.util.{GlslContext, ArrayType, SimpleType}

import scala.reflect.macros.whitebox.Context

class GlslInterfaceBlock(val name: String, val vars: List[GlslVariable], val context: GlslContext, val mods: List[String] = Nil, val size: Option[Int] = None) extends GlslSymbol {

  val interfaceTypeName = name.head.toUpper + name.drop(1) + "InterfaceBlock"

  val typ =  {
    val baseType = new SimpleType(interfaceTypeName, context)
    if (size.isDefined)
      new ArrayType(baseType)
    else
      baseType
  }

  val asVariable: GlslVariable = new GlslVariable( name, typ, context, None )

  override def toString: String = s"GlslInterfaceBlock($name, $vars, $context, $mods, $size)"

}

object GlslInterfaceBlock {

  def unapply( m: GlslInterfaceBlock ): Option[(String, List[GlslVariable], List[String], Option[Int])] =
    Some( (m.name, m.vars, m.mods, m.size) )

}
