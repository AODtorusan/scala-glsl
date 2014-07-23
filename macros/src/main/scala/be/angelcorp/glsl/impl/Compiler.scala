package be.angelcorp.glsl.impl

import be.angelcorp.glsl._
import be.angelcorp.glsl.ast._
import be.angelcorp.glsl.util._
import org.slf4j.LoggerFactory

import scala.reflect.macros.whitebox.Context

class Compiler[C <: Context](val c: C) extends MacroUtils {
  private val logger = LoggerFactory.getLogger( getClass )
  import c.universe._

  val SEMI_COLON = ";"

  def serialize( node: GlslNode): GlslCodeString = serialize(node, true)

  def serialize( node: GlslNode, allowInfixNotation: Boolean ): GlslCodeString = {
    node match {
      case e: GlslEmpty =>
        GlslCodeString()

      case GlslMacro(str) =>
        GlslCodeString( str )

      case GlslStatement( code ) =>
        serialize(code) append ";"

      case GlslUnknownSymbol(name) =>
        GlslCodeString(name)

      case func: GlslFunction =>
        val code = GlslCodeString()
        func.implementation match {
          case Some(GlslBlock(rhs: GlslEmpty)) =>
            code += declaration(func) + SEMI_COLON
          case Some(rhs) =>
            code += declaration(func) + " "
            code appendNoIndent serialize(rhs)
          case _ =>
            code += declaration(func) + SEMI_COLON
        }

      case GlslSymbolReference(variable) =>
        GlslCodeString( reference(variable) )

      case variable: GlslVariable =>
        val code = GlslCodeString()
        variable.initializer match {
          case Some(GlslStatement(rhs: GlslEmpty)) =>
            code += declaration(variable) + SEMI_COLON
          case Some(rhs) =>
            code += declaration(variable) + " = "
            code appendNoIndent serialize(rhs)
          case _ =>
            code += declaration(variable) + SEMI_COLON
        }

      case GlslApply( fun, args ) =>
        serialize( fun ) appendAll ( args.map( a => serialize(a) ), "(", ", ", ")" )

      case GlslArrayAccess( v, arg ) =>
        serialize(v) append "[" append serialize( arg ) append "]"

      case GlslApplyUnary( qualifier, func ) =>
        GlslCodeString( func + " " ) append serialize(qualifier)

      case GlslApplyBinary( lhs, f , op, rhs ) =>
        if (allowInfixNotation)
          serialize(lhs) append s" $op " append serialize(rhs, allowInfixNotation = false)
        else
          GlslCodeString("( ") append serialize(lhs) append s" $op " append serialize(rhs) append " )"

      case GlslArrayConstructor( args, typ ) =>
        GlslCodeString( s"${glslType(typ.subtyp)}[${args.length}]" ).appendAll( args.map(a => serialize(a)), "(", ", ", ")" )

      case GlslElements( expressions ) =>
        val code = GlslCodeString()
        for (expr <- expressions)
          code += serialize( expr )
        code

      case GlslBlock( expressions ) =>
        val code = GlslCodeString("{")
        code.tabIn()
        for (expr <- expressions)
          code += serialize( expr )
        code.tabOut()
        code += "}"

      case GlslConstant(value, _) =>
        GlslCodeString( value )

      case GlslReturn( ret ) =>
        GlslCodeString( "return " ) append serialize(ret)

      case GlslAssign( lhs, rhs ) =>
        GlslCodeString() append serialize(lhs) append " = " append serialize(rhs)

      case GlslExternalCode( tree ) =>
        GlslCodeString().add(c)(tree.asInstanceOf[c.Tree])

      case s: GlslStruct =>
        val code = GlslCodeString( declaration(s) + " {" )
        code.tabIn()
        s.vars.foreach( v => code += declaration(v) + SEMI_COLON )
        code.tabOut()
        code += "}"

      case ib: GlslInterfaceBlock =>
        val sizer     = ib.size match {
          case Some(size) if size > 0 => s"[$size]"
          case Some( _ ) => "[]"
          case None => ""
        }
        val code = GlslCodeString( declaration(ib) + " {" )
        code.tabIn()
        ib.vars.foreach( v => code += declaration(v) + SEMI_COLON )
        code.tabOut()
        code += s"} ${ib.name}$sizer"

      case GlslSelect( qualifier, name ) =>
        serialize(qualifier, allowInfixNotation = false) append "." + serialize(name)

      case GlslSwitch( sym, entries ) =>
        val code = GlslCodeString( s"switch (${reference(sym)}) {" )
        code.tabIn()
        for ( GlslSwitchEntry(cases, rhs) <- entries ) {
          for (c <- cases) {
            c match {
              case "#default#" => code += "default:"
              case _           => code += s"case $c:"
            }
          }
          code.tabIn()
          code += serialize( rhs )
          code += "break;"
          code.tabOut()
        }
        code.tabOut()
        code += "}"

      case GlslIf( cond, trueNode, optionElseNode ) =>
        val code = GlslCodeString( "if (" ) append serialize(cond) append ")"
        code appendNoIndent serialize(trueNode)
        optionElseNode match {
          case Some( elseNode ) =>
            code append " else "
            code appendNoIndent serialize(elseNode)
          case _ =>
        }
        code

      case GlslRangeFor( variable, from, end, step, func, isTo ) =>
        val isIncrementing = step.asInstanceOf[GlslConstant].value.toInt >= 0
        val op = isTo match {
          case true if isIncrementing => "<="
          case true => ">="
          case false if isIncrementing => "<"
          case _ => ">"
        }
        val code = GlslCodeString( "for (" + declaration(variable) + "  = " ) append serialize(from)
        code append s"; ${reference(variable)} $op " append serialize(end)
        code append s"; ${reference(variable)} += " append serialize(step) append ") "
        code appendNoIndent serialize(func)

      case cde =>
        throw new Exception( s"Cannot convert '$cde' to glsl code!"  )
    }
  }

