package be.angelcorp.glsl.impl

import be.angelcorp.glsl._
import be.angelcorp.glsl.ast._
import be.angelcorp.glsl.util.GlslType
import org.slf4j.LoggerFactory

import scala.reflect.macros.whitebox.Context

/**
 * Generates scala symbols into the object class files to retain glsl information during runtime (and code assistance)
 */
class ScalaGenerator[C <: Context](val c: C) extends MacroUtils {
  private val logger = LoggerFactory.getLogger( getClass )
  import c.universe._

  val serializer = new Compiler[c.type](c)

  def runtimeSymbol( sym: GlslSymbol ): c.Tree =
    runtimeSymbolWithImpl( sym, serializer.serialize(sym).toTree[c.type](c) )

  def runtimeSymbolWithImpl( sym: GlslSymbol, impl: c.Tree ): c.Tree = {
    import c.universe._
    try {
      val res = sym match {
        case s:  GlslStruct         => generateStruct( s, impl )
        case ib: GlslInterfaceBlock => generateInterfaceBlock( ib, impl )
        case f:  GlslFunction       => generateFunction( f, impl )
        case _                      => generateSymbol( sym, impl )
      }
      //logger.trace( "Generated runtime symbol: " + showCode(res) )
      res
    } catch {
      case e: Throwable =>
        logger.warn("Failed to generate runtime symbol for: " + sym, e)
        EmptyTree
    }
  }

  def scalaClassSymbolType( typ: GlslType ) =
    typ.toTypeTree(c)

  def generateFunction( f: GlslFunction, impl: c.Tree ): c.Tree = {
    val decl  = serializer.declaration( f )
    q"""
      @be.angelcorp.glsl.util.GlslRuntimeSymbolAnnotation( $decl, $impl )
      def ${TermName(f.name)}: (..${f.parameters.map( p => scalaClassSymbolType(p.typ) )}) => ${scalaClassSymbolType(f.typ)} = ???
    """
  }

  def generateStruct( s: GlslStruct, impl: c.Tree ): c.Tree = {
    val decl = serializer.declaration( s )
    val args = s.vars.map( p => ValDef(Modifiers(), TermName(p.name), Ident(p.typ.toTypeName[c.type](c)), EmptyTree) )
    q"""
      @be.angelcorp.glsl.util.GlslRuntimeSymbolAnnotation( $decl, $impl )
      case class ${TypeName(s.name)}( ..$args )
    """
  }

  def generateInterfaceBlock( ib: GlslInterfaceBlock, impl: c.Tree ): c.Tree = {
    val decl = serializer.declaration( ib )
    val funcs = ib.vars.map( p => ValDef(Modifiers(), TermName(p.name), p.typ.toTypeTree[c.type](c), q"???") )
    q"""
      @be.angelcorp.glsl.util.GlslRuntimeSymbolAnnotation( $decl, $impl )
      object ${TermName(ib.name)} { ..$funcs }
    """
  }

  def generateSymbol( sym: GlslSymbol, impl: c.Tree ): c.Tree = {
    val decl  = serializer.declaration( sym )
    val rtype = scalaClassSymbolType(sym.typ)
    q"""
      @be.angelcorp.glsl.util.GlslRuntimeSymbolAnnotation( $decl, $impl )
      def ${TermName(sym.name)} : $rtype = ???
    """
  }

}
