package be.angelcorp.glsl.util

import scala.annotation.StaticAnnotation

object GlslRuntimeSymbol {

  implicit def resolve[T]( s: GlslRuntimeSymbol[T] ): T = ???

}

class GlslRuntimeSymbolAnnotation( val declaration: String, implementation: String ) extends StaticAnnotation


trait GlslRuntimeSymbol[T] {

  def declaration:    String
  def implementation: String

}