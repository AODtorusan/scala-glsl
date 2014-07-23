package be.angelcorp.glsl.util

import scala.reflect.macros.whitebox.Context

trait GlslType {

  def simpleName: String
  def location: String

  def qualifiedName = {
    if (location.isEmpty) simpleName else location + "." + simpleName
  }

  def toTypeName[C <: Context](c: C): c.universe.TypeName
  def toTypeTree[C <: Context](c: C): c.universe.Tree
  def toType[C <: Context](c: C): c.Type = {
    import c.universe._
    val typ = try {
      // Required to make thinks like Unit and so properly identify ....
      // TODO: Get rid of this check, and only check with $tree
      c.typecheck(q"(7.asInstanceOf[${TypeName(simpleName)}])").tpe
    } catch {
      case e: Throwable =>
        val tree = toTypeTree(c)
        c.typecheck(q"(7.asInstanceOf[$tree])").tpe
    }
    typ
  }

  def context: GlslContext

  def isArray: Boolean

  override def toString: String = simpleName

}

object GlslType {

  def apply[C <: Context]( c: C)(typ: c.Type): GlslType = {
    if ( typ.typeSymbol.name.toString == "Array" ) {
      val subtyp = GlslType(c)(typ.typeParams.head.typeSignature)
      new ArrayType( subtyp )
    } else {
      new GlslScalaType[C](c, typ )
    }
  }

  object Unit extends SimpleType("Unit", new GlslContext(None), "")

}

class GlslScalaType[C <: Context](c: C, typContainer: Any ) extends GlslType {
  private val typ = typContainer.asInstanceOf[c.Type]
  override def simpleName: String = typ.typeSymbol.name.toString
  override def location: String = {
    val loc = qualifiedName.replace(simpleName, "")
    if (loc.endsWith(".")) loc.substring(0, loc.length - 1) else loc
  }
  override def toType[C <: Context](c: C): c.Type = typ.asInstanceOf[c.Type]
  override def toTypeName[C <: Context](c: C): c.TypeName = typ.typeSymbol.name.toTypeName.asInstanceOf[c.TypeName]
  override def toTypeTree[C <: Context](c: C): c.Tree = {
    import c.universe._
    val names = qualifiedName.split('.').map( n => TermName(n) )
    val tree = names.drop( 1 ).dropRight(1).foldLeft( Ident(names.head): c.Tree )( (tree, name) => Select( tree, name ) )
    if (names.length > 1) Select( tree, names.last.toTypeName ) else Ident(names.head.toTypeName)
  }
  override def isArray: Boolean = false
  override def context: GlslContext = new GlslContext(None) // TODO
  override lazy val qualifiedName: String = typ.typeSymbol.fullName
}

/**
 * Fall-back type when the given scala type is not known to the scala compiler.
 */
class SimpleType( val name: String, val context: GlslContext, val location: String = "" ) extends GlslType {
  def simpleName = name
  def toTypeName[C <: Context](c: C): c.TypeName = c.universe.TypeName( name )
  def toTypeTree[C <: Context](c: C): c.Tree = {
    import c.universe._
    val names = qualifiedName.split('.').map( n => TermName(n) )
    val tree = names.drop( 1 ).dropRight(1).foldLeft( Ident(names.head): c.Tree )( (tree, name) => Select( tree, name ) )
    if (names.length > 1) Select( tree, names.last.toTypeName ) else Ident(names.head.toTypeName)
  }
  override def isArray: Boolean = false
}

object SimpleType {
  def unapply( typ: SimpleType ): Option[String] = Some( typ.name )
}

/**
 * Special type for arrays
 * All other GlslType classes should represent arrays.
 */
class ArrayType( val subtyp: GlslType ) extends GlslType {

  def simpleName = subtyp.simpleName + "[]"
  def location = subtyp.location

  def toTypeName[C <: Context](c: C): c.TypeName =
    c.universe.TypeName("Array")

  def toTypeTree[C <: Context](c: C): c.Tree = {
    import c.universe._
    tq"Array[${subtyp.toTypeTree(c)}]"
  }

  override def isArray: Boolean = true

  override def context = ArrayContext

  object ArrayContext extends GlslContext(None)

}