package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.{GlslContext, GlslType}

case class GlslFunction(name: String, typ: GlslType, parameters: List[GlslVariable], context: GlslContext, implementation: Option[GlslBlock], modifiers: List[String] = Nil) extends GlslSymbol with GlslStatementLike