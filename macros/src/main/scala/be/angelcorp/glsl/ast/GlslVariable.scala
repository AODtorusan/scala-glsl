package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.{GlslContext, GlslType}


case class GlslVariable(name: String, typ: GlslType, context: GlslContext, initializer: Option[GlslStatementLike], modifiers: List[String] = Nil) extends GlslSymbol with GlslStatementLike
