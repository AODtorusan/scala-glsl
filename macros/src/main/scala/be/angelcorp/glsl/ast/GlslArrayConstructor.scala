package be.angelcorp.glsl.ast

import be.angelcorp.glsl.util.ArrayType

case class GlslArrayConstructor( args: List[GlslNode], typ: ArrayType ) extends GlslNode
