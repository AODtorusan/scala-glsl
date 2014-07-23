package be.angelcorp.glsl

import be.angelcorp.glsl.ast.{GlslNode, GlslStatement, GlslSymbol, GlslUnknownSymbol}
import be.angelcorp.glsl.impl._
import be.angelcorp.glsl.util.GlslContext
import org.slf4j.LoggerFactory

import scala.annotation.StaticAnnotation
import scala.collection.mutable
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

class Glsl extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro Glsl.impl
}

object Glsl {
  private val logger = LoggerFactory.getLogger( getClass )

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = try {
    import c.universe._
    def showResult( res: c.Tree ) {
      val code = try {
        showCode(res)
      } catch {
        case e: StackOverflowError => "<too complex, StackOverflowError in showCode>"
      }
      logger.info("Generated scala classes: \n" + code )
    }

    val result = {
      logger.info("--------------------------------------------------------------------------------")
      logger.info("--------------------------------------------------------------------------------")
      annottees.map(_.tree).toList match {
        case q"object $name extends ..$parents { ..$body }" :: rest =>
          logger.info(s" Converting object $name to glsl code")

          val res = q"""
            @be.angelcorp.glsl.${TypeName("GlslCode")}()
            object ${name.toTermName} {
              ..${createGLSLCode(c)(name.toString, body, parents)}
            }
          """
          showResult( res )
          res
        case q"trait $name extends ..$parents { ..$body }" :: rest =>
          logger.info(s" Converting trait $name to glsl code")

          val newBody = (body: List[c.Tree]).map {
            case vd: ValDef    => ValDef( vd.mods, vd.name, vd.tpt, q"???" )
            case dd: DefDef    => DefDef( dd.mods, dd.name, dd.tparams, dd.vparamss, dd.tpt, q"???" )
            case cd: ClassDef  => q" type ${cd.name} = ${name.toTermName}.${cd.name}"
            case cd: ModuleDef => q" def ${cd.name.toTermName} = ${name.toTermName}.${cd.name.toTermName}"
            case _ => EmptyTree
          }

          val res = q"""
            trait ${name.toTypeName} extends ..$parents {
              ..$newBody
            }

            @be.angelcorp.glsl.${TypeName("GlslCode")}()
            object ${name.toTermName} {
              ..${createGLSLCode(c)(name.toString, body, parents)}
            }
          """
          showResult( res )
          res
      }
    }
    c.Expr[Any](result)
  } catch {
    case e: Throwable =>
      logger.error("Failed to generate GLSL code!", e)
      c.Expr[Any]( c.universe.EmptyTree )
  }

  def createGLSLCode(c: Context)(compilationUnitName: String, scalaSourceCode: List[c.Tree], parents: List[c.Tree]): List[c.Tree] = {
    import c.universe._

    val decompiler = new Decompiler[c.type](c, compilationUnitName)
    val generator  = new Compiler[c.type](c)
    val scalagen   = new ScalaGenerator[c.type](c)
    val simplfier  = new Simplifier[c.type](c)
    //val simplfier  = new DummySimplifier[c.type](c)
    val context    = new GlslContext(None)
    val scalaSymbols = mutable.ListBuffer[c.Tree]()

    val parentSources = parents.map( p => {
      logger.debug("--------------------------------------------------------------------------------")
      logger.debug(s"Processing parent: \n ${showCode(p)}")

      val glsl = decompiler.parentToGlsl( p, context )
      logger.debug(s"Glsl AST tree: \n $glsl ")

      val glslTree = simplfier.simplify(generator.serialize(glsl).toTree[c.type](c))
      logger.debug(s"Glsl source tree: \n $glslTree ")
      glslTree
    } )

    val bodyGlsl = scalaSourceCode.map {
      case code =>
        logger.debug("--------------------------------------------------------------------------------")
        logger.debug(s"Processing code: \n ${showCode(code)} \n ${showRaw(code)}")
        val glsl = decompiler.statement( decompiler.codeToGlsl(code, context) )
        logger.debug(s"Glsl AST tree: \n $glsl ")

        val glslTree = simplfier.simplify(generator.serialize(glsl).toTree[c.type](c))
        logger.debug(s"Glsl source tree: \n $glslTree ")

        def getSymbol( n: GlslNode ) = n match {
          case s: GlslSymbol => Some(s)
          case GlslStatement( s: GlslSymbol ) => Some(s)
          case _ => None
        }
        getSymbol(glsl) match {
          case Some( sym: GlslSymbol ) if !sym.isInstanceOf[GlslUnknownSymbol] =>
            scalaSymbols += scalagen.runtimeSymbolWithImpl(sym, glslTree)
            logger.info(s"Encoded symbol '$sym' into the classfile" )
          case _ =>
        }

        val generateSources = code match {
          case vd: ValOrDefDefApi => vd.mods.annotations.collectFirst { case q"new GlslSymbolOnly()" => false} getOrElse true
          case _ => true
        }
        if (generateSources) glslTree else q""" "" """
    }
    val source = simplfier.simplify( (parentSources ::: bodyGlsl).reduce((a, b) => q"$a + $b") )

    scalaSymbols += q""" def source: ${typeOf[String]} = $source """
    scalaSymbols.toList
  }

}
