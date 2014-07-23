package be.angelcorp.glsl

import scala.annotation.StaticAnnotation
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

/**
 * Tags the class/trait/object as a mockup.
 *
 * This will cause the scala code NOT to get compiled.
 * It is used to help scala source parsing IDE's out a bit.
 *
 */
class GlslMockup extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro Glsl.impl
}

object GlslMockup {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = c.Expr( c.universe.EmptyTree )

}