  def reference( symbol: GlslSymbol): String =
    symbol.name

  def declaration( symbol: GlslSymbol ): String = {
    symbol match {
      case GlslUnknownSymbol( name ) =>
        name
      case GlslFunction( name, typ, params, context, _, mods ) =>
        val modifiers = mods.mkString(" ") + (if (mods.nonEmpty) " " else "")
        modifiers + glslType(typ) + " " + name + params.map( p => declaration(p) ).mkString("(", ", ", ")")
      case GlslVariable(name, typ, context, init, mods) =>
        val modifiers = mods.mkString(" ") + (if (mods.nonEmpty) " " else "")
        val typString = typ match {
          case a: ArrayType  =>
            val size = init.flatMap( rhs => findArraySize(rhs) ).map( _.toString ).getOrElse( "" )
            glslType(a.subtyp) + "[" + size + "]"
          case t =>
            glslType(t)

        }
        modifiers + typString + " " + name
      case GlslStruct(name, _) =>
        s"struct $name"
      case ib: GlslInterfaceBlock =>
        val modifiers = if (ib.mods.isEmpty) "" else ib.mods.mkString( " " ) + " "
        modifiers + ib.interfaceTypeName
    }
  }

  def findArraySize( node: GlslStatementLike ): Option[Int] = node match {
    case GlslStatement( GlslArrayConstructor(args, _) ) => Some(args.length)
    case GlslStatement( GlslArraySizeHint(size, _) ) => Some( size )
    case _ =>
      logger.info( "Cannot determine array size from " + showRaw(node) )
      None
  }

  def glslType[T](t: GlslType): String = {
    import c.universe._
//    val tp = try{ Some(t.toType[c.type](c)) } catch { case e: Throwable => None }
//    tp match {
//      case Some(tpe) if tpe == typeOf[Unit]     => "void"
//      case Some(tpe) if tpe == typeOf[Boolean]  => "bool"
//      case Some(tpe) if tpe == typeOf[Int]      => "int"
//      case Some(tpe) if tpe == typeOf[Float]    => "float"
//      case Some(tpe) if tpe == typeOf[Double]   => "double"
//      case _ => t.simpleName
//    }
    t.simpleName match {
      case "Unit"     => "void"
      case "Boolean"  => "bool"
      case "Int"      => "int"
      case "Float"    => "float"
      case "Double"   => "double"
      case typ        => typ
    }
  }

}
